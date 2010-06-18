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
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.policies.PartContainerLayoutEditPolicy;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartContainerEditPart extends CompositeEditPart {

	public PartContainerEditPart(EObject model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.tools.ui.designer.parts.WidgetEditPart#getModelChildren()
	 */
	protected List<?> getModelChildren() {
		MUIElement muiElement = getMuiElement();
		Object widget = muiElement.getWidget();
		List modelChildren = new ArrayList();
		if (widget instanceof CTabFolder) {
			CTabFolder tabFolder = (CTabFolder) muiElement.getWidget();
			CTabItem[] items = tabFolder.getItems();
			for (CTabItem item : items) {
				if (item != null) {
					modelChildren.add(item);
				}
			}
		}
		return modelChildren;
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.LAYOUT_ROLE);
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new PartContainerLayoutEditPolicy());
	}

	protected EditPart createChild(Object model) {
		if (model instanceof CTabItem) {
			CTabItem item = (CTabItem) model;
			MUIElement data = (MUIElement) item
					.getData(AbstractPartRenderer.OWNING_ME);
			return new PartEditPart((EObject) data, item);
		}
		return super.createChild(model);
	}

	List<EditPart> getHeaderParts() {
		List<EditPart> editParts = new ArrayList<EditPart>();
		List children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			EditPart part = (EditPart) children.get(i);
			if (part instanceof PartEditPart) {
				editParts.add(part);
			}
		}
		return editParts;
	}

	public Rectangle[] getHeaders() {
		List children = getHeaderParts();
		Rectangle r = getFigureBounds();
		Rectangle[] headers = new Rectangle[children.size() + 1];
		int x = r.x, y = r.y, width = r.width, height = 17;
		for (int i = 0; i < children.size(); i++) {
			PartEditPart child = (PartEditPart) children.get(i);
			Rectangle rect = child.getBounds();
			headers[i] = new Rectangle(x, y, rect.width, rect.height);
			x += rect.width;
			width -= rect.width;
			height = Math.max(height, rect.height);
		}
		headers[children.size()] = new Rectangle(x, y, width, height);
		return headers;
	}

	public Rectangle getTopIndicate() {
		Rectangle r = getFigureBounds();
		int height = r.height / 4;
		return new Rectangle(r.x, r.y - 1, r.width, height);
	}

	public Rectangle getTop() {
		Rectangle r = getFigureBounds();
		return new Rectangle(r.x, r.y, r.width, r.height / 2);
	}

	public Rectangle getBottomIndicate() {
		Rectangle r = getFigureBounds();
		int height = r.height / 4;
		return new Rectangle(r.x, r.bottom() - height, r.width, height);
	}

	public Rectangle getBottom() {
		Rectangle r = getFigureBounds();
		Point center = r.getCenter();
		return new Rectangle(r.x, center.y, r.width, r.height / 2);
	}

	public Rectangle getLeftIndicate() {
		Rectangle r = getFigureBounds();
		int width = r.width / 4;
		return new Rectangle(r.x, r.y, width, r.height);
	}

	public Rectangle getLeft() {
		Rectangle r = getFigureBounds();
		return new Rectangle(r.x, r.y, r.width / 2, r.height);
	}

	public Rectangle getRightIndicate() {
		Rectangle r = getFigureBounds();
		int width = r.width / 4;
		return new Rectangle(r.right() - width, r.y, width, r.height);
	}

	public Rectangle getRight() {
		Rectangle r = getFigureBounds();
		Point center = r.getCenter();
		return new Rectangle(center.x, r.y, r.width / 2, r.height);
	}

	public Rectangle getFigureBounds() {
		Rectangle r = getFigure().getBounds().getCopy();
		getFigure().translateToAbsolute(r);
		return r;
	}
}
