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
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ChangeParentCommand extends AddChildCommand {

	private int oldIndex = -1;
	private EObject oldParent;
	private String containerData;

	public ChangeParentCommand(EObject newParent, EObject element, int index) {
		super(newParent, element, index);
	}

	public ChangeParentCommand(Object newParent, Object element, int index) {
		super((newParent instanceof EObject ? (EObject) newParent : null),
				(element instanceof EObject ? (EObject) element : null), index);
	}

	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}
		if (oldParent == null) {
			oldParent = ((EObject) newChild).eContainer();
		}
		if (oldParent == null) {
			return false;
		}
		if (oldIndex == -1) {
			oldIndex = ApplicationModelHelper.getChildIndex(oldParent, newChild);
		}
		return true;
	}

	public void execute() {
		if (newChild instanceof MUIElement) {
			containerData = ((MUIElement) newChild).getContainerData();
		}

		EReference reference = ApplicationModelHelper.findReference(oldParent
				.eClass(), newChild.eClass());
		if (reference.isMany()) {
			List listValue = (List) oldParent.eGet(reference);
			listValue.remove(newChild);
		} else {
			oldParent.eSet(reference, null);
		}
		super.execute();
	}

	public boolean canUndo() {
		return super.canUndo() && oldParent != null;
	}

	public void undo() {
		super.undo();

		EReference reference = ApplicationModelHelper.findReference(oldParent
				.eClass(), newChild.eClass());
		if (reference.isMany()) {
			List listValue = (List) oldParent.eGet(reference);
			listValue.add(oldIndex, newChild);
		} else {
			oldParent.eSet(reference, newChild);
		}

		if (newChild instanceof MUIElement) {
			((MUIElement) newChild).setContainerData(containerData);
		}
	}
}
