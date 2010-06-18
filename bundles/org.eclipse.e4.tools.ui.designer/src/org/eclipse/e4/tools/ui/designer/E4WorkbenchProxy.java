/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.ui.internal.workbench.swt.ResourceUtility;
import org.eclipse.e4.ui.workbench.IResourceUtilities;

import org.eclipse.e4.ui.internal.workbench.ToolBarContributionHandler;
import org.eclipse.e4.ui.internal.workbench.TrimContributionHandler;
import org.eclipse.e4.ui.workbench.swt.modeling.MenuServiceFilter;

import org.eclipse.e4.ui.css.swt.engine.CSSSWTEngineImpl;

import org.eclipse.e4.ui.css.swt.theme.IThemeManager;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.css.core.util.impl.resources.OSGiResourceLocator;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.workbench.swt.WorkbenchSWTActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.eclipse.e4.ui.internal.workbench.ModelAssembler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.bindings.keys.KeyBindingDispatcher;
import org.eclipse.e4.ui.internal.workbench.Activator;
import org.eclipse.e4.ui.internal.workbench.E4CommandProcessor;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.Parameter;
import org.eclipse.e4.ui.internal.workbench.Policy;
import org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Mainly comes from PartRenderingEngine
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4WorkbenchProxy {

	private IEclipseContext appContext;
	private IPresentationEngine renderer;
	private Object root;
	private E4UIEventPublisher globalDistahcher;
	private MApplicationElement uiRoot;
	private MenuServiceFilter menuServiceFilter;
	org.eclipse.swt.widgets.Listener keyListener;

	public E4WorkbenchProxy(MApplicationElement uiRoot,
			IEclipseContext applicationContext) {
		this.uiRoot = uiRoot;
		this.appContext = applicationContext;
		appContext.set(IWorkbench.class.getName(), this);

		if (uiRoot instanceof MApplication) {
			init((MApplication) uiRoot);
		}

		// Hook the global notifications
		((Notifier) uiRoot).eAdapters().add(
				globalDistahcher = new E4UIEventPublisher(appContext));
	}

	private void init(MApplication appElement) {
		Activator.trace(Policy.DEBUG_WORKBENCH, "init() workbench", null); //$NON-NLS-1$

		// fill in commands
		Activator.trace(Policy.DEBUG_CMDS,
				"Initialize service from model", null); //$NON-NLS-1$
		ECommandService cs = (ECommandService) appContext
				.get(ECommandService.class.getName());
		Category cat = cs.defineCategory(MApplication.class.getName(),
				"Application Category", null); //$NON-NLS-1$
		List<MCommand> commands = appElement.getCommands();
		for (MCommand cmd : commands) {
			IParameter[] parms = null;
			String id = cmd.getElementId();
			String name = cmd.getCommandName();
			List<MCommandParameter> modelParms = cmd.getParameters();
			if (modelParms != null && !modelParms.isEmpty()) {
				ArrayList<Parameter> parmList = new ArrayList<Parameter>();
				for (MCommandParameter cmdParm : modelParms) {
					parmList.add(new Parameter(cmdParm.getElementId(), cmdParm
							.getName(), null, null, cmdParm.isOptional()));
				}
				parms = parmList.toArray(new Parameter[parmList.size()]);
			}
			cs.defineCommand(id, name, null, cat, parms);
		}

//FIXME Yves to check if this is correct
		IEclipseContext context = appContext.createChild();
		context.set(MApplication.class, appElement);
		ModelAssembler contribProcessor = ContextInjectionFactory.make(ModelAssembler.class,
				context);
		contribProcessor.processModel();
		context.dispose();
		
//		// Add model items described in the model extension point
//		ModelExtensionProcessor extProcessor = new ModelExtensionProcessor(
//				appElement);
//		extProcessor.addModelExtensions();

		// Do a top level processHierarchy for the application?
		E4Workbench.processHierarchy(appElement);
		
		//init commands and bindings here.
		E4CommandProcessor.processCommands(appContext, appElement.getCommands());
		E4CommandProcessor.processBindings(appContext, appElement);
	}

	public void createAndRunUI() {
		final Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {

			public void run() {
				initializeStyling(display, appContext);

				// Register an SWT resource handler
				appContext.set(IResourceUtilities.class.getName(),
						new ResourceUtility());

				// Has someone already created one ?
				renderer = (IPresentationEngine) appContext
						.get(IPresentationEngine.class.getName());
				if (renderer == null) {
					String presentationURI = (String) appContext
							.get(E4Workbench.PRESENTATION_URI_ARG);
					if (presentationURI != null) {
						IContributionFactory factory = (IContributionFactory) appContext
								.get(IContributionFactory.class.getName());
						renderer = (IPresentationEngine) factory.create(
								presentationURI, appContext);
						appContext.set(IPresentationEngine.class.getName(),
								renderer);
					}
					if (renderer == null) {
						Logger logger = (Logger) appContext.get(Logger.class
								.getName());
						logger
								.error("Failed to create the presentation engine for URI: " + presentationURI); //$NON-NLS-1$
					}
				}

//				String cssURI = (String) appContext
//						.get(E4Workbench.CSS_URI_ARG);
//				if (cssURI != null) {
//					String cssResourcesURI = (String) appContext
//							.get(E4Workbench.CSS_RESOURCE_URI_ARG);
//					PartRenderingEngine.initializeStyling(display, appContext);
//				} else {
//					initializeNullStyling(appContext);
//				}

				// Register an SWT resource handler
				// appContext.set(IResourceUtilities.class.getName(), new
				// ResourceUtility(Activator.getDefault()
				// .getBundleAdmin()));

				// set up the keybinding manager
				KeyBindingDispatcher dispatcher = (KeyBindingDispatcher) ContextInjectionFactory
						.make(KeyBindingDispatcher.class, appContext);
				keyListener = dispatcher.getKeyDownFilter();
				display.addFilter(SWT.KeyDown, keyListener);
				display.addFilter(SWT.Traverse, keyListener);

				ContextInjectionFactory.make(TrimContributionHandler.class,
						appContext);
				ContextInjectionFactory.make(ToolBarContributionHandler.class,
						appContext);
				menuServiceFilter = ContextInjectionFactory.make(
						MenuServiceFilter.class, appContext);
				display.addFilter(SWT.Show, menuServiceFilter);
				display.addFilter(SWT.Hide, menuServiceFilter);
				display.addFilter(SWT.Dispose, menuServiceFilter);
				appContext.set(MenuServiceFilter.class, menuServiceFilter);

				// if (device == null || device.isDisposed()) {
				// device = new Shell(display, SWT.NO_TRIM);
				// }

				// device.setSize(0, 0);
				// device.setVisible(true);
				// device.open();
				int x = 0, y = 0, width = 500, height = 500;
				if (renderer != null) {
					if (uiRoot instanceof MApplication) {
						List<MWindow> children = ((MApplication) uiRoot)
								.getChildren();
						for (MWindow mWindow : children) {
							root = renderer.createGui(mWindow);
							x = mWindow.getX();
							y = mWindow.getY();
							// width = mWindow.getWidth();
							// height = mWindow.getHeight();
						}
					}
				}
				if (root != null && root instanceof Shell) {
					Shell shell = ((Shell) root);
					if (x < 0) {
						x = 0;
					}
					if (y < 0) {
						y = 0;
					}
					shell.setLocation(x, y);
					if (width > 0 && height > 0) {
						shell.setSize(width, height);
					}
					shell.moveBelow(null);
					shell.setVisible(false);
				}
			}
		});
	}
	
	public static void initializeStyling(Display display,
			IEclipseContext appContext) {
		String cssTheme = (String) appContext.get(E4Application.THEME_ID);
		String cssURI = (String) appContext.get(E4Workbench.CSS_URI_ARG);

		if (cssTheme != null) {
			String cssResourcesURI = (String) appContext
					.get(E4Workbench.CSS_RESOURCE_URI_ARG);

			Bundle bundle = WorkbenchSWTActivator.getDefault().getBundle();
			BundleContext context = bundle.getBundleContext();
			ServiceReference ref = context
					.getServiceReference(org.eclipse.ui.themes.IThemeManager.class.getName());
			IThemeManager mgr = (IThemeManager) context.getService(ref);
			final IThemeEngine engine = mgr.getEngineForDisplay(display);

			// Store the app context
			display.setData("org.eclipse.e4.ui.css.context", appContext); //$NON-NLS-1$

			// Create the OSGi resource locator
			if (cssResourcesURI != null) {
				// TODO: Should this be set through an extension as well?
				engine.registerResourceLocator(new OSGiResourceLocator(
						cssResourcesURI));
			}

			engine.restore(cssTheme);
			// TODO Should we create an empty default theme?

			appContext.set(IThemeEngine.class.getName(), engine);

			appContext.set(IStylingEngine.SERVICE_NAME, new IStylingEngine() {
				public void setClassname(Object widget, String classname) {
					((Widget) widget).setData(
							"org.eclipse.e4.ui.css.CssClassName", classname); //$NON-NLS-1$
					engine.applyStyles((Widget) widget, true);
				}

				public void setId(Object widget, String id) {
					((Widget) widget).setData("org.eclipse.e4.ui.css.id", id); //$NON-NLS-1$
					engine.applyStyles((Widget) widget, true);
				}

				public void style(Object widget) {
					engine.applyStyles((Widget) widget, true);
				}

			});
		} else if (cssURI != null) {
			String cssResourcesURI = (String) appContext
					.get(E4Workbench.CSS_RESOURCE_URI_ARG);
			final CSSSWTEngineImpl engine = new CSSSWTEngineImpl(display, true);
			if (cssResourcesURI != null) {
				engine.getResourcesLocatorManager().registerResourceLocator(
						new OSGiResourceLocator(cssResourcesURI.toString()));
			}
			appContext.set(IStylingEngine.SERVICE_NAME, new IStylingEngine() {
				public void setClassname(Object widget, String classname) {
					((Widget) widget).setData(
							"org.eclipse.e4.ui.css.CssClassName", classname); //$NON-NLS-1$
					engine.applyStyles((Widget) widget, true);
				}

				public void setId(Object widget, String id) {
					((Widget) widget).setData("org.eclipse.e4.ui.css.id", id); //$NON-NLS-1$
					engine.applyStyles((Widget) widget, true);
				}

				public void style(Object widget) {
					engine.applyStyles((Widget) widget, true);
				}

			});

			URL url;
			InputStream stream = null;
			try {
				url = FileLocator.resolve(new URL(cssURI));
				stream = url.openStream();
				engine.parseStyleSheet(stream);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			Shell[] shells = display.getShells();
			for (Shell s : shells) {
				try {
					s.setRedraw(false);
					s.reskin(SWT.ALL);
					engine.applyStyles(s, true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					s.setRedraw(true);
				}
			}
		}

	}

	public Object create(MUIElement element) {
		if (renderer != null) {
			return renderer.createGui(element);
		}
		return null;
	}

	public void remove(MUIElement element) {
		if (renderer != null) {
			renderer.removeGui(element);
		}
	}

	/*
	 * There are situations where this is called more than once until we know
	 * why this is needed we should make this safe for multiple calls
	 */
	private void cleanUp() {
		if (menuServiceFilter != null) {
			Display display = Display.getDefault();
			if (!display.isDisposed()) {
				display.removeFilter(SWT.Show, menuServiceFilter);
				display.removeFilter(SWT.Hide, menuServiceFilter);
				display.removeFilter(SWT.Dispose, menuServiceFilter);
				menuServiceFilter.dispose();
				menuServiceFilter = null;
				appContext.remove(MenuServiceFilter.class);
			}
		}
		if (keyListener != null) {
			Display display = Display.getDefault();
			if (!display.isDisposed()) {
				display.removeFilter(SWT.KeyDown, keyListener);
				display.removeFilter(SWT.Traverse, keyListener);
				keyListener = null;
			}
		}
	}

	
	protected void initializeNullStyling(IEclipseContext appContext) {
		appContext.set(IStylingEngine.SERVICE_NAME, new IStylingEngine() {
			public void setClassname(Object widget, String classname) {
			}

			public void setId(Object widget, String id) {
			}

			public void style(Object widget) {
			}
		});
	}

	public E4UIEventPublisher getGlobalDistahcher() {
		return globalDistahcher;
	}

	public void dispose() {
		if (root != null && root instanceof Widget) {
			((Widget) root).dispose();
		}
		if (renderer != null) {
			renderer.stop();
		}
	}

	public Object getRoot() {
		return root;
	}

	public void reload() {
		if (root != null && root instanceof Widget) {
			((Widget) root).dispose();
		}
		if (renderer != null) {
			renderer.stop();
		}
		createAndRunUI();
	}
}
