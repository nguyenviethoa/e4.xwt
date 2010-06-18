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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.part.PartFeedback;
import org.eclipse.e4.tools.ui.designer.part.PartMoveRequest;
import org.eclipse.e4.tools.ui.designer.part.PartReqHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartMovableEditPolicy extends NonResizableEditPolicy {
	private IFigure dragFeedback;

	protected IFigure createDragFeedback() {
		IFigure figure = new PartFeedback(getInitialFeedbackBounds());
		addFeedback(figure);
		return figure;
	}

	protected IFigure getDragFeedback(ChangeBoundsRequest request) {
		if (dragFeedback != null) {
			return dragFeedback;
		}
		if (request.getType().equals(REQ_MOVE)) {
			if (dragFeedback == null) {
				dragFeedback = createDragFeedback();
			}
			return dragFeedback;
		}
		return super.getDragSourceFeedbackFigure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.NonResizableEditPolicy#eraseChangeBoundsFeedback
	 * (org.eclipse.gef.requests.ChangeBoundsRequest)
	 */
	protected void eraseChangeBoundsFeedback(ChangeBoundsRequest request) {
		super.eraseChangeBoundsFeedback(request);
		if (dragFeedback != null && dragFeedback.getParent() != null) {
			removeFeedback(dragFeedback);
			dragFeedback = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.NonResizableEditPolicy#showChangeBoundsFeedback
	 * (org.eclipse.gef.requests.ChangeBoundsRequest)
	 */
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		IFigure feedback = getDragFeedback(request);
		if (feedback == dragFeedback) {
			PartMoveRequest req = new PartMoveRequest(getHost(), request);
			req.setLocation(request.getLocation());

			Rectangle rect = req.getBounds();
			feedback.translateToRelative(rect);
			feedback.setBounds(rect);

		} else {
			super.showChangeBoundsFeedback(request);
		}
	}

	protected Command getMoveCommand(ChangeBoundsRequest request) {
		PartMoveRequest req = (PartMoveRequest) PartReqHelper.unwrap(request);
		if (req != null) {
			return getHost().getParent().getCommand(req);
		}
		return super.getMoveCommand(request);
	}

}
