/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.javabean;

import org.eclipse.e4.xwt.IBinding;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.swt.widgets.Widget;

public class StaticResourceBinding implements IBinding {
	protected Widget widget;
	protected String key;

	public StaticResourceBinding(Widget widget, String key) {
		this.widget = widget;
		this.key = key;
	}

	private ResourceDictionary getResourceDictionary(Widget widget) {
		ResourceDictionary data = (ResourceDictionary) widget.getData(IConstants.XWT_RESOURCES_KEY);
		Widget parent = widget;
		while (data == null && (parent = (Widget) parent.getData(IConstants.XWT_PARENT_KEY)) != null)
			data = (ResourceDictionary) ((Widget) parent).getData(IConstants.XWT_RESOURCES_KEY);
		return data;
	}

	public Object getValue() {
		ResourceDictionary dico = getResourceDictionary(widget);
		if (dico == null) {
			throw new XWTException("Can not find resource dictionary");
		}
		Object data = dico.get(key);
		if (data instanceof IBinding) {
			return ((IBinding) data).getValue();
		}
		return data;
	}
}
