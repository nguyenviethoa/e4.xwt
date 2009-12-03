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
package org.eclipse.e4.xwt.tools.ui.designer.properties.tabbed.sections.filters;

import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.tools.ui.designer.parts.WidgetEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTUtility;
import org.eclipse.jface.viewers.IFilter;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class TextSectionFilter implements IFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(Object toTest) {
		if (toTest instanceof WidgetEditPart) {
			IProperty property = XWTUtility.getProperty(((WidgetEditPart) toTest).getCastModel(), "text");
			if (property != null) {
				return true;
			}
		}
		return false;
	}

}
