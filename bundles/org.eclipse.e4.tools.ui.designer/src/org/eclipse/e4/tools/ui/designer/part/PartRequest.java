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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.editparts.PartContainerEditPart;
import org.eclipse.e4.tools.ui.designer.editparts.PartEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class PartRequest extends ChangeBoundsRequest {
	public static final String WRAPPER_DATA = "PART_REQUEST_WRAPPER";
	protected EditPart targetEditPart;
	private Request request;

	private Position position;
	private EditPart reference;
	private Rectangle bounds;

	public PartRequest(EditPart targetEditPart, Request request, Object type) {
		super(type);
		this.targetEditPart = targetEditPart;
		this.request = request;
		setEditParts(targetEditPart);
		if (request != null) {
			PartReqHelper.wrap(this, request);
		}
	}

	public Request getRequest() {
		return request;
	}

	public EditPart getTargetEditPart() {
		return targetEditPart;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setReference(EditPart reference) {
		this.reference = reference;
	}

	public EditPart getReference() {
		return reference;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Rectangle getBounds() {
		if (bounds == null) {
			return new Rectangle();
		}
		return bounds;
	}

	public void setLocation(Point location) {
		if (location == null) {
			return;
		}
		apply(location);
	}

	protected PartContainerEditPart findPartContainer(Point location) {
		if (targetEditPart == null) {
			return null;
		}
		EditPart ep = targetEditPart.getViewer().findObjectAt(location);
		while (ep != null && !(ep instanceof PartContainerEditPart)) {
			ep = ep.getParent();
		}
		return (PartContainerEditPart) ep;
	}

	protected EditPart findReference(Point location) {
		if (targetEditPart == null) {
			return null;
		}
		EditPart ep = targetEditPart.getViewer().findObjectAt(location);
		if (ep instanceof PartEditPart || ep instanceof PartContainerEditPart) {
			return ep;
		}
		return null;
	}

	protected abstract void apply(Point location);
}
