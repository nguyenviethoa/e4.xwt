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

import org.eclipse.e4.tools.ui.designer.commands.factory.CommandsFactory;
import org.eclipse.e4.tools.ui.designer.commands.factory.ToolBarCommandsFactory;
import org.eclipse.e4.ui.model.application.MToolItem;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ToolBarLayoutEditPolicy extends FlowLayoutEditPolicy {

	private CommandsFactory factory;

	public ToolBarLayoutEditPolicy() {
	}

	public void activate() {
		super.activate();
		if (factory == null) {
			factory = new ToolBarCommandsFactory(getHost());
		}
	}

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
		return factory.getCreateCommand(request, insertionReference, MToolItem.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createAddCommand
	 * (org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createAddCommand(EditPart child, EditPart after) {
		return factory.getAddCommand(child, after);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createMoveChildCommand
	 * (org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return factory.getMoveChildCommand(child, after);
	}

}
