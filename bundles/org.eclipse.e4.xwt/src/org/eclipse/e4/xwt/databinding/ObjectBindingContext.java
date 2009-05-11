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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.IBeanObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.utils.LoggerManager;
import org.eclipse.e4.xwt.utils.ObjectUtil;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class ObjectBindingContext extends BindingContext {

	private static Map<Object, ObjectBindingContext> bindingContext = new HashMap<Object, ObjectBindingContext>();

	private String propertyName;
	private Object observed;

	public void setNewValue(Object newValue) {
		if (newValue != null && newValue.getClass() == observed.getClass() && observeValue != null && propertyName != null && observeWidget != null) {

			Field[] fields = observed.getClass().getDeclaredFields();
			for (Field field : fields) {
				Object oldPropertyValue = getPropertyValue(observed, field.getName());
				Object newPropertyValue = getPropertyValue(newValue, field.getName());
				if (oldPropertyValue != null && oldPropertyValue != newPropertyValue) {
					ObjectBindingContext bc = bindingContext.get(oldPropertyValue);
					if (bc != null) {
						bc.setNewValue(newPropertyValue);
					}
				}
			}

			observeValue = BeansObservables.observeValue(newValue, propertyName);
			addListener(newValue);
			observed = newValue;
			bindingContext.put(observed, this);
			if (observeWidget != null) {
				DataBindingContext bindingContext = new DataBindingContext();
				bindingContext.bindValue(observeWidget, observeValue, null, null);
			}
		}
	}

	private Object getPropertyValue(Object object, String propertyName) {
		try {
			Method getMethod1 = ObjectUtil.findGetter(object.getClass(), propertyName, null);
			if (getMethod1 != null) {
				return getMethod1.invoke(object, new Object[] {});
			}
		} catch (Exception e) {
		}
		return null;
	}

	private void applyNewValue(Object oldValue, Object newValue) {
		ObjectBindingContext bc = bindingContext.get(oldValue);
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

	public void addObservable(Object observable) {
		ObjectBindingContext bbc = new ObjectBindingContext();
		bbc.observed = observable;
		bbc.addListener(observable);
		bindingContext.put(observable, bbc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider.BindingContext#bind(org.eclipse.core.databinding.observable.value.IObservableValue, org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public void bind(IObservableValue source, IObservableValue target, String mode) {
		super.bind(source, target, mode);
		if (source instanceof IBeanObservable) {
			IBeanObservable beanValue = (IBeanObservable) source;
			propertyName = beanValue.getPropertyDescriptor().getName();
			observed = beanValue.getObserved();
			bindingContext.put(observed, this);
			addListener(observed);
		}
	}
}
