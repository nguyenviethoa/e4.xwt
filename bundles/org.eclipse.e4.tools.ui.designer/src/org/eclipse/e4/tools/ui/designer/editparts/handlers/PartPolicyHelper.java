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
package org.eclipse.e4.tools.ui.designer.editparts.handlers;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.editparts.PartContainerEditPart;
import org.eclipse.e4.tools.ui.designer.editparts.PartEditPart;
import org.eclipse.e4.tools.ui.designer.part.Position;
import org.eclipse.e4.tools.ui.designer.sashform.SashFormEditPart;
import org.eclipse.gef.EditPart;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartPolicyHelper {

	public static SashFormEditPart getSashFormEditPart(EditPart host) {
		while (host != null && !(host instanceof SashFormEditPart)) {
			host = host.getParent();
		}
		return (SashFormEditPart) host;
	}

	public static PartContainerEditPart getPartContainer(EditPart host,
			Point location) {
		if (host == null) {
			return null;
		}
		EditPart ep = host.getViewer().findObjectAt(location);
		while (ep != null && !(ep instanceof PartContainerEditPart)) {
			ep = ep.getParent();
		}
		return (PartContainerEditPart) ep;
	}

	public static EditPart getReference(EditPart host, Point location) {
		if (host == null) {
			return null;
		}
		EditPart ep = host.getViewer().findObjectAt(location);
		if (ep instanceof PartEditPart || ep instanceof PartContainerEditPart) {
			return ep;
		}
		return null;
	}

	public static Position buildMovePosition(Rectangle rect,
			PartContainerEditPart editPart, Point pt, boolean horizontal) {

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
		// 3.
		Rectangle bottomIndicate = editPart.getBottomIndicate();
		if (bottomIndicate.contains(pt)) {
			rect.setBounds(editPart.getBottom());
			return Position.Bottom;
		}

		// 4.
		Rectangle leftIndicate = editPart.getLeftIndicate();
		if (leftIndicate.contains(pt)) {
			rect.setBounds(editPart.getLeft());
			return Position.Left;
		}

		// 5.
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
