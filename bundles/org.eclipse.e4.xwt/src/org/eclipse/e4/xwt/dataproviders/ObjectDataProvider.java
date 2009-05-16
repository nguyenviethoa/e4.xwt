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
package org.eclipse.e4.xwt.dataproviders;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.databinding.IBindingContext;
import org.eclipse.e4.xwt.databinding.ObjectBindingContext;
import org.eclipse.e4.xwt.dataproviders.observable.BeanObservableValue;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ObjectDataProvider extends AbstractDataProvider implements IObjectDataProvider {

	private Object objectInstance;
	private Class<?> objectType;

	private String methodName;

	private List<Object> methodParameters;

	private ObjectBindingContext bindingContext = new ObjectBindingContext();

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
			target = BeanObservableValue.getValue(target, path.substring(0, index));
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		return BeanObservableValue.getValue(target, path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider#getDataType(java.lang.String)
	 */
	public Class<?> getDataType(String path) {
		Object target = getTarget();
		if (target == null) {
			return null;
		}		
		Class<?> type = target.getClass();
		if (path == null) {
			return type;
		}
		int index = path.indexOf(".");
		while (index != -1 && target != null) {
			type = BeanObservableValue.getValueType(type, path.substring(0, index));
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		return BeanObservableValue.getValueType(type, path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.impl.AbstractDataProvider#createObservableValue(java.lang.String)
	 */
	public IObservableValue createObservableValue(Object valueType, String fullPath) {
		Object dataContext = getTarget();
		String propertyName = null;
		String[] paths = fullPath.trim().split("\\.");
		if (paths.length > 1) {
			for (int i = 0; i < paths.length - 1; i++) {
				String path = paths[i];
				if (dataContext != null) {
					bindingContext.addObservable(dataContext);
					dataContext = BeanObservableValue.getValue(dataContext, path);
				}
			}
			propertyName = paths[paths.length - 1];
		} else if (paths.length == 1) {
			propertyName = fullPath;
		}
		if (isBeanSupport(dataContext)) {
			return BeansObservables.observeValue(XWT.realm, dataContext, propertyName);
		}
		return new BeanObservableValue(valueType, dataContext, propertyName);
	}

	private boolean isBeanSupport(Object target) {
		Method method = null;
		try {
			try {
				method = target.getClass().getMethod("addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class });
			} catch (NoSuchMethodException e) {
				method = target.getClass().getMethod("addPropertyChangeListener", new Class[] { PropertyChangeListener.class });
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return method != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider#getBindingContext()
	 */
	public IBindingContext getBindingContext() {
		return bindingContext;
	}
}
