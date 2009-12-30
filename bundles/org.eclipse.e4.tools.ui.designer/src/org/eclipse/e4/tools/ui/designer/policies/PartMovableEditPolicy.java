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

import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.parts.PartContainerEditPart;
import org.eclipse.e4.tools.ui.designer.parts.PartEditPart;
import org.eclipse.e4.tools.ui.designer.parts.handlers.MovePartRequest;
import org.eclipse.e4.tools.ui.designer.parts.handlers.MovePosition;
import org.eclipse.e4.tools.ui.designer.sashform.SashFormEditPart;
import org.eclipse.e4.tools.ui.designer.sashform.SashFormUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartMovableEditPolicy extends NonResizableEditPolicy {
	private IFigure dragFeedback;

	protected IFigure createDragFeedback() {
		RectangleFigure r = new RectangleFigure();
		r.setLineWidth(3);
		r.setFill(false);
		r.setForegroundColor(ColorConstants.lightBlue);
		r.setBounds(getInitialFeedbackBounds());
		addFeedback(r);
		return r;
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
		PartContainerEditPart part = getPartContainer(request);
		if (part == null) {
			return;
		}
		if (feedback == dragFeedback) {

			PrecisionRectangle rect = new PrecisionRectangle();

			SashFormEditPart sashFormEp = getSashFormEditPart();
			boolean horizontal = SashFormUtil.isHorizontal(sashFormEp);

			getHostFigure().translateToAbsolute(rect);

			Map extendedData = request.getExtendedData();

			MovePosition position = buildMovePosition(rect, part, request
					.getLocation(), horizontal);

			EditPart reference = getReference(request);
			if (reference == null) {
				reference = part;
			}
			extendedData.put(MovePartRequest.REFERENCE_KEY, reference);
			extendedData.put(MovePartRequest.MOVE_POSITION_KEY, position);

			feedback.translateToRelative(rect);
			feedback.setBounds(rect);

		} else {
			super.showChangeBoundsFeedback(request);
		}
	}

	private MovePosition buildMovePosition(Rectangle rect,
			PartContainerEditPart editPart, Point pt, boolean horizontal) {

		// 1. Move_to_header
		Rectangle[] headers = editPart.getHeaders();
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].contains(pt)) {
				rect.setBounds(headers[i]);
				return MovePosition.MoveToHeader;
			}
		}
		// 2. Move_to_top
		Rectangle topIndicate = editPart.getTopIndicate();
		if (topIndicate.contains(pt)) {
			rect.setBounds(editPart.getTop());
			return MovePosition.MoveToTop;
		}
		// 3.
		Rectangle bottomIndicate = editPart.getBottomIndicate();
		if (bottomIndicate.contains(pt)) {
			rect.setBounds(editPart.getBottom());
			return MovePosition.MoveToBottom;
		}

		// 4.
		Rectangle leftIndicate = editPart.getLeftIndicate();
		if (leftIndicate.contains(pt)) {
			rect.setBounds(editPart.getLeft());
			return MovePosition.MoveToLeft;
		}

		// 5.
		Rectangle rightIndicate = editPart.getRightIndicate();
		if (rightIndicate.contains(pt)) {
			rect.setBounds(editPart.getRight());
			return MovePosition.MoveToRight;
		}
		Rectangle bounds = editPart.getFigureBounds();
		rect.setBounds(bounds);
		return MovePosition.MoveToHeader;
	}

	protected Command getMoveCommand(ChangeBoundsRequest request) {
		MovePartRequest req = new MovePartRequest(request);
		Command command = getHost().getParent().getCommand(req);
		return command;
	}

	private SashFormEditPart getSashFormEditPart() {
		EditPart host = getHost();
		while (host != null && !(host instanceof SashFormEditPart)) {
			host = host.getParent();
		}
		if (host instanceof SashFormEditPart) {
			return (SashFormEditPart) host;
		}
		return null;
	}

	protected PartContainerEditPart getPartContainer(ChangeBoundsRequest request) {
		Point location = request.getLocation();
		EditPart ep = getHost().getViewer().findObjectAt(location);
		while (ep != null && !(ep instanceof PartContainerEditPart)) {
			ep = ep.getParent();
		}
		return (PartContainerEditPart) ep;
	}

	private EditPart getReference(ChangeBoundsRequest request) {
		Point location = request.getLocation();
		EditPart ep = getHost().getViewer().findObjectAt(location);
		if (ep instanceof PartEditPart || ep instanceof PartContainerEditPart) {
			return ep;
		}
		return null;
	}
}
