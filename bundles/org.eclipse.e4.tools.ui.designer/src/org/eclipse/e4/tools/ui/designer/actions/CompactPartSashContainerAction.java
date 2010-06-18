/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.actions;

import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CompactPartSashContainerAction implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		boolean enabled = false;
		if (!selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof EditPart) {
				EditPart editPart = (EditPart) object;
				object = editPart.getModel();
			}
			if (object instanceof MPartSashContainer) {
				MPartSashContainer selectedPartSashContainer = (MPartSashContainer) object;
				if (!selectedPartSashContainer.getChildren().isEmpty()) {
					enabled = true;
				}
			}
		}
		action.setEnabled(enabled);
	}
}
