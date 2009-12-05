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
package org.eclipse.e4.xwt.tools.ui.designer.providers;

import org.eclipse.e4.xwt.tools.ui.designer.utils.FontUtil;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class FontLabelProvider extends LabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof Font) {
			FontData[] fontData = ((Font) element).getFontData();
			if (fontData != null && fontData.length > 0) {
				return FontUtil.getFontStr(fontData[0]);
			}
		}
		return super.getText(element);
	}

}