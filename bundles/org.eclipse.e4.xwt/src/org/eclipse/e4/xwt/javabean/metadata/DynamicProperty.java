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
package org.eclipse.e4.xwt.javabean.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicProperty extends AbstractProperty {

	private Class<?> propertyType;
	private final Method setter;
	private final Method getter;

	public DynamicProperty(Class<?> type, Class<?> propertyType, String name) {
		this(propertyType, createSetter0(type, propertyType, name), createGetter0(propertyType, name), name);
	}

	public DynamicProperty(Class<?> propertyType, Method setter, Method getter, String name) {
		super(name);
		this.propertyType = propertyType;
		this.setter = setter;
		this.getter = getter;
	}

	public void setValue(Object target, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		setter.invoke(target, value);
		fireSetPostAction(target, this, value);
	}

	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		return getter.invoke(target, null);
	}

	public Class<?> getType() {
		return propertyType;
	}

	public void setType(Class<?> type) {
		this.propertyType = type;
	}

	private static Method createSetter0(Class<?> type, Class<?> propertyType, String name) {
		try {
			return createSetter(type, propertyType, name);
		} catch (Exception e) {
		}
		return null;
	}

	public static Method createSetter(Class<?> type, Class<?> propertyType, String name) throws SecurityException, NoSuchMethodException {
		return type.getMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), propertyType);
	}

	private static Method createGetter0(Class<?> type, String name) {
		try {
			return createGetter(type, name);
		} catch (Exception e) {
		}
		return null;
	}

	public static Method createGetter(Class<?> type, String name) throws SecurityException, NoSuchMethodException {
		return type.getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1), null);
	}
}
