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

package org.eclipse.e4.xwt.javabean.metadata;

/**
 * @author chun.wang
 *
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.internal.databinding.beans.JavaBeanObservableValue;

public class DataContextChangeListener implements PropertyChangeListener {
	IObservableValue observeWidget;
	private List<Object> dataContexts;
	private IObservableValue observableValue;

	private String path;

	public DataContextChangeListener(IObservableValue observeWidget, String path, List<Object> dataContexts, IObservableValue observableValue) {
		this.observeWidget = observeWidget;
		this.path = path;
		this.dataContexts = dataContexts;
		this.observableValue = observableValue;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (observableValue == null || dataContexts == null || dataContexts.isEmpty()) {
			return;
		}
		Object source = evt.getSource();
		Object evtOldvalue = evt.getOldValue();
		Object evtNewValue = evt.getNewValue();

		if (!dataContexts.contains(source)) {
			return;
		}
		if (evtOldvalue == observableValue.getValue()) {
			return;
		}
		Object observed = null;
		PropertyDescriptor pd = null;
		if (observableValue instanceof JavaBeanObservableValue) {
			JavaBeanObservableValue javaObservor = (JavaBeanObservableValue) observableValue;
			observed = javaObservor.getObserved();
			pd = javaObservor.getPropertyDescriptor();
		}
		if (observed != null && pd != null) {
			if (evtOldvalue != evtNewValue) {
				Method readMethod = pd.getReadMethod();
				if (readMethod != null) {
					if (observed.getClass() == evtNewValue.getClass()) {
						BindingDataContent(observed, evtNewValue, pd);
					} else if (observed.getClass() == source.getClass()) {
						BindingDataContent(observed, source, pd);
					} else {
						Object bindingResouse = getObservedObj(evtNewValue);
						if (bindingResouse != null) {
							BindingDataContent(observed, bindingResouse, pd);
						}
					}
				}
			}
		}
	}

	private void BindingDataContent(Object observed, Object bindingResouse, PropertyDescriptor pd) {
		dataContexts.remove(observed);
		dataContexts.add(bindingResouse);
		IObservableValue newBinding = BeansObservables.observeValue(bindingResouse, pd.getName());
		if (observeWidget != null) {
			DataBindingContext bindingContext = new DataBindingContext();
			bindingContext.bindValue(observeWidget, newBinding, null, null);
		}
	}

	private Object getObservedObj(Object evtNewValue) {
		String[] paths = path.split("\\.");
		Object object = evtNewValue;
		for (int i = 1; paths.length - 1 > i; i++) {
			String path1 = paths[i];
			String getMethodName = "get" + path1.substring(0, 1).toUpperCase() + path1.substring(1);
			try {
				Method getMethod1 = object.getClass().getDeclaredMethod(getMethodName, new Class[] {});
				if (getMethod1 != null) {
					object = getMethod1.invoke(object, new Object[] {});
					break;
				}
			} catch (Exception e) {
			}
		}
		if (object == evtNewValue) {
			return null;
		}
		return object;
	}
}
