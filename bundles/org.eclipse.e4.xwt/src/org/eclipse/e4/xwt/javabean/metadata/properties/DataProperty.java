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
package org.eclipse.e4.xwt.javabean.metadata.properties;

import org.eclipse.swt.widgets.Widget;

public class DataProperty extends AbstractProperty {
	protected String key;

	public DataProperty(String name, String key) {
		this(Object.class, name, key);
	}

	public DataProperty(Class<?> propertyType, String name, String key) {
		super(name, propertyType);
		this.key = key;
	}

	public Object getValue(Object target) {
		Widget widget = (Widget) target;
		return widget.getData(key);
	}

	public void setValue(Object target, Object value) {
		Widget widget = (Widget) target;
		widget.setData(key, value);
	}
}
