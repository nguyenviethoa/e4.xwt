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


/**
 * A Data Binding provider defines the nature of Data Binding such as Bean Object Binding, EMF Object Binding, XML data Binding or Data Base binding
 * 
 * @author yyang
 */
public interface IDataProvider {

	void setKey(String key);

	String getKey();

	Object getData(String path);

}