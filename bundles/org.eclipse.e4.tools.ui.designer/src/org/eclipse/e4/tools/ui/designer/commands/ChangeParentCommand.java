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

import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.emf.common.util.EList;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ChangeParentCommand extends AddChildCommand {

	private int oldIndex = -1;
	private MElementContainer<?> oldParent;

	public ChangeParentCommand(MElementContainer<?> newParent,
			MUIElement element, int index) {
		super(newParent, element, index);
	}

	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}
		oldParent = newChild.getParent();
		if (oldParent == null) {
			return false;
		}
		oldIndex = oldParent.getChildren().indexOf(newChild);
		return true;
	}

	public void execute() {
		oldParent.getChildren().remove(newChild);
		super.execute();
	}

	public boolean canUndo() {
		return super.canUndo() && oldParent != null;
	}

	public void undo() {
		super.undo();
		EList<MUIElement> children = (EList<MUIElement>) oldParent
				.getChildren();
		children.add(oldIndex, newChild);
	}
}