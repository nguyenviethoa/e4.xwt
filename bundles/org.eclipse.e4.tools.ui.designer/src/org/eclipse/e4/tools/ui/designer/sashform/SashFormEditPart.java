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
package org.eclipse.e4.tools.ui.designer.sashform;

import java.util.List;
import org.eclipse.e4.tools.ui.designer.editparts.CompositeEditPart;
import org.eclipse.e4.tools.ui.designer.editparts.SashEditPart;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.CompositeInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.RectangleInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Sash;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashFormEditPart extends CompositeEditPart {

	public SashFormEditPart(EObject model) {
		super(model);
	}

	protected IVisualInfo createVisualInfo() {
		Object widget = getMuiElement().getWidget();
		if (widget instanceof Rectangle) {
			Rectangle rectangle = (Rectangle) widget;
			if (getParent() instanceof SashFormEditPart) {
				return new RectangleInfo(
						new org.eclipse.draw2d.geometry.Rectangle(0,
								0, rectangle.width, rectangle.height),
						isRoot());
			}
			return new RectangleInfo(
					new org.eclipse.draw2d.geometry.Rectangle(rectangle.x,
							rectangle.y, rectangle.width, rectangle.height),
					isRoot());
		}
		return new CompositeInfo(widget, isRoot());
	}

	protected org.eclipse.draw2d.geometry.Rectangle getBounds() {
		Object widget = getWidget();
		if (widget instanceof Rectangle) {
			Rectangle rectangle = (Rectangle) widget;
			if (getParent() instanceof SashFormEditPart) {
				return new org.eclipse.draw2d.geometry.Rectangle(0, 0,
						rectangle.width, rectangle.height);
			}
			return new org.eclipse.draw2d.geometry.Rectangle(rectangle.x,
					rectangle.y, rectangle.width, rectangle.height);
		}
		return super.getBounds();
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.LAYOUT_ROLE);
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new SashFormLayoutEditPolicy());
	}

	public void refresh() {
		super.refresh();
		EditPolicy layoutPolicy = getEditPolicy(EditPolicy.LAYOUT_ROLE);
		if (layoutPolicy != null) {
			layoutPolicy.deactivate();
			layoutPolicy.activate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.tools.ui.designer.parts.WidgetEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		List children = super.getModelChildren();
		// TODO
		// SashForm sashForm = (SashForm) getMuiElement().getWidget();
		// if (sashForm != null && !sashForm.isDisposed()) {
		// int i = 1;
		// Control[] controls = sashForm.getChildren();
		// for (Control control : controls) {
		// if (control instanceof Sash) {
		// children.add(i, control);
		// i += 2;
		// }
		// }
		// }
		return children;
	}

	protected EditPart createChild(Object model) {
		if (model instanceof Sash) {
			return new SashEditPart((Sash) model, null);
		}
		return super.createChild(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.FlowLayoutEditPolicy#isHorizontal()
	 */
	public boolean isHorizontal() {
		MUIElement muiElement = getMuiElement();
		if (muiElement instanceof MGenericTile) {
			MGenericTile<?> partSashContainer = (MGenericTile<?>) muiElement;
			return partSashContainer.isHorizontal();
		}
		SashForm sashForm = (SashForm) getWidget();
		if (sashForm != null && !sashForm.isDisposed()) {
			return (sashForm.getOrientation() & SWT.HORIZONTAL) != 0;
		}
		return true;
	}
}
