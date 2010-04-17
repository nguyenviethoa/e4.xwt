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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.actions.CopyElementAction;
import org.eclipse.e4.tools.ui.designer.actions.CutElementAction;
import org.eclipse.e4.tools.ui.designer.actions.PasteElementAction;
import org.eclipse.e4.tools.ui.designer.editparts.E4EditPartsFactory;
import org.eclipse.e4.tools.ui.designer.outline.E4ContentOutlinePage;
import org.eclipse.e4.tools.ui.designer.outline.OutlinePageDropManager;
import org.eclipse.e4.ui.model.application.commands.provider.CommandsItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.descriptor.basic.provider.BasicItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.provider.ApplicationItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.provider.AdvancedItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.menu.provider.MenuItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.provider.UiItemProviderAdapterFactory;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.ISelectionSynchronizer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer.Result;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropContext;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.ContentOutlinePage;
import org.eclipse.e4.xwt.tools.ui.designer.core.model.IModelBuilder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4Designer extends Designer {

	private boolean isDirty = false;
	private E4UIRenderer uiRenderer = new E4UIRenderer();
	private ComposedAdapterFactory adapterFactory;

	/*
	 * (non-Javadoc)Property
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createModelBuilder()
	 */
	protected IModelBuilder createModelBuilder() {
		return uiRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.XAMLDesigner#createMenuProvider()
	 */
	protected ContextMenuProvider createMenuProvider() {
		return new E4DesignerMenuProvider(this);
	}

	protected ISelectionSynchronizer createSelectionSynchronizer() {
		return new E4SelectionSynchronizer(getGraphicalViewer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createEditPartFactory
	 * ()
	 */
	protected EditPartFactory createEditPartFactory() {
		return new E4EditPartsFactory();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		EditDomain domain = getEditDomain();
		domain.setDefaultTool(new E4SelectionTool());
		domain.loadDefaultTool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#
	 * setupGraphicalViewerDropCreation(org.eclipse.gef.GraphicalViewer)
	 */
	protected void setupGraphicalViewerDropCreation(GraphicalViewer viewer) {
		viewer.addDropTargetListener(new E4GraphicalViewerDropCreationListener(
				viewer));
	}

	protected ComposedAdapterFactory getAdapterFactory() {
		if (adapterFactory == null) {
			adapterFactory = new ComposedAdapterFactory(
					ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
			adapterFactory
					.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new ApplicationItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new CommandsItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new UiItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new MenuItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new BasicItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new AdvancedItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return adapterFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;
		action = new CopyElementAction(this);
		registry.registerAction(action);
		action = new PasteElementAction(this);
		registry.registerAction(action);
		action = new CutElementAction(this);
		registry.registerAction(action);
	}

	protected void performModelChanged(Notification event) {
		Result result = getVisualsRender().refreshVisuals(event);
		if (result == null || !result.isRefreshed()) {
			return;
		}
		EditPart editPart = getEditPart(result.visuals);
		EditPart toRefresh = null;
		if (editPart != null) {
			toRefresh = editPart.getParent();
		}
		if (toRefresh == null && result.visuals instanceof MUIElement) {
			MElementContainer<MUIElement> parent = ((MUIElement) result.visuals)
					.getParent();
			toRefresh = getEditPart(parent);
		}
		if (toRefresh == null) {
			return;
		}
		getRefresher().refreshInJob(toRefresh);

		getOutlinePage().refresh(toRefresh);

		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPages()
	 */
	protected void createPages() {
		super.createPages();
		// TODO: hide source page quickly.
		pageContainer.setWeights(new int[] { 1, 0 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createVisualsRender()
	 */
	protected IVisualRenderer createVisualsRender() {
		return uiRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#getDropContext()
	 */
	protected DropContext getDropContext() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPropertyPage
	 * ()
	 */
	protected IPropertySheetPage createPropertyPage() {
		PropertySheetPage propertyPage = new PropertySheetPage() {
			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object[] objects = structuredSelection.toArray();
					for (int i = 0; i < objects.length; i++) {
						if (objects[i] instanceof EditPart) {
							EditPart editPart = (EditPart) objects[i];
							objects[i] = editPart.getModel();
						}
					}
					selection = new StructuredSelection(objects);
				}
				super.selectionChanged(part, selection);
			}
		};
		propertyPage
				.setPropertySourceProvider(new AdapterFactoryContentProvider(
						getAdapterFactory()));
		return propertyPage;
	}

	@Override
	protected void setContent(EditPart diagram) {
		super.setContent(diagram);
		EObject eObject = (EObject) diagram.getModel();
		if (eObject != null) {
			getOutlinePage().getTreeViewer().setInput(eObject.eResource());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createOutlinePage
	 * ()
	 */
	protected ContentOutlinePage createOutlinePage() {
		ComposedAdapterFactory adapterFactory = getAdapterFactory();

		ContentOutlinePage outlinePage = new E4ContentOutlinePage(this,
				new AdapterFactoryContentProvider(adapterFactory),
				new AdapterFactoryLabelProvider(adapterFactory),
				new ViewerFilter[] { new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element) {
						if (element instanceof EObject) {
							return ((EObject) element).eResource() != null;
						}
						return false;
					}
				} });
		outlinePage.setDropManager(new OutlinePageDropManager(getEditDomain()
				.getCommandStack()));
		return outlinePage;
	}
}
