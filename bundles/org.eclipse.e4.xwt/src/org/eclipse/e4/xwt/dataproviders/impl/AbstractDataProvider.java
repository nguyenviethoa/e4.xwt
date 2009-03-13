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
package org.eclipse.e4.xwt.dataproviders.impl;

import org.eclipse.e4.xwt.dataproviders.IDataProvider;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class AbstractDataProvider implements IDataProvider {

	private String key;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#getKey()
	 */
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#setKey(java.lang.String)
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
