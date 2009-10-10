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
				Class<?> paraType = parameterTypes[0];
				if (!IBinding.class.isAssignableFrom(getType())) {
					IConverter convertor = XWT.findConvertor(value == null ? Object.class : value.getClass(), paraType);
					if (convertor != null) {
						value = convertor.convert(value);
					}					
				}
				writeMethod.invoke(target, value);
				fireSetPostAction(target, this, value);
			}
		}
	}

	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (descriptor != null && descriptor.getReadMethod() != null) {
			Method writeMethod = descriptor.getReadMethod();
			return writeMethod.invoke(target);
		}
		return null;
	}

	@Override
	public boolean isDefault() {
		return true;
	}
}
