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

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.ContentOutlinePage;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Widget;

public class E4ContentOutlinePage extends ContentOutlinePage {

	public E4ContentOutlinePage(Designer designer) {
		super(designer);
	}

	public E4ContentOutlinePage(Designer designer,
			ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
		this(designer, contentProvider, labelProvider, null);
	}
	
	public E4ContentOutlinePage(Designer designer,
			ITreeContentProvider contentProvider, ILabelProvider labelProvider, ViewerFilter[] viewerFilters) {
		super(designer, contentProvider, labelProvider, viewerFilters);
	}

	/**
	 * @param editPart
	 */
	public void refresh(EditPart editPart) {
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer != null) {
			Object model = editPart.getModel();
			Widget item = treeViewer.testFindItem(model);
			if (item == null) {
				item = treeViewer.testFindItem(editPart);
			}
			if (item != null) {
				treeViewer.refresh(model, true);
			} else {
				treeViewer.refresh();
			}
		}
	}
}
