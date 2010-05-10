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
import org.eclipse.e4.tools.ui.designer.outline.TreeEditPartFactory;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer.Result;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropContext;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.DesignerOutlinePage;
import org.eclipse.e4.xwt.tools.ui.designer.core.model.IModelBuilder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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

	protected IModelBuilder createModelBuilder() {
		return uiRenderer;
	}

	protected ContextMenuProvider createMenuProvider(EditPartViewer viewer,
			ActionRegistry actionRegistry) {
		return new E4DesignerMenuProvider(viewer, actionRegistry);
	}

	protected EditPartFactory createEditPartFactory() {
		return new E4EditPartsFactory();
	}

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
		propertyPage.setPropertySourceProvider(ApplicationModelHelper
				.getContentProvider());
		return propertyPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createOutlinePage
	 * ()
	 */
	protected DesignerOutlinePage createOutlinePage() {
		DesignerOutlinePage designerOutlinePage = new DesignerOutlinePage(
				getEditDomain(), new TreeEditPartFactory());
		TreeViewer treeViewer = designerOutlinePage.getTreeViewer();
		ContextMenuProvider outlineMenu = createMenuProvider(treeViewer,
				getActionRegistry());
		if (outlineMenu != null) {
			treeViewer.setContextMenu(outlineMenu);
			outlineMenu.setRemoveAllWhenShown(true);
			getSite().registerContextMenu(
					getClass().getSimpleName() + ".outlineMenu", outlineMenu,
					treeViewer);
		}
		return designerOutlinePage;
	}
}
