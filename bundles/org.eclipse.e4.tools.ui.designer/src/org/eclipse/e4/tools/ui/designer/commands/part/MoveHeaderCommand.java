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
package org.eclipse.e4.tools.ui.designer.commands.part;

import org.eclipse.e4.tools.ui.designer.commands.ChangeParentCommand;
import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MoveHeaderCommand extends AbstractPartCommand {

	private MPart header;

	public MoveHeaderCommand(MUIElement model, MPartStack partStack,
			MPart header) {
		super(model, partStack);
		this.header = header;
	}

	public boolean canExecute() {
		return super.canExecute() && model instanceof MPart;
	}

	protected Command computeCommand() {
		CompoundCommand cmdList = new CompoundCommand();
		int index = partStack.getChildren().indexOf(header);
		if (index != -1) {
			index++;
		}
		if (index < 0 || index > partStack.getChildren().size()) {
			index = partStack.getChildren().size();
		}
		MElementContainer<MUIElement> parent = model.getParent();
		if (parent != null) {
			cmdList.add(new ChangeParentCommand(partStack, model, index));
			if (parent.getChildren().size() == 1) {
				Command deleteCommand = CommandFactory.createDeleteCommand(parent);
				if (deleteCommand != null ) {
					cmdList.add(deleteCommand);
				}
			}
		} else {
			cmdList.add(CommandFactory.createAddChildCommand(partStack, model, index));
		}
		return cmdList.unwrap();
	}

}
