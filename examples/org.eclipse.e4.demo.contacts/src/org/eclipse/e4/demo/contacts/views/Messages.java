/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation for XWT
 *******************************************************************************/
package org.eclipse.e4.demo.contacts.views;

import org.eclipse.osgi.util.NLS;

public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.e4.demo.contacts.views.messages"; // NON-NLS-1

	public static String General;
	public static String FullName;

	public static String Company;
	public static String JobTitle;
	public static String Note;
	public static String BusinessAddress;
	public static String Street;
	public static String City;
	public static String ZIP;
	public static String State;
	public static String Country;
	public static String BusinessPhones;
	public static String Phone;
	public static String Mobile;
	public static String BusinessInternet;
	public static String Email;
	public static String WebPage;

	public static String FirstName;
	public static String LastName;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
