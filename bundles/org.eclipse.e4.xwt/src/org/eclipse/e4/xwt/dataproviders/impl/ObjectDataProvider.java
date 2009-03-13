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
package org.eclipse.e4.xwt.dataproviders.impl;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.xwt.dataproviders.IObjectDataProvider;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ObjectDataProvider extends AbstractDataProvider implements IObjectDataProvider {

	private Object objectInstance;
	private Class<?> objectType;

	private String methodName;

	private List<Object> methodParameters;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getMethodName()
	 */
	public String getMethodName() {
		return methodName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getMethodParameters()
	 */
	public List<Object> getMethodParameters() {
		return methodParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getObjectInstance()
	 */
	public Object getObjectInstance() {
		if (objectInstance == null && objectType != null) {
			try {
				objectInstance = objectType.newInstance();
			} catch (Exception e) {
			}
		}
		return objectInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getObjectType()
	 */
	public Class<?> getObjectType() {
		if (objectType == null && objectInstance != null) {
			objectType = objectInstance.getClass();
		}
		return objectType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setMethodName(java.lang.String)
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setMethodParameters(java.util.List)
	 */
	public void setMethodParameters(List<Object> parameters) {
		this.methodParameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setObjectInstance(java.lang.Object)
	 */
	public void setObjectInstance(Object objectImstance) {
		this.objectInstance = objectImstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setObjectType(java.lang.Class)
	 */
	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}

	private Object getTarget() {
		Object target = getObjectInstance();
		if (target == null) {
			return null;
		}
		Class<?> targetType = getObjectType();
		Method method = null;
		if (methodName != null) {
			List<Class<?>> paras = new ArrayList<Class<?>>();
			if (methodParameters != null) {
				for (Object p : methodParameters) {
					paras.add(p.getClass());
				}
			}
			try {
				if (paras.isEmpty()) {
					method = targetType.getDeclaredMethod(methodName, new Class<?>[0]);
					return method.invoke(target, new Object[0]);
				} else {
					method = targetType.getDeclaredMethod(methodName, paras.toArray(new Class<?>[paras.size()]));
					return method.invoke(target, methodParameters.toArray(new Object[methodParameters.size()]));
				}
			} catch (Exception e) {
			}
		}
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider#getData(java.lang.String)
	 */
	public Object getData(String path) {
		Object target = getTarget();
		int index = path.indexOf(".");
		while (index != -1 && target != null) {
			target = getValue(path.substring(0, index), target);
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		if (target == null) {
			return null;
		}
		return getValue(path, target);
	}

	private Object getValue(String path, Object target) {
		Class<?> type = target.getClass();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (path.equals(pd.getName())) {
					Method readMethod = pd.getReadMethod();
					Object value = readMethod.invoke(target, null);
					return value;
				}
			}
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (path.equals(field.getName())) {
					Object object = field.get(target);
					return object;
				}
			}
		} catch (Exception e) {
		}
		throw new IllegalArgumentException("Can't get value from class['" + target.getClass().getName() + "'] by using path['" + path + "'].");
	}

}
