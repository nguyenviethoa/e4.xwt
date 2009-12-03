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
package org.eclipse.e4.xwt.tools.ui.designer.visuals;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.xwt.tools.ui.designer.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.tools.Draw2dTools;
import org.eclipse.swt.widgets.Composite;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class CompositeVisualInfo extends ControlVisualInfo implements IVisualOffset {

	public CompositeVisualInfo(Composite composite) {
		super(composite);
	}

	private Point getOriginOffset() {
		Composite composite = (Composite) getVisualable();
		return Draw2dTools.toDraw2d(SWTTools.getOffset(composite));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.xwt.visuals.IVisualOffset#getXOffset()
	 */
	public int getXOffset() {
		return getOriginOffset().x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.xwt.visuals.IVisualOffset#getYOffset()
	 */
	public int getYOffset() {
		return getOriginOffset().y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.visuals.WidgetVisualInfo#getClientArea()
	 */
	public Rectangle getClientArea() {
		Rectangle bounds = getBounds().getCopy();
		Composite visualable = (Composite) getVisualable();
		org.eclipse.swt.graphics.Point offset = SWTTools.getOffset(visualable);
		bounds.translate(offset.x, offset.y).resize(-offset.x, -offset.y);
		int borderWidth = visualable.getBorderWidth();
		bounds.resize(-borderWidth * 2, -borderWidth * 2);
		return bounds;
	}

}
