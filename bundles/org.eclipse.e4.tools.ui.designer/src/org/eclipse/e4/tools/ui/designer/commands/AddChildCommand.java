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
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AddChildCommand extends Command {

	protected EObject parent;
	protected EObject newChild;
	protected Object oldChild;
	protected int index;
	protected EReference reference;

	public AddChildCommand(EObject parent, EObject newChild,
			int index) {
		this.parent = parent;
		this.newChild = newChild;
		this.index = index;
	}

	public boolean canExecute() {
		return parent != null && newChild != null && ApplicationModelHelper.canAddedChild(parent, newChild);
	}

	public void execute() {
		EObject container = (EObject) parent;
		EObject element = (EObject) newChild;
		reference = ApplicationModelHelper.findReference(container.eClass(), element.eClass());
		
		oldChild = container.eGet(reference);
		if (reference.isMany()) {
			List listValue = (List) oldChild;
			if (index < 0 || index > listValue.size()) {
				index = listValue.size();
			}
			listValue.add(index, newChild);
		}
		else {
			container.eSet(reference, newChild);
		}
	}

	public boolean canUndo() {
		return parent != null && reference != null;
	}

	public void undo() {
		EObject container = (EObject) parent;
		if (reference.isMany()) {
			List listValue = (List) container.eGet(reference);
			listValue.add(index, newChild);
		}
		else {
			container.eSet(reference, oldChild);
		}
	}
}
