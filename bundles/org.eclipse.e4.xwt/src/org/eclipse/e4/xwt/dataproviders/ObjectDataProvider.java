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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataObservableValueBridge;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.core.AbstractObservableValueBridge;
import org.eclipse.e4.xwt.databinding.ObjectBindingContext;
import org.eclipse.e4.xwt.databinding.ObservableValueFactory;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ObjectDataProvider extends AbstractDataProvider implements
		IObjectDataProvider {

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
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getMethodParameters
	 * ()
	 */
	public List<Object> getMethodParameters() {
		return methodParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#getObjectInstance()
	 */
	public Object getObjectInstance() {
		if (objectInstance == null && objectType != null) {
			try {
				objectInstance = objectType.newInstance();
			} catch (Exception e) {
				throw new XWTException(e);
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
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setMethodName(java
	 * .lang.String)
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setMethodParameters
	 * (java.util.List)
	 */
	public void setMethodParameters(List<Object> parameters) {
		this.methodParameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setObjectInstance
	 * (java.lang.Object)
	 */
	public void setObjectInstance(Object objectImstance) {
		this.objectInstance = objectImstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IObjectDataProvider#setObjectType(java
	 * .lang.Class)
	 */
	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}

	protected Object getTarget() {
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
					method = targetType.getDeclaredMethod(methodName);
					return method.invoke(target);
				} else {
					method = targetType.getDeclaredMethod(methodName, paras
							.toArray(new Class<?>[paras.size()]));
					return method.invoke(target, methodParameters
							.toArray(new Object[methodParameters.size()]));
				}
			} catch (Exception e) {
			}
		}
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IDataProvider#getData(java.lang.String)
	 */
	public Object getData(String path) {
		return getData(getTarget(), path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IDataProvider#getData(java.lang.String)
	 */
	public Object getData(Object object, String path) {
		if (object instanceof IObservableValue) {
			object = ((IObservableValue) object).getValue();
		}
		if (path == null || path.trim().length() == 0) {
			return ObservableValueFactory.getValue(object, null);			
		}
		int index = path.indexOf(".");
		while (index != -1 && object != null) {
			object = ObservableValueFactory.getValue(object, path.substring(0,
					index));
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		return ObservableValueFactory.getValue(object, path);
	}

	public void setData(Object object, String path, Object value) {
		if (object instanceof IObservableValue) {
			object = ((IObservableValue) object).getValue();
		}
		int index = path.indexOf(".");
		while (index != -1 && object != null) {
			object = ObservableValueFactory.getValue(object, path.substring(0,
					index));
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		ObservableValueFactory.setValue(object, path, value);
	}

	public void setData(String path, Object value) {
		setData(getTarget(), path, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.dataproviders.IDataProvider#getDataType(java.lang.
	 * String)
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
			type = ObservableValueFactory.getValueType(type, path.substring(0,
					index));
			if (type == null) {
				type = Object.class;
			}
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		return ObservableValueFactory.getValueType(type, path);
	}

	/**
	 * check if the property is read only
	 * 
	 * @param path
	 * @return
	 */
	public boolean isPropertyReadOnly(String path) {
		Object target = getTarget();
		if (target == null) {
			return true;
		}
		Class<?> type = target.getClass();
		if (path == null) {
			return true;
		}
		int index = path.indexOf(".");
		while (index != -1 && target != null) {
			type = ObservableValueFactory.getValueType(type, path.substring(0,
					index));
			path = path.substring(index + 1);
			index = path.indexOf(".");
		}
		return ObservableValueFactory.isPropertyReadOnly(type, path);
	}

	protected IDataObservableValueBridge createObservableValueFactory() {
		return new AbstractObservableValueBridge() {
			@Override
			protected IObservableValue observeValue(Object bean, String propertyName) {
				if (ObservableValueFactory.isBeanSupport(bean)) {
					return BeansObservables.observeValue(XWT.getRealm(), bean, propertyName);
				}		
				return PojoObservables.observeValue(XWT.getRealm(), bean, propertyName);
			}
						
			@Override
			protected IObservableValue observeDetailValue(IObservableValue master, Class<?> elementType,
					String propertyName, Class<?> propertyType) {
				Class beanClass = elementType;
				if (beanClass == null && master.getValueType() instanceof Class) {
					beanClass = (Class) master.getValueType();
				}
				if (ObservableValueFactory.isBeanSupport(elementType)) {
					return BeanProperties.value(beanClass, propertyName, propertyType).observeDetail(master);
				}
				return PojoProperties.value(beanClass, propertyName, propertyType)
						.observeDetail(master);
			}
			
			public IValueProperty createValueProperty(Object type, String propertyName) {
				if (ObservableValueFactory.isBeanSupport(type)) {
					return BeanProperties.value(ObservableValueFactory.toType(type), propertyName);
				}		
				return PojoProperties.value(ObservableValueFactory.toType(type), propertyName);
			}
		};
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
