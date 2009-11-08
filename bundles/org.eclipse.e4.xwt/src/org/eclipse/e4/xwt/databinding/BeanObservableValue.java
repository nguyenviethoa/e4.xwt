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
package org.eclipse.e4.xwt.databinding;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class BeanObservableValue extends XWTObservableValue {

	private String propertyName;

	/**
	 * @param observed
	 */
	public BeanObservableValue(Class<?> valueType, Object observed, String propertyName) {
		super(valueType, observed, propertyName);
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
				if (propertyName.equalsIgnoreCase(pd.getName())) {
					Method readMethod = pd.getReadMethod();
					if (readMethod != null) {
						return readMethod.invoke(target);
					}
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (propertyName.equalsIgnoreCase(field.getName())) {
					Object object = field.get(target);
					return object;
				}
			}
			return UserData.getLocalData(target, propertyName);
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	public static Class<?> getValueType(Class<?> type, String propertyName) {
		if (type == null || propertyName == null || propertyName.indexOf(".") != -1) {
			return null;
		}
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (propertyName.equalsIgnoreCase(pd.getName())) {
					return pd.getPropertyType();
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (propertyName.equalsIgnoreCase(field.getName())) {
					return field.getType();
				}
			}
			IMetaclass metaclass = XWT.getMetaclass(type);
			IProperty property = metaclass.findProperty(propertyName);
			if (property != null) {
				return property.getType();
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	public static boolean isPropertyReadOnly(Class<?> type, String propertyName) {
		if (type == null || propertyName == null || propertyName.indexOf(".") != -1) {
			return true;
		}
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (propertyName.equals(pd.getName())) {
					return pd.getWriteMethod() == null;
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (propertyName.equals(field.getName())) {
					return !Modifier.isPublic(field.getModifiers());
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.observable.XWTObservableValue#doSetApprovedValue(java.lang.Object)
	 */
	protected void doSetApprovedValue(Object value) {
		Object observed = getObserved();
		setValue(observed, propertyName, value);
	}

	public static void setValue(Object target, String propertyName, Object value) {
		Class<?> type = target.getClass();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (propertyName.equals(pd.getName())) {
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod == null) {
						return;
					}
					if (!writeMethod.isAccessible()) {
						writeMethod.setAccessible(true);
					}
					Class<?>[] parameterTypes = writeMethod.getParameterTypes();
					Class targetType = parameterTypes[0];
					if (targetType != value.getClass()) {
						if (targetType.isEnum() && value instanceof String) {
							try {
								writeMethod.invoke(target, new Object[] { Enum.valueOf(targetType, (String) value) });
								return;
							} catch (Exception e) {
							}
						}
						IConverter c = XWT.findConvertor(value.getClass(), targetType);
						if (c != null) {
							value = c.convert(value);
						}
					}
					writeMethod.invoke(target, new Object[] { value });
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
							field.set(target, Enum.valueOf(fieldType, (String) value));
							return;
						} catch (Exception e) {
						}
					}
					IConverter c = XWT.findConvertor(value.getClass(), fieldType);
					if (c != null) {
						value = c.convert(value);
					}
					field.set(target, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPropertyName() {
		return propertyName;
	}
}
