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
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.editparts.handlers.MovableTracker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.Draw2dTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.WidgetInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartEditPart extends WidgetEditPart {

	private Widget header;

	public PartEditPart(EObject model, Widget header) {
		super(model);
		this.header = header;
	}

	protected Rectangle getBounds() {
		if (!validateVisuals() || !isHeaderShowing()) {
			return new Rectangle();
		}
		return Draw2dTools.toDraw2d(getHeaderBounds());
	}

	protected boolean isHeaderShowing() {
		if (header instanceof CTabItem) {
			CTabItem tabItem = (CTabItem) header;
			return tabItem.isShowing();
		}
		throw new UnsupportedOperationException();
	}

	protected org.eclipse.swt.graphics.Rectangle getHeaderBounds() {
		if (header instanceof CTabItem) {
			CTabItem tabItem = (CTabItem) header;
			return tabItem.getBounds();
		}
		throw new UnsupportedOperationException();
	}

	public MUIElement getPartModel() {
		if (header != null && !header.isDisposed()) {
			return (MUIElement) header.getData(AbstractPartRenderer.OWNING_ME);
		}
		return null;
	}

	protected boolean validateVisuals() {
		if (header == null || header.isDisposed()) {
			Control widget = (Control) getMuiElement().getWidget();
			if (widget != null && !widget.isDisposed()) {
				Object container = widget.getParent();
				if (container instanceof CTabFolder) {
					CTabFolder parent = (CTabFolder) widget.getParent();
					for (CTabItem item : parent.getItems()) {
						if (widget == item.getControl()) {
							header = item;
							break;
						}
					}
				}
			}
			if (header != null && !header.isDisposed()) {
				getVisualInfo().setVisualObject(header);
			} else {
				getVisualInfo().setVisualObject(null);
			}
		}
		return super.validateVisuals();
	}

	protected IVisualInfo createVisualInfo() {
		return new WidgetInfo(header, isRoot());
	}

	public DragTracker getDragTracker(Request request) {
		return new MovableTracker(this);
	}

}
