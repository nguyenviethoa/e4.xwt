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
package org.eclipse.e4.xwt.converters;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * String to Integer converter
 * 
 * @author yyang
 */
public class StringToFloat implements IConverter {
	public static StringToFloat instance = new StringToFloat();

	public Object convert(Object fromObject) {
		String str = (String) fromObject;
		if (str == null || str.trim().length() == 0) {
			return 0F;
		}
		return Float.parseFloat(str.trim());
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return Float.class;
	}
}
