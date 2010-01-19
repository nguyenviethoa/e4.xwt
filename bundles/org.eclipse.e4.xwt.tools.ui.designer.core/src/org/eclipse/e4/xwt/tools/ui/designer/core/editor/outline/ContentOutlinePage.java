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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline;

import java.util.Iterator;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd.OutlineDragListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd.OutlineDropListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd.OutlineDropManager;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd.OutlineNodeTransfer;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ContentOutlinePage extends
		org.eclipse.ui.views.contentoutline.ContentOutlinePage {

	private Designer designer;
	private ITreeContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private ViewerFilter[] viewerFilters;

	private ContextMenuProvider contextMenuProvider;

	private OutlineDragListener dragListener;
	private OutlineDropListener dropListener;
	private OutlineDropManager dropManager;

	public ContentOutlinePage(Designer designer) {
		this.designer = designer;
	}

	public ContentOutlinePage(Designer designer,
			ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
		this(designer, contentProvider, labelProvider, null);
	}

	public ContentOutlinePage(Designer designer,
			ITreeContentProvider contentProvider, ILabelProvider labelProvider, ViewerFilter[] viewerFilters) {
		this(designer);
		this.setContentProvider(contentProvider);
		this.setLabelProvider(labelProvider);
		this.setViewerFilters(viewerFilters);
	}

	public ViewerFilter[] getViewerFilters() {
		return viewerFilters;
	}

	public void setViewerFilters(ViewerFilter[] viewerFilters) {
		this.viewerFilters = viewerFilters;
		if (getTreeViewer() != null && viewerFilters != null) {
			getTreeViewer().setFilters(viewerFilters);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(
	 * org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		configureViewer();
	}

	private void configureViewer() {
		final TreeViewer treeViewer = getTreeViewer();
		ContextMenuProvider menuManager = getContextMenuProvider();
		if (menuManager != null) {
			Tree tree = treeViewer.getTree();
			tree.setMenu(menuManager.createContextMenu(tree));
		}
		if (getContentProvider() != null) {
			treeViewer.setContentProvider(getContentProvider());
		}
		if (getLabelProvider() != null) {
			treeViewer.setLabelProvider(getLabelProvider());
		}
		if (getViewerFilters() != null) {
			treeViewer.setFilters(getViewerFilters());
		}

		setupDnD(treeViewer);

		if (designer == null) {
			return;
		}
		GraphicalViewer graphicalViewer = designer.getGraphicalViewer();
		if (graphicalViewer != null) {
			RootEditPart rootEditPart = graphicalViewer.getRootEditPart();
			if (rootEditPart != null) {
				treeViewer.setInput(rootEditPart);
				rootEditPart.addEditPartListener(new RefreshListener());
			}
		}
		treeViewer.addSelectionChangedListener(designer);
	}

	protected void setupDnD(TreeViewer treeViewer) {
		if (dragListener == null) {
			dragListener = new OutlineDragListener(treeViewer);
		}
		if (dropListener == null) {
			dropListener = new OutlineDropListener(treeViewer, getDropManager());
		}

		treeViewer.addDragSupport(
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK,
				new Transfer[] { OutlineNodeTransfer.getTransfer() },
				dragListener);
		treeViewer.addDropSupport(
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK,
				new Transfer[] { OutlineNodeTransfer.getTransfer() },
				dropListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.contentoutline.ContentOutlinePage#getTreeViewer()
	 */
	public TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}

	/**
	 * @param contentProvider
	 *            the contentProvider to set
	 */
	public void setContentProvider(ITreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		if (getTreeViewer() != null && contentProvider != null) {
			getTreeViewer().setContentProvider(contentProvider);
		}
	}

	/**
	 * @return the contentProvider
	 */
	public ITreeContentProvider getContentProvider() {
		return contentProvider;
	}

	/**
	 * @param labelProvider
	 *            the labelProvider to set
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
		if (getTreeViewer() != null && labelProvider != null) {
			getTreeViewer().setLabelProvider(labelProvider);
		}
	}

	/**
	 * @return the labelProvider
	 */
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * @param editPart
	 */
	public void refresh(EditPart editPart) {
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer != null) {
			Widget item = treeViewer.testFindItem(editPart);
			if (item != null) {
				treeViewer.refresh(editPart, true);
			} else {
				treeViewer.refresh();
			}
		}
	}

	/**
	 * @param contextMenuProvider
	 *            the contextMenuProvider to set
	 */
	public void setContextMenuProvider(ContextMenuProvider contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
		TreeViewer treeViewer = getTreeViewer();
		if (contextMenuProvider != null && treeViewer != null) {
			Tree tree = treeViewer.getTree();
			tree.setMenu(contextMenuProvider.createContextMenu(tree));
		}
	}

	/**
	 * @return the contextMenuProvider
	 */
	public ContextMenuProvider getContextMenuProvider() {
		return contextMenuProvider;
	}

	/**
	 * @param dropManager
	 *            the dropManager to set
	 */
	public void setDropManager(OutlineDropManager dropManager) {
		this.dropManager = dropManager;
	}

	/**
	 * @return the dropManager
	 */
	public OutlineDropManager getDropManager() {
		return dropManager;
	}

	private class RefreshListener extends EditPartListener.Stub {
		public void childAdded(EditPart child, int index) {			
			updateEditPartListener(child);
			refresh(child.getParent());
		}

		public void removingChild(EditPart child, int index) {
			removeEditPartListener(child);
			TreeViewer treeViewer = getTreeViewer();
			if (treeViewer != null) {
				treeViewer.remove(child);
			}
		}
		
		protected void removeEditPartListener(EditPart child) {
			child.removeEditPartListener(this);
			Iterator<?> it = child.getChildren().iterator();
			while (it.hasNext()) {
				EditPart part = (EditPart) it.next();
				removeEditPartListener(part);
			}			
		}
		
		protected void updateEditPartListener(EditPart child) {
			child.removeEditPartListener(this);
			child.addEditPartListener(this);
			Iterator<?> it = child.getChildren().iterator();
			while (it.hasNext()) {
				EditPart part = (EditPart) it.next();
				updateEditPartListener(part);
			}			
		}
	}
}
