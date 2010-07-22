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

import org.eclipse.e4.xwt.tools.ui.designer.core.util.Draw2dTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.swt.WidgetLocator;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.ControlInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class ControlEditPart extends WidgetEditPart {

	public ControlEditPart(EObject model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.tools.ui.designer.parts.WidgetEditPart#createVisualInfo()
	 */
	protected IVisualInfo createVisualInfo() {
		Object widget = getMuiElement().getWidget();
		return new ControlInfo(widget, isRoot());
	}

	protected org.eclipse.draw2d.geometry.Rectangle getBounds() {
		Control control = (Control) getWidget();
		if (control != null
				&& !control.isDisposed()
				&& (control.getParent() instanceof CTabFolder || control.getParent() instanceof org.eclipse.e4.ui.widgets.CTabFolder)) {
			return Draw2dTools.toDraw2d(control.getBounds());
		}
		EditPart parentEp = getParent();
		Control parentControl = null;
		if (parentEp instanceof ControlEditPart) {
			parentControl = (Control) ((ControlEditPart) parentEp).getWidget();
		}
		if (parentControl == null || control == null || control.isDisposed()
				|| control.getParent() == parentControl) {
			return super.getBounds();
		} else {
			int x = 0;
			int y = 0;
			int width = -1;
			int height = -1;
			Control parent = control.getParent();
			while (parent != null) {
				Rectangle r = WidgetLocator.getBounds(parent, false);
				x += r.x;
				y += r.y;
				width = Math.max(width, r.width);
				height = Math.max(height, r.height);
				parent = parent.getParent();
				if (parent == parentControl) {
					parent = null;
				}
			}
			Rectangle rect = WidgetLocator.getBounds(control, false);
			rect.x += x;
			rect.y += y;
			if (width != -1) {
				rect.width = Math.min(width, rect.width);
			}
			if (height != -1) {
				rect.height = Math.min(height, rect.height);
			}
			return Draw2dTools.toDraw2d(rect);
		}
	}
}
