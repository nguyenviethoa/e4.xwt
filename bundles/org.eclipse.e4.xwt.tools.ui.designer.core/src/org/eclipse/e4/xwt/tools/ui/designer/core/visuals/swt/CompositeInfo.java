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
package org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt;

import org.eclipse.e4.xwt.tools.ui.designer.core.util.Draw2dTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.swt.WidgetLocator;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Scrollable;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class CompositeInfo extends ControlInfo {

	public CompositeInfo(Object visualObject, boolean isRoot) {
		super(visualObject, isRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.visuals.VisualInfo#getClientArea
	 * ()
	 */
	public org.eclipse.draw2d.geometry.Rectangle getClientArea() {
		if (visualObject instanceof Scrollable) {
			// get the display-relative location.
			Rectangle bounds = WidgetLocator.getBounds(
					(Scrollable) visualObject, true);
			Rectangle clientArea = ((Scrollable) visualObject).getClientArea();
			Rectangle calced = ((Scrollable) visualObject).computeTrim(
					bounds.x, bounds.y, clientArea.width, clientArea.height);
			// bug workground, if Shell, the location of clientArea is always
			// (0,0).
			Rectangle correct = new Rectangle(2 * bounds.x - calced.x, 2
					* bounds.y - calced.y, clientArea.width, clientArea.height);
			return Draw2dTools.toDraw2d(correct);
		}
		return super.getClientArea();
	}
}
