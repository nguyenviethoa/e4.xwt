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

import java.util.List;
import java.util.Map;

import org.eclipse.e4.tools.ui.designer.parts.PartEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MovePartRequest extends ChangeBoundsRequest {

	public static final String REFERENCE_KEY = "reference";
	public static final String MOVE_POSITION_KEY = "move position";

	private PartEditPart editPart;
	private EditPart reference;
	private MovePosition movePosition;

	/**
	 * Default constructor.
	 */
	public MovePartRequest() {
		setType(RequestConstants.REQ_MOVE_CHILDREN);
	}

	public MovePartRequest(ChangeBoundsRequest req) {
		setEditParts(req.getEditParts());
		setMoveDelta(req.getMoveDelta());
		setSizeDelta(req.getSizeDelta());
		setLocation(req.getLocation());
		setExtendedData(req.getExtendedData());
		setType(RequestConstants.REQ_MOVE_CHILDREN);
	}

	public void setReference(EditPart reference) {
		this.reference = reference;
	}

	public EditPart getReference() {
		if (reference == null) {
			Map extendedData = getExtendedData();
			reference = (EditPart) extendedData.get(REFERENCE_KEY);
		}
		return reference;
	}

	public void setMovePosition(MovePosition movePosition) {
		this.movePosition = movePosition;
	}

	public MovePosition getMovePosition() {
		if (movePosition == null) {
			Map extendedData = getExtendedData();
			movePosition = (MovePosition) extendedData.get(MOVE_POSITION_KEY);
		}
		return movePosition;
	}

	public PartEditPart getEditPart() {
		List editParts = getEditParts();
		if (editParts != null && editParts.size() > 0) {
			for (Object object : editParts) {
				if (object instanceof PartEditPart) {
					editPart = (PartEditPart) object;
				}
			}
		}
		return editPart;
	}

}
