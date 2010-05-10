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
package org.eclipse.e4.tools.ui.designer.outline.commands;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.ChangeOrderCommand;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MoveChildrenCommand extends Command {

	private EditPart host;
	private List<EditPart> editParts;
	private int index;

	private MElementContainer<MUIElement> parentModel;
	private Command command;

	public MoveChildrenCommand(EditPart host, List<EditPart> editParts,
			int index) {
		this.host = host;
		this.editParts = editParts;
		this.index = index;
	}

	public boolean canExecute() {
		if (editParts == null || editParts.isEmpty()) {
			return false;
		}
		if (host == null) {
			host = (editParts.get(0)).getParent();
		}
		if (host == null) {
			return false;
		}
		for (EditPart child : editParts) {
			if (host != child.getParent()) {
				return false;
			}
		}
		Object model = host.getModel();
		if (model == null || !(model instanceof MElementContainer<?>)) {
			return false;
		}
		parentModel = (MElementContainer<MUIElement>) model;
		command = createCommand();
		return command != null && command.canExecute();
	}

	private Command createCommand() {
		CompoundCommand cmds = new CompoundCommand();
		for (EditPart childEp : editParts) {
			Object child = childEp.getModel();
			if (!(child instanceof MUIElement)) {
				continue;
			}
			ChangeOrderCommand c = new ChangeOrderCommand(parentModel,
					(MUIElement) child, index);
			if (c.canExecute()) {
				cmds.add(c);
			}
		}
		return cmds.unwrap();
	}

	public void execute() {
		command.execute();
	}

	public void undo() {
		command.undo();
	}

	public boolean canUndo() {
		return command != null && command.canExecute();
	}
}
