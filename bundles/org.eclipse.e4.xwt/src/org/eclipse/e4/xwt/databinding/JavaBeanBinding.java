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
package org.eclipse.e4.xwt.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.utils.LoggerManager;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class JavaBeanBinding extends AbstractDataBinding {
	private static Map<Object, BindingContext> bindingContext = new HashMap<Object, BindingContext>();

	public JavaBeanBinding(Object source, Widget target, String path) {
		super(source, target, path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.databinding.DataBinding#createObservableSource()
	 */
	protected IObservableValue createObservableSource() {
		Object dataContext = getSource();
		String fullPath = getPath();
		String propertyName = null;
		String[] paths = fullPath.trim().split("\\.");
		if (paths.length > 1) {
			for (int i = 0; i < paths.length - 1; i++) {
				String path = paths[i];
				if (dataContext != null) {
					bindingContext.put(dataContext, new BindingContext(dataContext));
					dataContext = getObserveData(dataContext, path);
				}
			}
			propertyName = paths[paths.length - 1];
		} else if (paths.length == 1) {
			propertyName = fullPath;
		}
		IObservableValue observeSource = BeansObservables.observeValue(dataContext, propertyName);
		BindingContext bc = new BindingContext(dataContext);
		bc.observeValue = observeSource;
		bc.observeWidget = getObservableWidget();
		bc.propertyName = propertyName;
		bindingContext.put(dataContext, bc);
		return observeSource;
	}

	private Object getObserveData(Object dataContext, String path) {
		try {
			Class<?> dataContextClass = dataContext.getClass();
			String getMethiodName = "get" + path.substring(0, 1).toUpperCase() + path.substring(1);
			Method getMethod = dataContextClass.getDeclaredMethod(getMethiodName, new Class[] {});
			if (getMethod != null) {
				return getMethod.invoke(dataContext, new Object[] {});
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	static class BindingContext {
		String propertyName;
		Object source;
		IObservableValue observeValue;
		IObservableValue observeWidget;

		public BindingContext(Object source) {
			this.source = source;
			addListener(source);
		}

		public void setNewValue(Object newValue) {
			if (newValue != null && newValue.getClass() == source.getClass() && observeValue != null && propertyName != null && observeWidget != null) {

				Field[] fields = source.getClass().getDeclaredFields();
				for (Field field : fields) {
					Object oldPropertyValue = getPropertyValue(source, field.getName());
					Object newPropertyValue = getPropertyValue(newValue, field.getName());
					if (oldPropertyValue != null && oldPropertyValue != newPropertyValue) {
						BindingContext bc = bindingContext.get(oldPropertyValue);
						if (bc != null) {
							bc.setNewValue(newPropertyValue);
						}
					}
				}

				observeValue = BeansObservables.observeValue(newValue, propertyName);
				addListener(newValue);
				source = newValue;
				bindingContext.put(source, this);
				if (observeWidget != null) {
					DataBindingContext bindingContext = new DataBindingContext();
					bindingContext.bindValue(observeWidget, observeValue, null, null);
				}
			}
		}

		private Object getPropertyValue(Object object, String propertyName) {
			String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				Method getMethod1 = object.getClass().getDeclaredMethod(getMethodName, new Class[] {});
				if (getMethod1 != null) {
					return getMethod1.invoke(object, new Object[] {});
				}
			} catch (Exception e) {
			}
			return null;
		}

		private void applyNewValue(Object oldValue, Object newValue) {
			BindingContext bc = bindingContext.get(oldValue);
			if (bc == null) {
				if (oldValue.getClass() == newValue.getClass() && oldValue.getClass() != String.class) {
					// children, ...
					Field[] fields = oldValue.getClass().getDeclaredFields();
					for (Field field : fields) {
						Object oldPropertyValue = getPropertyValue(oldValue, field.getName());
						Object newPropertyValue = getPropertyValue(newValue, field.getName());
						if (oldPropertyValue != null && oldPropertyValue != newPropertyValue) {
							applyNewValue(oldPropertyValue, newPropertyValue);
						}
					}
				}
				return;
			}
			bc.setNewValue(newValue);
		}

		public void addListener(Object dataContext) {
			if (dataContext == null) {
				return;
			}
			PropertyChangeListener p = new PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent evt) {
					Object oldValue = evt.getOldValue();
					Object newValue = evt.getNewValue();
					if (oldValue == newValue) {
						return;
					}
					applyNewValue(oldValue, newValue);
				}
			};

			Class<?> dataContextClass = dataContext.getClass();
			Field[] fields = dataContextClass.getDeclaredFields();
			try {
				Method addListenerMethod = dataContextClass.getDeclaredMethod("addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class });
				try {
					for (Field field : fields) {
						if (!PropertyChangeSupport.class.equals(field.getType())) {
							addListenerMethod.invoke(dataContext, new Object[] { field.getName(), p });
						}
					}
				} catch (Exception e) {
					LoggerManager.log(e);
				}
			} catch (Exception e) {
			}
		}
	}
}
