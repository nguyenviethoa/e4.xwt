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

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MKeyBinding;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AddApplicationKeyBindingChildCommand extends Command {

	protected MApplication parent;
	protected MKeyBinding newChild;
	protected int index;

	public AddApplicationKeyBindingChildCommand(MApplication parent, MKeyBinding newChild,
			int index) {
		this.parent = parent;
		this.newChild = newChild;
		this.index = index;
	}

	public boolean canExecute() {
		return parent != null && newChild != null;
	}

	public void execute() {
		if (index < 0 || index > parent.getChildren().size()) {
			index = parent.getBindingTables().get(0).getBindings().size();
		}
		EList<MKeyBinding> commands = (EList<MKeyBinding>) parent.getBindingTables().get(0).getBindings();
		commands.add(index, newChild);
	}

	public boolean canUndo() {
		return parent != null;
	}

	public void undo() {
		parent.getBindingTables().get(0).getBindings().remove(newChild);
	}
}
