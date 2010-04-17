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
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AddChildCommand extends Command {

	protected MElementContainer<?> parent;
	protected MUIElement newChild;
	protected int index;

	public AddChildCommand(MElementContainer<?> parent, MUIElement newChild,
			int index) {
		this.parent = parent;
		this.newChild = newChild;
		this.index = index;
	}

	public boolean canExecute() {
		return parent != null && newChild != null && ApplicationModelHelper.canAddedChild(newChild, parent);
	}

	public void execute() {
		if (index < 0 || index > parent.getChildren().size()) {
			index = parent.getChildren().size();
		}
		List<MUIElement> children = (List<MUIElement>) parent.getChildren();
		children.add(index, newChild);
	}

	public boolean canUndo() {
		return parent != null;
	}

	public void undo() {
		parent.getChildren().remove(newChild);
	}
}
