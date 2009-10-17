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

import org.eclipse.e4.xwt.internal.utils.UserData;

public class DataProperty extends AbstractProperty {
	protected String key;
	protected Object defaultValue;

	public DataProperty(String name, String key) {
		this(name, Object.class, key, null);
	}

	public DataProperty(String name, Class<?> propertyType, String key) {
		this(name, propertyType, key, null);
	}

	public DataProperty(String name, Class<?> propertyType, String key, Object defaultValue) {
		super(name, propertyType);
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public Object getValue(Object target) {
		Object object = UserData.getLocalData(target, key);
		if (object == null) {
			return defaultValue;
		}
		return object;
	}

	public void setValue(Object target, Object value) {
		Object oldValue = UserData.getLocalData(target, key);
		UserData.setLocalData(target, key, value);
	}
}
