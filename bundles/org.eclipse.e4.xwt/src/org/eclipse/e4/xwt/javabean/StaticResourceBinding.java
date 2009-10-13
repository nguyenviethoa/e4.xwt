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

import java.util.Map;

import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.swt.widgets.Widget;

public class StaticResourceBinding implements IBinding {
	protected Widget widget;
	protected String key;

	public StaticResourceBinding(Widget widget, String key) {
		this.widget = widget;
		this.key = key;
	}

	public Object getValue() {
		Widget parent = widget;		
		while (parent != null) {
			Map<String, Object> dico = UserData.getLocalResources(parent);
			if (dico != null && dico.containsKey(key)) {
				Object data = dico.get(key);
				if (data instanceof IBinding) {
					return ((IBinding) data).getValue();
				}
				return data;
			}
			parent = (Widget) UserData.getParent(parent);
		}
		throw new XWTException("Key " + key + " is not found.");			
	}
}
