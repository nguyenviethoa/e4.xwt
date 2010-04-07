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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.bindings.keys.KeyBindingDispatcher;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MCommand;
import org.eclipse.e4.ui.model.application.MCommandParameter;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.swt.internal.CSSStylingSupport;
import org.eclipse.e4.workbench.ui.IPresentationEngine;
import org.eclipse.e4.workbench.ui.IWorkbench;
import org.eclipse.e4.workbench.ui.internal.Activator;
import org.eclipse.e4.workbench.ui.internal.E4CommandProcessor;
import org.eclipse.e4.workbench.ui.internal.E4Workbench;
import org.eclipse.e4.workbench.ui.internal.ModelExtensionProcessor;
import org.eclipse.e4.workbench.ui.internal.Parameter;
import org.eclipse.e4.workbench.ui.internal.Policy;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4WorkbenchProxy {

	private IEclipseContext appContext;
	private IPresentationEngine renderer;
	private Object root;
	private E4UIEventPublisher globalDistahcher;
	private MApplicationElement uiRoot;

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
		EList<MCommand> commands = appElement.getCommands();
		for (MCommand cmd : commands) {
			IParameter[] parms = null;
			String id = cmd.getId();
			String name = cmd.getCommandName();
			EList<MCommandParameter> modelParms = cmd.getParameters();
			if (modelParms != null && !modelParms.isEmpty()) {
				ArrayList<Parameter> parmList = new ArrayList<Parameter>();
				for (MCommandParameter cmdParm : modelParms) {
					parmList.add(new Parameter(cmdParm.getId(), cmdParm
							.getName(), null, null, cmdParm.isOptional()));
				}
				parms = parmList.toArray(new Parameter[parmList.size()]);
			}
			cs.defineCommand(id, name, null, cat, parms);
		}

		// Add model items described in the model extension point
		ModelExtensionProcessor extProcessor = new ModelExtensionProcessor(
				appElement);
		extProcessor.addModelExtensions();

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

				String cssURI = (String) appContext
						.get(E4Workbench.CSS_URI_ARG);
				if (cssURI != null) {
					String cssResourcesURI = (String) appContext
							.get(E4Workbench.CSS_RESOURCE_URI_ARG);
					CSSStylingSupport.initializeStyling(display, cssURI,
							cssResourcesURI, appContext);
				} else {
					initializeNullStyling(appContext);
				}

				// Register an SWT resource handler
				// appContext.set(IResourceUtiltities.class.getName(), new
				// ResourceUtility(Activator.getDefault()
				// .getBundleAdmin()));

				// set up the keybinding manager
				try {
					KeyBindingDispatcher dispatcher = (KeyBindingDispatcher) ContextInjectionFactory
							.make(KeyBindingDispatcher.class, appContext);
					org.eclipse.swt.widgets.Listener listener = dispatcher
							.getKeyDownFilter();
					display.addFilter(SWT.KeyDown, listener);
					display.addFilter(SWT.Traverse, listener);
				} catch (InvocationTargetException e) {
					Logger logger = (Logger) appContext.get(Logger.class
							.getName());
					if (logger != null) {
						logger.error(e);
					}
				} catch (InstantiationException e) {
					Logger logger = (Logger) appContext.get(Logger.class
							.getName());
					if (logger != null) {
						logger.error(e);
					}
				}

				// if (device == null || device.isDisposed()) {
				// device = new Shell(display, SWT.NO_TRIM);
				// }

				// device.setSize(0, 0);
				// device.setVisible(true);
				// device.open();
				int x = 0, y = 0, width = 500, height = 500;
				if (renderer != null) {
					if (uiRoot instanceof MApplication) {
						EList<MWindow> children = ((MApplication) uiRoot)
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
