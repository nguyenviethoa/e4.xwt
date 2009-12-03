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
package org.eclipse.e4.xwt.tools.ui.designer.utils;

import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.IVisualOffset;
import org.eclipse.gef.EditPart;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class OffsetUtil {

	public static int getXOffset(EditPart part) {
		int x = 0;
		if (part != null && part instanceof VisualEditPart) {
			IVisualInfo visualInfo = ((VisualEditPart) part).getVisualInfo();
			if (visualInfo instanceof IVisualOffset) {
				x = ((IVisualOffset) visualInfo).getXOffset();
			}
		}
		return x;
	}

	public static int getYOffset(EditPart part) {
		int y = 0;
		if (part != null && part instanceof VisualEditPart) {
			IVisualInfo visualInfo = ((VisualEditPart) part).getVisualInfo();
			if (visualInfo instanceof IVisualOffset) {
				y = ((IVisualOffset) visualInfo).getYOffset();
			}
		}
		return y;
	}
}
