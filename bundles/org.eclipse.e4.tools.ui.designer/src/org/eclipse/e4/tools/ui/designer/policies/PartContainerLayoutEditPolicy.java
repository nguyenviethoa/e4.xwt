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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.commands.CreatePartCommand;
import org.eclipse.e4.tools.ui.designer.editparts.PartEditPart;
import org.eclipse.e4.tools.ui.designer.part.PartCreateRequest;
import org.eclipse.e4.tools.ui.designer.part.PartFeedback;
import org.eclipse.e4.tools.ui.designer.part.PartReqHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartContainerLayoutEditPolicy extends CompositeLayoutEditPolicy {

	private RectangleFigure feedback;

	public IFigure getFeedback() {
		if (feedback == null) {
			feedback = new PartFeedback(getInitialFeedbackBounds());
			addFeedback(feedback);
		}
		return feedback;
	}

	private Rectangle getInitialFeedbackBounds() {
		GraphicalEditPart host = (GraphicalEditPart) getHost();
		if (host != null) {
			return host.getFigure().getBounds().getCopy();
		}
		return new Rectangle();
	}

	protected Command getCreateCommand(CreateRequest request) {
		PartCreateRequest partReq = (PartCreateRequest) PartReqHelper
				.unwrap(request);
		if (partReq == null) {
			partReq = new PartCreateRequest(getHost(), request);
		}
		return new CreatePartCommand(partReq);
		// return super.getCreateCommand(request);
	}

	protected void showLayoutTargetFeedback(Request request) {
		if (request.getType() == REQ_CREATE) {
			updateRequest((CreateRequest) request);
		} else {
			super.showLayoutTargetFeedback(request);
		}
	}

	private void updateRequest(CreateRequest request) {
		IFigure figure = getFeedback();
		Point location = request.getLocation();

		PartCreateRequest req = new PartCreateRequest(getHost(), request);
		req.setLocation(location);

		Rectangle bounds = req.getBounds();
		figure.translateToRelative(bounds);
		figure.setBounds(bounds);
	}

	protected void eraseLayoutTargetFeedback(Request request) {
		if (feedback != null && feedback.getParent() != null) {
			removeFeedback(feedback);
			feedback = null;
		}
		super.eraseLayoutTargetFeedback(request);
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
