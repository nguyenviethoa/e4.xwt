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

import org.eclipse.e4.tools.ui.designer.commands.part.PartCommandFactory;
import org.eclipse.e4.tools.ui.designer.part.PartMoveRequest;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MovePartCommand extends Command {

	private PartMoveRequest request;
	private Command command;

	public MovePartCommand(PartMoveRequest request) {
		this.request = request;
	}

	public boolean canExecute() {
		if (request == null || request.getPosition() == null
				|| request.getReference() == null) {
			return false;
		}

		if (command == null) {
			command = createCommand();			
		}
		return command != null && command.canExecute();
	}

	public void execute() {
		command.execute();
	}

	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	public void undo() {
		command.undo();
	}

	private Command createCommand() {
		EditPart targetEditPart = request.getTargetEditPart();
		MPart part = null;
		MPartStack partStack = null;
		MPart header = null;
		Object model = targetEditPart.getModel();
		if (model instanceof MPart) {
			part = (MPart) model;
		}
		EditPart refEditPart = request.getReference();
		Object refModel = refEditPart.getModel();
		if (refModel instanceof MPartStack) {
			partStack = (MPartStack) refModel;
		} else if (refModel instanceof MPart) {
			header = (MPart) refModel;
			partStack = (MPartStack) (MUIElement) header.getParent();
		}
		if (part == null || partStack == null) {
			return null;
		}
		return PartCommandFactory.createCommand(request.getPosition(), part,
				partStack, header);
	}

}
