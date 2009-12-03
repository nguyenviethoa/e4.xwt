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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.AbstractVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.images.ImageCollector;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.tools.Draw2dTools;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class WidgetVisualInfo extends AbstractVisualInfo {
	private ImageCollector fImageCollector;

	public WidgetVisualInfo(Widget widget) {
		setVisualable(widget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.visual.IVisualInfo#getBounds()
	 */
	public Rectangle getBounds() {
		Widget w = getWidget();
		if (w != null && !w.isDisposed()) {
			return Draw2dTools.toDraw2d(SWTTools.getBounds(w));
		}
		return new Rectangle();
	}

	/**
	 * @return
	 */
	private Widget getWidget() {
		return (Widget) getVisualable();
	}

	/**
	 * @return the fImageDataCollector
	 */
	public ImageCollector getImageDataCollector() {
		if (fImageCollector == null) {
			fImageCollector = new ImageCollector();
		}
		return fImageCollector;
	}

	public int getStyle() {
		Widget widget = getWidget();
		if (widget != null && !widget.isDisposed()) {
			return widget.getStyle();
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.visuals.IVisualInfo#getClientArea()
	 */
	public Rectangle getClientArea() {
		// TODO: Try to using this method.
		return getBounds();
	}

}
