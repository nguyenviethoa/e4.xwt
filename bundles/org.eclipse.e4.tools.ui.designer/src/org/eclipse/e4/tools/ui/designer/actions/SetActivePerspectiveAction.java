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
package org.eclipse.e4.tools.ui.designer.actions;

import org.eclipse.e4.tools.ui.designer.commands.SetActivePerspectiveCommand;
import org.eclipse.e4.ui.model.application.MPerspective;
import org.eclipse.e4.ui.model.application.MPerspectiveStack;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @yyang <yves.yang@soyatec.com>
 */
public class SetActivePerspectiveAction implements IObjectActionDelegate {
	private SetActivePerspectiveCommand command;
	private Designer designer;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		designer = (Designer) targetPart;
	}

	public void run(IAction action) {
		designer.getEditDomain().getCommandStack().execute(command);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		command = null;
		if (!selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof EditPart) {
				EditPart editPart = (EditPart) object;
				object = editPart.getModel();
			}
			if (object instanceof MPerspective) {
				MPerspective selectedPerspective = (MPerspective) object;
				Object parent = selectedPerspective.getParent();
				if (parent instanceof MPerspectiveStack) {
					MPerspectiveStack perspectiveStack = (MPerspectiveStack) parent;
					if (perspectiveStack.getSelectedElement() != selectedPerspective) {
						command = new SetActivePerspectiveCommand(selectedPerspective, perspectiveStack);
					}
				}
			}
		}
		action.setEnabled(command != null && command.canExecute());
	}
}
