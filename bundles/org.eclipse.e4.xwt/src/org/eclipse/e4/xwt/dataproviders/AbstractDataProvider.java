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

import java.util.HashMap;

import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.databinding.BindingContext;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class AbstractDataProvider implements IDataProvider {

	private HashMap<String, Object> properties = new HashMap<String, Object>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.dataproviders.IDataProvider#getBindingContext()
	 */
	public IBindingContext getBindingContext() {
		return new BindingContext();
	}

	public Object getProperty(String property) {
		return properties.get(property);
	}

	public void setProperty(String property, Object value) {
		properties.put(property, value);
	}

	public boolean hasProperty(String property) {
		return properties.containsKey(property);
	}

	public void removeProperty(String property) {
		properties.remove(property);
	}
}
