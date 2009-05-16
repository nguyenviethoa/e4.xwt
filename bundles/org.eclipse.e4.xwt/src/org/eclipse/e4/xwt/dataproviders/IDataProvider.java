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
package org.eclipse.e4.xwt.dataproviders;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.databinding.IBindingContext;

/**
 * A Data Binding provider defines the nature of Data Binding such as Bean Object Binding, EMF Object Binding, XML data Binding or Data Base binding
 * 
 * @author yyang
 */
public interface IDataProvider {
	
	/**
	 * Return the data of the provider, this value can be not used for databindings.
	 * 
	 * @param path
	 * @return
	 */
	Object getData(String path);
	
	/**
	 * Return the data type of the provider.
	 * 
	 * @param path
	 * @return
	 */
	Class<?> getDataType(String path);

	/**
	 * Create a databinding data with given path.
	 * 
	 * @param path
	 * @return
	 */
	IObservableValue createObservableValue(Object valueType, String path);

	/**
	 * Process context of databindings.
	 * 
	 * @return
	 */
	IBindingContext getBindingContext();

	Object getProperty(String property);

	void setProperty(String property, Object object);

	boolean hasProperty(String property);

	void removeProperty(String property);
}
