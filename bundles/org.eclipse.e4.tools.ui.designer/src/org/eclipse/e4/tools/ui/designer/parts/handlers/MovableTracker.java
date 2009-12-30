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
package org.eclipse.e4.tools.ui.designer.parts.handlers;

import org.eclipse.e4.tools.ui.designer.commands.MovePartCommand;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.DragEditPartsTracker;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MovableTracker extends DragEditPartsTracker {

	public MovableTracker(EditPart sourceEditPart) {
		super(sourceEditPart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#getCommand()
	 */
	protected Command getCommand() {
		ChangeBoundsRequest targetRequest = (ChangeBoundsRequest) getTargetRequest();
		MovePartRequest moveReq = new MovePartRequest(targetRequest);
		return new MovePartCommand(moveReq);
	}
}
