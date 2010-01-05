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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartFeedback extends RectangleFigure {

	public PartFeedback(Rectangle bounds) {
		setLineWidth(3);
		setFill(false);
		setForegroundColor(ColorConstants.lightBlue);
		if (bounds != null) {
			setBounds(bounds);
		}
	}
}
