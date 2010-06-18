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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.actions.CopyElementAction;
import org.eclipse.e4.tools.ui.designer.actions.CutElementAction;
import org.eclipse.e4.tools.ui.designer.actions.PasteElementAction;
import org.eclipse.e4.tools.ui.designer.editparts.E4EditPartsFactory;
import org.eclipse.e4.tools.ui.designer.outline.TreeEditPartFactory;
import org.eclipse.e4.tools.ui.designer.properties.E4TabbedPropertySheetPage;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.xwt.tools.ui.designer.core.ceditor.ConfigureDesigner;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.DesignerOutlinePage;
import org.eclipse.e4.xwt.tools.ui.designer.core.model.IModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.root.DesignerRootEditPart;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4Designer extends ConfigureDesigner implements
		ITabbedPropertySheetPageContributor {

	private E4UIRenderer uiRenderer = new E4UIRenderer();

	protected void initializeGraphicalViewer() {
		GraphicalViewer graphicalViewer = getGraphicalViewer();
		graphicalViewer.setContextMenu(createMenuProvider(graphicalViewer,
				getActionRegistry()));
		graphicalViewer.setEditPartFactory(createEditPartFactory());
		graphicalViewer.setRootEditPart(new DesignerRootEditPart());
		graphicalViewer
				.addDropTargetListener(new E4GraphicalViewerDropCreationListener(
						graphicalViewer));
	}

	public MApplication getDocumentRoot() {
		if (uiRenderer != null) {
			return uiRenderer.getDiagram();
		}
		return null;
	}

	protected IModelBuilder createModelBuilder() {
		return uiRenderer;
	}

	protected ContextMenuProvider createMenuProvider(EditPartViewer viewer,
			ActionRegistry actionRegistry) {
		return new E4DesignerMenuProvider(getProject(), viewer, actionRegistry);
	}

	protected EditPartFactory createEditPartFactory() {
		return new E4EditPartsFactory();
	}

	protected EditDomain createEditDomain() {
		EditDomain ed = super.createEditDomain();
		ed.setDefaultTool(new E4SelectionTool());
		ed.loadDefaultTool();
		return ed;
	}

	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new CopyElementAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new PasteElementAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new CutElementAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

	}

	protected IVisualRenderer createVisualsRender(IFile file, Object diagram) {
		return uiRenderer;
	}

	protected void saveGraphicalEditor(IProgressMonitor monitor) {
		if (uiRenderer != null) {
			uiRenderer.doSave(monitor);
		}
		super.saveGraphicalEditor(monitor);
	}

	protected IPropertySheetPage createPropertySheetPage() {
		return new E4TabbedPropertySheetPage(this, getCommandStack());
	}

	protected IContentOutlinePage createContentOutlinePage() {
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
		getSelectionSynchronizer().addViewer(treeViewer);
		return designerOutlinePage;
	}

	public String getContributorId() {
		return getSite().getId();
	}
}
