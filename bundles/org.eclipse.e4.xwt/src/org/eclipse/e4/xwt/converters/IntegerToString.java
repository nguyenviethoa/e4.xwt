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

public class IntegerToString extends ObjectToString {
	public static IntegerToString instance = new IntegerToString();

	public Object getFromType() {
		return Integer.class;
	}

	public Object getToType() {
		return String.class;
	}
}
