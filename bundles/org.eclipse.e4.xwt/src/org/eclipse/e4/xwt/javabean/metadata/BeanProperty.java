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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class BeanProperty extends AbstractProperty {
	protected PropertyDescriptor descriptor;
	private Field field;
	private Class<?> type;

	public BeanProperty(PropertyDescriptor descriptor) {
		super(descriptor.getName());
		if (descriptor == null)
			throw new NullPointerException();
		this.descriptor = descriptor;
	}

	public BeanProperty(Field field) {
		super(field.getName());
		if (field == null)
			throw new NullPointerException();
		this.field = field;
	}

	public void setValue(Object target, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (descriptor != null && descriptor.getWriteMethod() != null) {
			Method writeMethod = descriptor.getWriteMethod();
			// Bug of invoke boolean value.
			Class<?>[] parameterTypes = writeMethod.getParameterTypes();
			if (parameterTypes.length == 1) {
				Class<?> paraType = parameterTypes[0];
				IConverter convertor = XWT.findConvertor(value == null ? Object.class : value.getClass(), paraType);
				if (convertor != null) {
					value = convertor.convert(value);
				}
				writeMethod.invoke(target, value);
				fireSetPostAction(target, this, value);
			}
		} else if (field != null) {
			if (field.getType() != value.getClass())
				value = XWT.findConvertor(value.getClass(), field.getType()).convert(value);
			field.set(target, value);
			fireSetPostAction(target, this, value);
		}
	}

	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (descriptor != null && descriptor.getReadMethod() != null) {
			Method writeMethod = descriptor.getReadMethod();
			return writeMethod.invoke(target);
		} else if (field != null) {
			return field.get(target);
		}
		return null;
	}

	public Class<?> getType() {
		if (type != null) {
			return type;
		}
		if (descriptor == null) {
			type = field.getType();
		} else {
			type = descriptor.getPropertyType();
		}
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
