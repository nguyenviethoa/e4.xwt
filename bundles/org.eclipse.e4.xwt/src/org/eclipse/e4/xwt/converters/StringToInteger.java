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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.maps.XWTMaps;
import org.eclipse.swt.layout.GridData;

public class StringToInteger implements IConverter {
	private static final String GRIDDATA_PREFIX = "GRIDDATA.";
	private static final String STYLES_SEP = "|";
	public static StringToInteger instance = new StringToInteger();

	public Object convert(Object fromObject) {
		String str = (String) fromObject;
		if (str.indexOf(STYLES_SEP) != -1) {
			List<String> values = new ArrayList<String>();
			StringTokenizer stk = new StringTokenizer(str, STYLES_SEP);
			while (stk.hasMoreTokens()) {
				values.add(stk.nextToken());
			}
			int result = 0;
			for (String value : values) {
				result |= convertInt(value);
			}
			return result;
		}
		return convertInt(str);
	}

	private int convertInt(String str) {
		if (str == null || str.equals("")) {
			return 0;
		}
		try {
			// Quick solution for numbers.
			return Integer.parseInt(str.trim());
		} catch (NumberFormatException e) {
			str = str.toUpperCase().trim();
			if (str.startsWith(GRIDDATA_PREFIX)) {
				return convertGridDataInt(str);
			}
			return defaultConvertInt(str);
		}
	}

	private int convertGridDataInt(String str) {
		if ("GridData.BEGINNING".equalsIgnoreCase(str)) {
			return GridData.BEGINNING;
		} else if ("GridData.CENTER".equalsIgnoreCase(str)) {
			return GridData.CENTER;
		} else if ("GridData.END".equalsIgnoreCase(str)) {
			return GridData.END;
		} else if ("GridData.FILL".equalsIgnoreCase(str)) {
			return GridData.FILL;
		} else if ("GridData.FILL_BOTH".equalsIgnoreCase(str)) {
			return GridData.FILL_BOTH;
		} else if ("GridData.FILL_HORIZONTAL".equalsIgnoreCase(str)) {
			return GridData.FILL_HORIZONTAL;
		} else if ("GridData.GRAB_HORIZONTAL".equalsIgnoreCase(str)) {
			return GridData.GRAB_HORIZONTAL;
		} else if ("GridData.GRAB_VERTICAL".equalsIgnoreCase(str)) {
			return GridData.GRAB_VERTICAL;
		} else if ("GridData.HORIZONTAL_ALIGN_BEGINNING".equalsIgnoreCase(str)) {
			return GridData.HORIZONTAL_ALIGN_BEGINNING;
		} else if ("GridData.HORIZONTAL_ALIGN_CENTER".equalsIgnoreCase(str)) {
			return GridData.HORIZONTAL_ALIGN_CENTER;
		} else if ("GridData.HORIZONTAL_ALIGN_END".equalsIgnoreCase(str)) {
			return GridData.HORIZONTAL_ALIGN_END;
		} else if ("GridData.HORIZONTAL_ALIGN_FILL".equalsIgnoreCase(str)) {
			return GridData.HORIZONTAL_ALIGN_FILL;
		} else if ("GridData.VERTICAL_ALIGN_BEGINNING".equalsIgnoreCase(str)) {
			return GridData.VERTICAL_ALIGN_BEGINNING;
		} else if ("GridData.VERTICAL_ALIGN_CENTER".equalsIgnoreCase(str)) {
			return GridData.VERTICAL_ALIGN_CENTER;
		} else if ("GridData.VERTICAL_ALIGN_END".equalsIgnoreCase(str)) {
			return GridData.VERTICAL_ALIGN_END;
		} else if ("GridData.VERTICAL_ALIGN_FILL".equalsIgnoreCase(str)) {
			return GridData.VERTICAL_ALIGN_FILL;
		}
		return 0;
	}

	private int defaultConvertInt(String str) {
		return XWTMaps.getValue(str);
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return Integer.class;
	}
}
