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
package org.eclipse.e4.tools.ui.designer.policies;

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ToolBarLayoutEditPolicy extends FlowLayoutEditPolicy {

	protected boolean isHorizontal() {
		// quickly fixed.
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse
	 * .gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		EditPart insertionReference = getInsertionReference(request);
		return CommandFactory.createCreateCommand(request, getHost(),
				insertionReference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createAddCommand
	 * (org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createAddCommand(EditPart child, EditPart after) {
		EditPart host = getHost();
		if (host == null || child == null) {
			return UnexecutableCommand.INSTANCE;
		}
		int index = -1;
		if (after != null) {
			index = host.getChildren().indexOf(after);
		}
		return CommandFactory.createAddChildCommand(host.getModel(), child
				.getModel(), index);
	}

	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return null;
	}

}
