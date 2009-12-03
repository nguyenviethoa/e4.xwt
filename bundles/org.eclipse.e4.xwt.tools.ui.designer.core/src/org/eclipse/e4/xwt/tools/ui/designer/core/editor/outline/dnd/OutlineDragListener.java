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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd;

import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class OutlineDragListener implements DragSourceListener {

	private TreeViewer treeViewer;

	public OutlineDragListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	private XamlNode getSelection() {
		if (treeViewer == null) {
			return null;
		}
		ISelection selection = treeViewer.getSelection();
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		Object firstElement = ((IStructuredSelection) selection).getFirstElement();
		if (firstElement instanceof EditPart) {
			return (XamlNode) ((EditPart) firstElement).getModel();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
		OutlineNodeTransfer.getTransfer().setNode(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		event.data = getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		XamlNode selection = getSelection();
		if (selection == null) {
			event.doit = false;
		}
		OutlineNodeTransfer.getTransfer().setNode(selection);
	}

}
