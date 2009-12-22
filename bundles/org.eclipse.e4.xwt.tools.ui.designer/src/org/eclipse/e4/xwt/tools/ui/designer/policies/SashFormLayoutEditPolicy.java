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
package org.eclipse.e4.xwt.tools.ui.designer.policies;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.e4.xwt.tools.ui.designer.commands.ChangeWeightsCommand;
import org.eclipse.e4.xwt.tools.ui.designer.commands.InsertCreateCommand;
import org.eclipse.e4.xwt.tools.ui.designer.commands.MoveChildCommand;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class SashFormLayoutEditPolicy extends FlowLayoutEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse
	 * .gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		EditPart after = getInsertionReference(request);
		return new InsertCreateCommand(getHost(), after, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createAddCommand
	 * (org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createAddCommand(EditPart child, EditPart after) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createMoveChildCommand
	 * (org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return new MoveChildCommand(child, after);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.LayoutEditPolicy#getCommand(org.eclipse.
	 * gef.Request)
	 */
	public Command getCommand(Request request) {
		if (request instanceof ChangeBoundsRequest) {
			return createChangeBoundsCommand((ChangeBoundsRequest) request);
		}
		return super.getCommand(request);
	}

	protected Command createChangeBoundsCommand(ChangeBoundsRequest request) {
		return new ChangeWeightsCommand(getHost(), request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createChildEditPolicy
	 * (org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		int directions = 0;
		if (isHorizontal()) {
			directions = PositionConstants.EAST_WEST;
		} else {
			directions = PositionConstants.NORTH_SOUTH;
		}
		return new NewResizableEditPolicy(directions, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.FlowLayoutEditPolicy#getLineFeedback()
	 */
	protected Polyline getLineFeedback() {
		Polyline feedback = super.getLineFeedback();
		feedback.setForegroundColor(ColorConstants.red);
		return feedback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.FlowLayoutEditPolicy#isHorizontal()
	 */
	protected boolean isHorizontal() {
		EditPart host = getHost();
		if (host instanceof SashFormEditPart) {
			SashForm sashForm = (SashForm) ((SashFormEditPart) host)
					.getWidget();
			if (sashForm != null && !sashForm.isDisposed()) {
				return (sashForm.getOrientation() & SWT.HORIZONTAL) != 0;
			}
		}
		return true;
	}
}
