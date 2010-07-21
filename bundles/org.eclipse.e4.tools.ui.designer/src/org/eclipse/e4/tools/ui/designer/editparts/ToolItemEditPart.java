package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.emf.ecore.EObject;

public class ToolItemEditPart extends WidgetEditPart {

	public ToolItemEditPart(EObject model) {
		super(model);
	}

	protected Rectangle getBounds() {
		Rectangle rectangle = getVisualInfo().getBounds();
		org.eclipse.swt.graphics.Rectangle parentRectangle = ((ToolItem) getWidget())
				.getParent().getBounds();
		rectangle.translate(parentRectangle.x, parentRectangle.y);
		return rectangle;
	}
}
