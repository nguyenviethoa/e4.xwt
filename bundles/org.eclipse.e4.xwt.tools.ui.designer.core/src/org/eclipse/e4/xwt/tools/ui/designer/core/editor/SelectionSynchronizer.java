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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SelectionSynchronizer implements ISelectionChangedListener {

	private List<ISelectionProvider> viewers = new ArrayList<ISelectionProvider>();
	private boolean isDispatching = false;
	private int disabled = 0;
	private ISelectionProvider pendingSelection;

	/**
	 * Adds a viewer to the set of synchronized viewers
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public void addViewer(ISelectionProvider viewer) {
		viewer.addSelectionChangedListener(this);
		viewers.add(viewer);
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
	protected EditPart convert(ISelectionProvider viewer, EditPart part) {
		// Object temp = viewer.getEditPartRegistry().get(part.getModel());
		EditPart newPart = part;
		// if (temp != null) {
		// newPart = (EditPart) temp;
		// }
		return newPart;
	}

	/**
	 * Removes the viewer from the set of synchronized viewers
	 * 
	 * @param viewer
	 *            the viewer to remove
	 */
	public void removeViewer(ISelectionProvider viewer) {
		viewer.removeSelectionChangedListener(this);
		viewers.remove(viewer);
		if (pendingSelection == viewer)
			pendingSelection = null;
	}

	/**
	 * Receives notification from one viewer, and maps selection to all other
	 * members.
	 * 
	 * @param event
	 *            the selection event
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (isDispatching)
			return;
		ISelectionProvider source = event.getSelectionProvider();
		if (disabled > 0) {
			pendingSelection = source;
		} else {
			ISelection selection = event.getSelection();
			syncSelection(source, selection);
		}
	}

	private void syncSelection(ISelectionProvider source, ISelection selection) {
		isDispatching = true;
		for (int i = 0; i < viewers.size(); i++) {
			if (viewers.get(i) != source) {
				ISelectionProvider viewer = viewers.get(i);
				setViewerSelection(viewer, selection);
			}
		}
		isDispatching = false;
	}

	/**
	 * Enables or disabled synchronization between viewers.
	 * 
	 * @since 3.1
	 * @param value
	 *            <code>true</code> if synchronization should occur
	 */
	public void setEnabled(boolean value) {
		if (!value)
			disabled++;
		else if (--disabled == 0 && pendingSelection != null) {
			syncSelection(pendingSelection, pendingSelection.getSelection());
			pendingSelection = null;
		}
	}

	private void setViewerSelection(ISelectionProvider viewer,
			ISelection selection) {
		ArrayList<EditPart> result = new ArrayList<EditPart>();
		Iterator iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			EditPart part = convert(viewer, (EditPart) iter.next());
			if (part != null)
				result.add(part);
		}
		viewer.setSelection(new StructuredSelection(result));
		if (result.size() > 0)
			if (viewer instanceof EditPartViewer) {
				((EditPartViewer) viewer).reveal((EditPart) result.get(result
						.size() - 1));
			}
	}

}
