/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.javabean.metadata.properties;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;

/**
 * @author jliu
 * 
 */
public class FieldProperty extends AbstractProperty {

	private Field field;

	public FieldProperty(Field field) {
		super(field.getName(), field.getType());
		if (field == null) {
			throw new NullPointerException();
		}
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.metadata.IProperty#getValue(java.lang.Object)
	 */
	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		return field.get(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.metadata.IProperty#setValue(java.lang.Object, java.lang.Object)
	 */
	public void setValue(Object target, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (value != null) {
			Class<?> fieldType = field.getType();
			Class<?> valueType = value.getClass();
			if (!fieldType.isAssignableFrom(value.getClass())) {
				IConverter converter = XWT.findConvertor(valueType, fieldType);
				if (converter != null) {
					value = converter.convert(value);
				} else {
					throw new XWTException("Converter " + valueType.getName() + "->" + fieldType.getName());
				}
			}
		}
		field.set(target, value);
		fireSetPostAction(target, this, value);
	}

	@Override
	public boolean isDefault() {
		return true;
	}
}
