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

import org.eclipse.e4.xwt.tools.ui.designer.layouts.LayoutType;
import org.eclipse.e4.xwt.tools.ui.designer.layouts.LayoutsHelper;
import org.eclipse.e4.xwt.tools.ui.designer.layouts.pages.AssistancePageFactory;
import org.eclipse.e4.xwt.tools.ui.designer.parts.WidgetEditPart;
import org.eclipse.jface.viewers.IFilter;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class LayoutSectionFilter implements IFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(Object toTest) {
		if (toTest instanceof WidgetEditPart) {
			LayoutType layoutType = LayoutsHelper.getLayoutType((WidgetEditPart) toTest);
			return AssistancePageFactory.isSupport(layoutType);
		}
		return false;
	}

}
