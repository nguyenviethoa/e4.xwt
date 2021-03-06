/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.core.runtime.CoreException;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.tools.ui.designer.render.DesignerPartRenderingEngine;
import org.eclipse.e4.tools.ui.designer.session.ProjectBundleSession;
import org.eclipse.e4.tools.ui.designer.utils.ResourceUtilities;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.internal.workbench.Activator;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.impl.ApplicationImpl;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.model.AbstractModelBuilder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4UIRenderer extends AbstractModelBuilder implements
		IVisualRenderer {

	private ApplicationImpl appModel = null;
	private Resource resource;
	private IFile inputFile;
	private E4WorkbenchProxy workbench;
	private IEclipseContext appContext;
	private ProjectBundleSession projectBundleSession;

	public boolean doLoad(IEditorPart designer, IProgressMonitor monitor) {
		inputFile = (IFile) designer.getAdapter(IFile.class);
		String path = inputFile.getLocation().toString();
		URI uri = URI.createFileURI(path);
		resource = new ResourceSetImpl().getResource(uri, true);
		appModel = (ApplicationImpl) resource.getContents().get(0);
		return appModel != null;
	}

	public ApplicationImpl getDiagram() {
		return appModel;
	}

	public void doSave(IProgressMonitor monitor) {
		if (resource != null) {
			try {
				resource.save(null);
				inputFile.refreshLocal(IResource.DEPTH_ONE, monitor);
			} catch (Exception e) {
			}
		}
	}

	public EObject getModel(Object textNode) {
		return null;
	}

	public IDOMNode getTextNode(Object model) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IModelBuilder#dispose()
	 */
	public void dispose() {
		super.dispose();
		if (workbench != null) {
			workbench.dispose();
		}
		if (projectBundleSession != null){
			try {
				projectBundleSession.dispose();
			} catch (CoreException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer#
	 * createVisuals()
	 */
	public Result createVisuals() {
		if (appModel == null) {
			return Result.NONE;
		}
		// for compatibility layer: set the application in the OSGi service
		// context (see Workbench#getInstance())
		if (!E4Workbench.getServiceContext().containsKey(
				MApplication.class.getName())) {
			// first one wins.
			E4Workbench.getServiceContext().set(MApplication.class.getName(),
					appModel);
		}
		appContext = E4Application.createDefaultContext();

		IExtensionRegistry registry = RegistryFactory.getRegistry();

		projectBundleSession = new ProjectBundleSession(
				Activator.getDefault().getContext());
		DesignerReflectionContributionFactory contributionFactory = new DesignerReflectionContributionFactory(
				registry, projectBundleSession);
		appContext.set(IContributionFactory.class.getName(),
				contributionFactory);

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
			// presentationURI = PartRenderingEngine.engineURI;
			presentationURI = DesignerPartRenderingEngine.engineURI;
		}
		appContext.set(E4Workbench.PRESENTATION_URI_ARG, presentationURI);
		if (cssURI == null) {
			cssURI = cssResourcesURI;
		}
		IProject project = inputFile.getProject();
		// take over the resource resolution
		appContext.set(IResourceUtilities.class.getName(),
				new ResourceUtilities(project, projectBundleSession));

		workbench = new E4WorkbenchProxy(appModel, appContext);
		workbench.createAndRunUI();
		if (cssURI != null) {
			applyStyle((Control) workbench.getRoot(), project, cssURI);
		}

		E4UIEventPublisher globalDistahcher = workbench.getGlobalDistahcher();
		globalDistahcher.addPublishedAdapter(new AdapterImpl() {
			public void notifyChanged(Notification msg) {
				int eventType = msg.getEventType();
				if (eventType == Notification.REMOVE) {
					Object oldValue = msg.getOldValue();
					if (oldValue != null && oldValue instanceof MUIElement) {
						workbench.remove((MUIElement) oldValue);
					}
				}
				if (eventType == Notification.ADD
						|| eventType == Notification.REMOVE) {
					layout(getRoot());
				}
				dispatchEvent(msg);
			}
		});
		// layout(getRoot());
		return new Result(appModel.getWidget(), true);
	}

	public static void applyStyle(Control control, final IProject project,
			final String css) {
		final Shell shell = (Shell) control.getShell();
		if (shell == null) {
			return;
		}

		Display display = shell.getDisplay();
		final CSSEngine engine = (CSSEngine) display
				.getData("org.eclipse.e4.ui.css.core.engine");

		display.syncExec(new Runnable() {
			public void run() {
				try {
					IFile file = project.getFile(css);
					if (file == null || !file.exists()) {
						return;
					}

					InputStream stream = file.getContents();
					InputStreamReader streamReader = new InputStreamReader(
							stream);
					engine.reset();
					stream.close();
					streamReader.close();

					try {
						shell.setRedraw(false);
						shell.reskin(SWT.ALL);
					} finally {
						shell.setRedraw(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void layout(Object widget) {
		if (widget != null && widget instanceof Composite) {
			Composite composite = (Composite) widget;
			if (composite.isDisposed()) {
				return;
			}
			composite.layout();
			for (Control child : composite.getChildren()) {
				layout(child);
			}
			composite.getDisplay().update();
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#refreshVisuals
	 * (java.lang.Object)
	 */
	public Result refreshVisuals(Object source) {
		if (source instanceof Notification) {
			Notification msg = (Notification) source;
			Object notifier = msg.getNotifier();
			return new Result(notifier, true);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer#
	 * getHostClassName()
	 */
	public String getHostClassName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IVisualRenderer#getRoot()
	 */
	public Object getRoot() {
		if (workbench != null) {
			return workbench.getRoot();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.IVisualFactory#getVisual(org
	 * .eclipse.emf.ecore.EObject)
	 */
	public Object getVisual(EObject model) {
		if (model instanceof MUIElement) {
			return ((MUIElement) model).getWidget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.IVisualFactory#getVisual(org
	 * .eclipse.emf.ecore.EObject, boolean)
	 */
	public Object getVisual(EObject model, boolean loadOnDemand) {
		Object visual = getVisual(model);
		if (visual == null && model instanceof MUIElement) {
			visual = workbench.create((MUIElement) model);
		}
		return visual;
	}
}
