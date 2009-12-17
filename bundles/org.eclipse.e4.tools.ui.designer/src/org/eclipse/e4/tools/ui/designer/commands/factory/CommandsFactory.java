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
package org.eclipse.e4.tools.ui.designer.commands.factory;

import org.eclipse.e4.tools.ui.designer.commands.ChangeConstraintCommand;
import org.eclipse.e4.tools.ui.designer.commands.CreateCommand;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class CommandsFactory {

	protected EditPart editPart;

	public CommandsFactory(EditPart editPart) {
		this.editPart = editPart;
	}

	public Command getCreateCommand(CreateRequest request, EditPart insertAfter) {
		return new CreateCommand(editPart, request, insertAfter);
	}

	public Command getAddCommand(EditPart child, EditPart after) {
		return null;
	}

	public Command getMoveChildCommand(EditPart child, EditPart after) {
		return null;
	}

	public Command getChangeConstraintCommand(EditPart child, Object constraint) {
		return new ChangeConstraintCommand(child, constraint);
	}

}
