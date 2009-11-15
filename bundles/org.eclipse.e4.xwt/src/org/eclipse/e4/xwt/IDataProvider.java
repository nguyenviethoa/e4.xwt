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
package org.eclipse.e4.xwt;

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
	 * Return the data of the provider, this value can be not used for databindings.
	 * 
	 * @param path
	 * @return
	 */
	Object getData(Object target, String path);

	/**
	 * Return the data of the provider, this value can be not used for databindings.
	 * 
	 * @param path
	 * @return
	 */
	void setData(String path, Object value);

	/**
	 * Return the data of the provider, this value can be not used for databindings.
	 * 
	 * @param path
	 * @return
	 */
	void setData(Object target, String path, Object value);

	/**
	 * Return the data type of the provider.lue
	 * 
	 * @param path
	 * @return
	 */
	Class<?> getDataType(String path);

	/**
	 * check if the property is read only.
	 * 
	 * @param path
	 * @return
	 */
	boolean isPropertyReadOnly(String path);

	/**
	 * Create a databinding data with given path.
	 * 
	 * @param valueType
	 * @param path
	 * @param updateSourceTrigger
	 * @return
	 */
	IDataObservableValueBridge observableValueBridge();

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
