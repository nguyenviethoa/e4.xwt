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
package org.eclipse.e4.tools.ui.designer.sashform;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Transposer;
import org.eclipse.e4.tools.ui.designer.commands.MoveChildCommand;
import org.eclipse.e4.tools.ui.designer.commands.NoOpCommand;
import org.eclipse.e4.tools.ui.designer.editparts.SashEditPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashFormLayoutEditPolicy extends FlowLayoutEditPolicy {
	static int WIDTH = 4;
	private Polygon insertionLine;

	protected Command getCreateCommand(CreateRequest request) {
		EditPart reference = getInsertionReference(request);
		boolean after = false;
		if (reference instanceof SashEditPart) {
			EditPart previous = null;
			for (Object child : getHost().getChildren()) {
				if (child == reference) {
					break;
				}
				if (!(child instanceof SashEditPart)) {
					previous = (EditPart) child;
				}
			}
			if (previous != null) {
				reference = previous;
				after = true;
			}
		}
		return new SashFormInsertCreateCommand((SashFormEditPart) getHost(),
				request, reference, after);
	}

	protected Point getLocationFromRequest(Request request) {
		return ((DropRequest) request).getLocation();
	}

	protected Polyline getLineFeedback() {
		if (insertionLine == null) {
			insertionLine = new Polygon();
			insertionLine.setLineWidth(WIDTH);
			insertionLine.addPoint(new Point(0, 0));
			insertionLine.addPoint(new Point(10, 10));
			insertionLine.addPoint(new Point(10, 10));
			insertionLine.addPoint(new Point(10, 10));
			insertionLine.setForegroundColor(ColorConstants.lightBlue);
			addFeedback(insertionLine);
		}
		return insertionLine;
	}

	/**
	 * @see LayoutEditPolicy#eraseLayoutTargetFeedback(Request)
	 */
	protected void eraseLayoutTargetFeedback(Request request) {
		if (insertionLine != null) {
			removeFeedback(insertionLine);
			insertionLine = null;
		}
	}

	/**
	 * display the feedback when it is armed to insert an child
	 * 
	 */
	@Override
	protected void showLayoutTargetFeedback(Request request) {
		if (!RequestConstants.REQ_CREATE.equals(request.getType())) {
			return;
		}
		if (getHost().getChildren().size() == 0)
			return;
		Polyline fb = getLineFeedback();
		Transposer transposer = new Transposer();
		transposer.setEnabled(!isHorizontal());

		SashFormEditPart host = (SashFormEditPart) getHost();
		List<GraphicalEditPart> children = host.getChildren();
		Rectangle parentBox = transposer.t(getAbsoluteBounds(host));

		boolean before = true;
		int epIndex = getFeedbackIndexFor(request);
		Rectangle r = null;
		GraphicalEditPart editPart;
		if (epIndex == -1) {
			before = false;
			epIndex = children.size() - 1;
			editPart = children.get(epIndex);
			r = transposer.t(getAbsoluteBounds(editPart));
		} else {
			editPart = children.get(epIndex);
			r = transposer.t(getAbsoluteBounds(editPart));
			Point p = transposer.t(getLocationFromRequest(request));
			if (p.x <= r.x + (r.width / 2))
				before = true;
			else {
				/*
				 * We are not to the left of this Figure, so the emphasis line
				 * needs to be to the right of the previous Figure, which must
				 * be on the previous row.
				 */
				before = false;
				epIndex--;
				editPart = children.get(epIndex);
				r = transposer.t(getAbsoluteBounds(editPart));
			}
		}

		int x = Integer.MIN_VALUE;
		if (before) {
			/*
			 * Want the line to be halfway between the end of the previous and
			 * the beginning of this one. If at the begining of a line, then
			 * start halfway between the left edge of the parent and the
			 * beginning of the box, but no more than 5 pixels (it would be too
			 * far and be confusing otherwise).
			 */
			if (epIndex > 0) {
				// Need to determine if a line break.
				Rectangle boxPrev = transposer.t(getAbsoluteBounds(children
						.get(epIndex - 1)));
				int prevRight = boxPrev.right();
				if (prevRight < r.x) {
					// Not a line break
					x = prevRight + (r.x - prevRight) / 2;
				} else if (prevRight == r.x) {
					x = prevRight + 1;
				}
			}
			if (x == Integer.MIN_VALUE) {
				// It is a line break.
				x = r.x - 5;
				if (x < parentBox.x)
					x = parentBox.x + (r.x - parentBox.x) / 2;
			}
		} else {
			/*
			 * We only have before==false if we are at the end of a line, so go
			 * halfway between the right edge and the right edge of the parent,
			 * but no more than 5 pixels.
			 */
			int rRight = r.x + r.width;
			int pRight = parentBox.x + parentBox.width;
			x = rRight + 5;
			if (x > pRight)
				x = rRight + (pRight - rRight) / 2;
		}
		Point p1 = new Point(x, r.y);
		p1 = transposer.t(p1);
		Point p2 = new Point(x, r.y + parentBox.height);
		p2 = transposer.t(p2);

		Point p3;
		Point p4;

		if (editPart instanceof SashEditPart) {
			if (before) {
				editPart = children.get(epIndex - 1);
				before = false;
			} else {
				editPart = children.get(epIndex + 1);
			}
		}

		Rectangle sibleBound = transposer.t(getAbsoluteBounds(editPart));
		if (before) {
			p3 = new Point(x + sibleBound.width / 2, r.y + parentBox.height);
			p4 = new Point(x + sibleBound.width / 2, r.y);
		} else {
			p3 = new Point(x - sibleBound.width / 2, r.y + parentBox.height);
			p4 = new Point(x - sibleBound.width / 2, r.y);
		}
		p3 = transposer.t(p3);
		p4 = transposer.t(p4);

		fb.translateToRelative(p1);
		fb.setPoint(p1, 0);
		fb.translateToRelative(p2);
		fb.setPoint(p2, 1);
		fb.translateToRelative(p3);
		fb.setPoint(p3, 2);
		fb.translateToRelative(p4);
		fb.setPoint(p4, 3);
	}

	private Rectangle getAbsoluteBounds(GraphicalEditPart ep) {
		Rectangle bounds = ep.getFigure().getBounds().getCopy();
		ep.getFigure().translateToAbsolute(bounds);
		return bounds;
	}

	protected Command createAddCommand(EditPart child, EditPart after) {
		return NoOpCommand.INSTANCE;
	}

	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return new MoveChildCommand(child, after);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createChildEditPolicy
	 * (org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof SashEditPart) {
			return new SashMoveableEditPolicy((SashEditPart) child);
		}
		return new SashFormChildResizableEditPolicy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.LayoutEditPolicy#getCommand(org.eclipse.
	 * gef.Request)
	 */
	public Command getCommand(Request request) {
		if (request.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
			return new ChangeWeightsCommand((SashFormEditPart) getHost(),
					(ChangeBoundsRequest) request);
		}
		return super.getCommand(request);
	}

	protected boolean isHorizontal() {
		SashFormEditPart host = (SashFormEditPart) getHost();
		if (host == null) {
			return true;
		}
		MPartSashContainer model = (MPartSashContainer) host.getModel();
		return model.isHorizontal();
	}
}
