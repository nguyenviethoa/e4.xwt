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
package org.eclipse.e4.tools.ui.designer.part;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.editparts.PartContainerEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartMoveRequest extends PartRequest {

	public PartMoveRequest(EditPart host, ChangeBoundsRequest request) {
		super(host, request, RequestConstants.REQ_MOVE_CHILDREN);
	}

	protected void apply(Point location) {
		PartContainerEditPart part = findPartContainer(location);
		if (part == null) {
			return;
		}
		PrecisionRectangle rect = new PrecisionRectangle();

		setPosition(buildMovePosition(rect, part, location));

		setBounds(rect);

		setReference(findReference(location));
	}

	public Position buildMovePosition(Rectangle rect,
			PartContainerEditPart editPart, Point pt) {

		// 1. Move_to_header
		Rectangle[] headers = editPart.getHeaders();
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].contains(pt)) {
				rect.setBounds(headers[i]);
				return Position.Header;
			}
		}
		// 2. Move_to_top
		Rectangle topIndicate = editPart.getTopIndicate();
		if (topIndicate.contains(pt)) {
			rect.setBounds(editPart.getTop());
			return Position.Top;
		}
		// 3.move to bottom
		Rectangle bottomIndicate = editPart.getBottomIndicate();
		if (bottomIndicate.contains(pt)) {
			rect.setBounds(editPart.getBottom());
			return Position.Bottom;
		}

		// 4.move to left
		Rectangle leftIndicate = editPart.getLeftIndicate();
		if (leftIndicate.contains(pt)) {
			rect.setBounds(editPart.getLeft());
			return Position.Left;
		}

		// 5.move to right
		Rectangle rightIndicate = editPart.getRightIndicate();
		if (rightIndicate.contains(pt)) {
			rect.setBounds(editPart.getRight());
			return Position.Right;
		}
		Rectangle bounds = editPart.getFigureBounds();
		rect.setBounds(bounds);
		return Position.Header;
	}
}
