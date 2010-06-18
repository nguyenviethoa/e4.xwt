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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.SelectionSynchronizer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.DesignerOutlinePage;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4SelectionSynchronizer extends SelectionSynchronizer {
	protected GraphicalViewer viewer;

	public E4SelectionSynchronizer(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Maps the given editpart from one viewer to an editpart in another viewer.
	 * It returns <code>null</code> if there is no corresponding part. This
	 * method can be overridden to provide custom mapping.
	 * 
	 * @param viewer
	 *            the viewer being mapped to
	 * @param part
	 *            a part from another viewer
	 * @return <code>null</code> or a corresponding editpart
	 */
	protected EditPart convert(ISelectionProvider viewer, Object model) {
		return (EditPart) this.viewer.getEditPartRegistry().get(model);
	}

	protected void setViewerSelection(ISelectionProvider source, ISelectionProvider viewer,
			ISelection selection) {
		if (source instanceof DesignerOutlinePage) {
			ArrayList<EditPart> result = new ArrayList<EditPart>();
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			while (iter.hasNext()) {
				EditPart part = convert(viewer, iter.next());
				if (part != null) {
					result.add(part);
				}
			}
			viewer.setSelection(new StructuredSelection(result));
			if (result.size() > 0) {
				if (viewer instanceof EditPartViewer) {
					((EditPartViewer) viewer).reveal((EditPart) result.get(result
							.size() - 1));
				}
			}
		} else if (viewer instanceof DesignerOutlinePage){
			ArrayList<Object> result = new ArrayList<Object>();
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			while (iter.hasNext()) {
				EditPart part = (EditPart) iter.next();
				if (part != null) {
					result.add(part.getModel());
				}
			}
			viewer.setSelection(new StructuredSelection(result));
			if (result.size() > 0) {
				if (viewer instanceof EditPartViewer) {
					((EditPartViewer) viewer).reveal((EditPart) result.get(result
							.size() - 1));
				}
			}
		} else {
			ArrayList<Object> result = new ArrayList<Object>();
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			while (iter.hasNext()) {
				EditPart part = (EditPart) iter.next();
				if (part != null) {
					result.add(part);
				}
			}
			viewer.setSelection(new StructuredSelection(result));
			if (result.size() > 0) {
				if (viewer instanceof EditPartViewer) {
					((EditPartViewer) viewer).reveal((EditPart) result.get(result
							.size() - 1));
				}
			}
		} 
	}
}
