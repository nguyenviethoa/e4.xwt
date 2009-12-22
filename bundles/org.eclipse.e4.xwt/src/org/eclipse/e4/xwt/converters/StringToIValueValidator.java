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
package org.eclipse.e4.xwt.converters;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.IValueValidator;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class StringToIValueValidator implements IConverter {
	public static StringToIValueValidator instance = new StringToIValueValidator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object fromObject) {
		try {
			Class<?> type = XWT.getLoadingContext().loadClass(fromObject.toString());
			return type.newInstance();
		} catch (Exception e) {
			throw new XWTException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	public Object getFromType() {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	public Object getToType() {
		return IValueValidator.class;
	}
}
