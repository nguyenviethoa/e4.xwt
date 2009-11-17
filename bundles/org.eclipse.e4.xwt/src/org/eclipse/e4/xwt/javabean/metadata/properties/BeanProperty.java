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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.internal.utils.UserData;

public class BeanProperty extends AbstractProperty {
	protected PropertyDescriptor descriptor;

	public BeanProperty(PropertyDescriptor descriptor) {
		super(descriptor.getName(), descriptor.getPropertyType());
		if (descriptor == null)
			throw new NullPointerException();
		this.descriptor = descriptor;
	}

	public void setValue(Object target, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (descriptor != null && descriptor.getWriteMethod() != null) {
			Method writeMethod = descriptor.getWriteMethod();
			// Bug of invoke boolean value.
			Class<?>[] parameterTypes = writeMethod.getParameterTypes();
			if (parameterTypes.length == 1) {
				Class<?> type = parameterTypes[0];
				Class<?> propertyType = getType();
				if (propertyType != Object.class) {
					type = propertyType;
				}
				if (!IBinding.class.isAssignableFrom(propertyType)) {
					IConverter convertor = value == null ? null : XWT.findConvertor(value.getClass(), type);
					if (convertor != null) {
						value = convertor.convert(value);
					}					
				}
				
				Object oldValue = null;
				Method readMethod = descriptor.getReadMethod();
				if (readMethod != null) {
					oldValue = readMethod.invoke(target);
				}
				
				if (value == null && type != null && UserData.getWidget(target) != null) {
					if (type == String.class) {
						value = "";
					}
					else if (type == Boolean.class) {
						value = false;
					}
				}
				
				if (oldValue != value) {
					writeMethod.setAccessible(true);
					writeMethod.invoke(target, value);
					fireSetPostAction(target, this, value);
				}
			}
		}
	}

	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (descriptor != null && descriptor.getReadMethod() != null) {
			Method writeMethod = descriptor.getReadMethod();
			writeMethod.setAccessible(true);
			return writeMethod.invoke(target);
		}
		return null;
	}

	@Override
	public boolean isDefault() {
		return true;
	}
	
	public boolean isReadOnly() {
		return descriptor.getWriteMethod() == null;
	}
}
