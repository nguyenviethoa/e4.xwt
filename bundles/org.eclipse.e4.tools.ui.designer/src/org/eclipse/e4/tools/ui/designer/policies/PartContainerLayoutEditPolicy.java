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
package org.eclipse.e4.tools.ui.designer.policies;

import org.eclipse.e4.tools.ui.designer.commands.MovePartCommand;
import org.eclipse.e4.tools.ui.designer.commands.NoOpCommand;
import org.eclipse.e4.tools.ui.designer.parts.PartEditPart;
import org.eclipse.e4.tools.ui.designer.parts.handlers.MovePartRequest;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartContainerLayoutEditPolicy extends CompositeLayoutEditPolicy {

	public Command getCommand(Request request) {
		if (request.getType().equals(REQ_MOVE_CHILDREN)) {
			MovePartRequest req = new MovePartRequest(
					(ChangeBoundsRequest) request);
			return new MovePartCommand(req);
		}
		Command command = super.getCommand(request);
		if (command == null && request instanceof ChangeBoundsRequest) {
			return NoOpCommand.INSTANCE;
		}
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#
	 * createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof PartEditPart) {
			return new PartMovableEditPolicy();
		}
		return super.createChildEditPolicy(child);
	}
}
