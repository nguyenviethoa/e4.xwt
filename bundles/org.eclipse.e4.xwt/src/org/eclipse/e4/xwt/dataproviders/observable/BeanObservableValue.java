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
package org.eclipse.e4.xwt.dataproviders.observable;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class BeanObservableValue extends XWTObservableValue {

	private String propertyName;

	/**
	 * @param observed
	 */
	public BeanObservableValue(Object valueType, Object observed, String propertyName) {
		super(valueType, observed);
		this.propertyName = propertyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.observable.XWTObservableValue#doGetValue()
	 */
	protected Object doGetValue() {
		Object value = getValue(getObserved(), propertyName);
		if (value != null) {
			return value;
		}
		return null;
	}

	public static Object getValue(Object target, String propertyName) {
		if (target == null || propertyName == null || propertyName.indexOf(".") != -1) {
			return null;
		}
		Class<?> type = target.getClass();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (propertyName.equals(pd.getName())) {
					Method readMethod = pd.getReadMethod();
					return readMethod.invoke(target, null);
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (propertyName.equals(field.getName())) {
					Object object = field.get(target);
					return object;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.observable.XWTObservableValue#doSetApprovedValue(java.lang.Object)
	 */
	protected void doSetApprovedValue(Object value) {
		Object observed = getObserved();
		Class<?> type = observed.getClass();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (propertyName.equals(pd.getName())) {
					Method writeMethod = pd.getWriteMethod();
					if (!writeMethod.isAccessible()) {
						writeMethod.setAccessible(true);
					}
					Class<?>[] parameterTypes = writeMethod.getParameterTypes();
					Class targetType = parameterTypes[0];
					if (targetType != value.getClass()) {
						if (targetType.isEnum() && value instanceof String) {
							try {
								writeMethod.invoke(observed, new Object[] { Enum.valueOf(targetType, (String) value) });
								return;
							} catch (Exception e) {
							}
						}
						IConverter c = XWT.findConvertor(value.getClass(), targetType);
						if (c != null) {
							value = c.convert(value);
						}
					}
					writeMethod.invoke(observed, new Object[] { value });
					return;
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (propertyName.equals(field.getName())) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					Class fieldType = field.getType();
					if (fieldType.isEnum() && value instanceof String) {
						try {
							field.set(observed, Enum.valueOf(fieldType, (String) value));
							return;
						} catch (Exception e) {
						}
					}
					IConverter c = XWT.findConvertor(value.getClass(), fieldType);
					if (c != null) {
						value = c.convert(value);
					}
					field.set(observed, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
