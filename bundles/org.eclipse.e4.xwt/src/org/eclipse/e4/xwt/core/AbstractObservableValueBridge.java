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
package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.xwt.IDataObservableValueBridge;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.databinding.ObservableValueFactory;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;

public abstract class AbstractObservableValueBridge implements
		IDataObservableValueBridge {

	final public IObservableValue observe(Object data, String path, Class<?> elementType) {
		Class<?> type = null;
		if (elementType == null) {
			type = ObservableValueFactory.toType(data);
		}
		else {
			type = elementType;
		}
		IMetaclass metaclass = XWT.getMetaclass(type);
		IProperty property = metaclass.findProperty(path);
		if (property == null) {
			throw new XWTException(" Property \"" + path + "\" is not found in the class " + metaclass.getType().getName());
		}
		Class<?> propertyType = property.getType();
		
		if (data instanceof IObservableValue) {
			IObservableValue observableValue = (IObservableValue) data;
			return observeDetailValue(observableValue, type, path, propertyType);
		}
		
		return observeValue(data, path);
	}
	
	protected abstract IObservableValue observeValue(Object bean, String propertyName);
	
	protected abstract IObservableValue observeDetailValue(IObservableValue bean, Class<?> elementType, String propertyName, Class<?> propertyType);

	// TODO to remove
	public IValueProperty createValueProperty(Object type, String fullPath) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("to remove this method");
	}

}
