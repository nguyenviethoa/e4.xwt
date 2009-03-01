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
public class StringToDouble implements IConverter {
	public static StringToDouble instance = new StringToDouble();

	public Object convert(Object fromObject) {
		String str = (String) fromObject;
		if (str == null || str.trim().length() == 0) {
			return 0D;
		}
		return Double.parseDouble(str.trim());
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return Double.class;
	}
}
