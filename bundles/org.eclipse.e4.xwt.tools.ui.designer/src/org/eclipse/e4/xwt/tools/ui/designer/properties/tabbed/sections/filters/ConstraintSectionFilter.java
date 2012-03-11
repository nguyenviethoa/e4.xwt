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
package org.eclipse.e4.xwt.tools.ui.designer.properties.tabbed.sections.filters;

import org.eclipse.jface.viewers.IFilter;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class ConstraintSectionFilter implements IFilter {

	public boolean select(Object toTest) {
		// if (toTest instanceof WidgetEditPart) {
		// LayoutType layoutType = LayoutsHelper.getLayoutType(((WidgetEditPart) toTest).getParent());
		// if (layoutType == LayoutType.NullLayout) {
		// return true;
		// }
		// }
		return true;
	}

}
