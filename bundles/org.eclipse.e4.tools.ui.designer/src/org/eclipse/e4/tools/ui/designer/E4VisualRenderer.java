/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.core.commands.contexts.ContextManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.e4.core.services.IContributionFactory;
import org.eclipse.e4.core.services.Logger;
import org.eclipse.e4.core.services.context.EclipseContextFactory;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.core.services.context.spi.ContextInjectionFactory;
import org.eclipse.e4.core.services.context.spi.IContextConstants;
import org.eclipse.e4.core.services.context.spi.IEclipseContextStrategy;
import org.eclipse.e4.ui.internal.services.ActiveContextsFunction;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.swt.internal.PartRenderingEngine;
import org.eclipse.e4.workbench.ui.IExceptionHandler;
import org.eclipse.e4.workbench.ui.internal.ActiveChildLookupFunction;
import org.eclipse.e4.workbench.ui.internal.ActivePartLookupFunction;
import org.eclipse.e4.workbench.ui.internal.E4Workbench;
import org.eclipse.e4.workbench.ui.internal.ExceptionHandler;
import org.eclipse.e4.workbench.ui.internal.ReflectionContributionFactory;
import org.eclipse.e4.workbench.ui.internal.UISchedulerStrategy;
import org.eclipse.e4.workbench.ui.internal.WorkbenchLogger;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4VisualRenderer implements IVisualRenderer {

	MApplication appModel;
	IFile inputFile;
	private E4WorkbenchProxy workbench;

	public E4VisualRenderer(IFile inputFile, MApplication documentRoot) {
		this.inputFile = inputFile;
		this.appModel = documentRoot;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#createVisuals()
	 */
	public Result createVisuals() {

		IEclipseContext appContext = createDefaultContext();

		// Set the app's context after adding itself
		appContext.set(MApplication.class.getName(), appModel);
		appModel.setContext(appContext);

		// Parse out parameters from both the command line and/or the product
		// definition (if any) and put them in the context
		String xmiURI = getArgValue(E4Workbench.XMI_URI_ARG);
		appContext.set(E4Workbench.XMI_URI_ARG, xmiURI);
		String cssURI = getArgValue(E4Workbench.CSS_URI_ARG);
		appContext.set(E4Workbench.CSS_URI_ARG, cssURI);
		String cssResourcesURI = getArgValue(E4Workbench.CSS_RESOURCE_URI_ARG);
		appContext.set(E4Workbench.CSS_RESOURCE_URI_ARG, cssResourcesURI);

		// This is a default arg, if missing we use the default rendering engine
		String presentationURI = getArgValue(E4Workbench.PRESENTATION_URI_ARG);
		if (presentationURI == null) {
			presentationURI = PartRenderingEngine.engineURI;
			appContext.set(E4Workbench.PRESENTATION_URI_ARG, presentationURI);
		}

		workbench = new E4WorkbenchProxy(appContext);
		workbench.createRoot(appModel);
		return new Result(appModel.getWidget(), true);
	}

	private String getArgValue(String argName) {
		// Is it in the arg list ?
		if (argName == null || argName.length() == 0)
			return null;

		// No, if we're a product is it in the product's definition?
		IProduct product = Platform.getProduct();
		if (product != null) {
			return product.getProperty(argName);
		}

		return null;
	}

	public IEclipseContext createDefaultContext() {
		return createDefaultContext(UISchedulerStrategy.getInstance());
	}

	public IEclipseContext createDefaultContext(IEclipseContextStrategy strategy) {
		// FROM: WorkbenchApplication
		// parent of the global workbench context is an OSGi service
		// context that can provide OSGi services
		IProject project = inputFile.getProject();
		Bundle bundle = Platform.getBundle(project.getName());
		BundleContext bundleContext = bundle.getBundleContext();

		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
		final IEclipseContext appContext = EclipseContextFactory.create(serviceContext, strategy);
		appContext.set(IContextConstants.DEBUG_STRING, "WorkbenchAppContext"); //$NON-NLS-1$

		// FROM: Workbench#createWorkbenchContext
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		ExceptionHandler exceptionHandler = new ExceptionHandler();
		ReflectionContributionFactory contributionFactory = new ReflectionContributionFactory(registry);
		appContext.set(IContributionFactory.class.getName(), contributionFactory);

		appContext.set(Logger.class.getName(), ContextInjectionFactory.inject(new WorkbenchLogger(), appContext));
		appContext.set(IContextConstants.DEBUG_STRING, "WorkbenchContext"); //$NON-NLS-1$

		// setup for commands and handlers
		appContext.set(ContextManager.class.getName(), new ContextManager());

		// FROM: Workbench#createWorkbenchContext
		appContext.set(IServiceConstants.ACTIVE_CONTEXTS, new ActiveContextsFunction());
		appContext.set(IServiceConstants.ACTIVE_PART, new ActivePartLookupFunction());
		appContext.runAndTrack(new Runnable() {
			public void run() {
				Object o = appContext.get(IServiceConstants.ACTIVE_PART);
				if (o instanceof MPart) {
					appContext.set(IServiceConstants.ACTIVE_PART_ID, ((MPart) o).getId());
				}
			}

			/*
			 * For debugging purposes only
			 */
			@Override
			public String toString() {
				return IServiceConstants.ACTIVE_PART_ID;
			}
		});
		// EHandlerService comes from a ContextFunction
		// EContextService comes from a ContextFunction
		appContext.set(IExceptionHandler.class.getName(), exceptionHandler);
		appContext.set(IExtensionRegistry.class.getName(), registry);
		appContext.set(IServiceConstants.ACTIVE_SHELL, new ActiveChildLookupFunction(IServiceConstants.ACTIVE_SHELL,
				E4Workbench.LOCAL_ACTIVE_SHELL));

		// FROM: Workbench#initializeNullStyling
		appContext.set(IStylingEngine.SERVICE_NAME, new IStylingEngine() {
			public void setClassname(Object widget, String classname) {
			}

			public void setId(Object widget, String id) {
			}

			public void style(Object widget) {
			}
		});

		appContext.set(IExtensionRegistry.class.getName(), registry);
		appContext.set(IContributionFactory.class.getName(), contributionFactory);
		appContext.set(IEclipseContext.class.getName(), appContext);

		return appContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#refreshVisuals(java.lang.Object)
	 */
	public Result refreshVisuals(Object source) {
		if (source instanceof Notification) {
			Notification msg = (Notification) source;
			Object notifier = msg.getNotifier();
			if (notifier instanceof MUIElement) {
				return new Result(((MUIElement) notifier), true);
			}
			int eventType = msg.getEventType();
			switch (eventType) {
			case Notification.SET:
			case Notification.UNSET:
				break;

			default:
				break;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#dispose()
	 */
	public void dispose() {
		if (workbench != null) {
			workbench.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#getHostClassName()
	 */
	public String getHostClassName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#getRoot()
	 */
	public Object getRoot() {
		if (appModel != null) {
			return appModel.getWidget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualFactory#getVisual(org.eclipse.emf.ecore.EObject)
	 */
	public Object getVisual(EObject model) {
		if (model instanceof MUIElement) {
			return ((MUIElement) model).getWidget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualFactory#getVisual(org.eclipse.emf.ecore.EObject, boolean)
	 */
	public Object getVisual(EObject model, boolean loadOnDemand) {
		Object visual = getVisual(model);
		if (visual == null && model instanceof MUIElement) {
			visual = workbench.create((MUIElement) model);
		}
		return visual;
	}

}
