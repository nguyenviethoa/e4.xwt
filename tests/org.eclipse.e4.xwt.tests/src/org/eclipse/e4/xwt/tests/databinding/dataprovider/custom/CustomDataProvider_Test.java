/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.databinding.dataprovider.custom;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

/**
 * Introduce the Data provider with data context together in XML
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class CustomDataProvider_Test {
	public static void main(String[] args) {

		URL url = CustomDataProvider_Test.class
				.getResource(CustomDataProvider_Test.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
