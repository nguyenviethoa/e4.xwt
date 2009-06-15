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
package org.eclipse.e4.xwt.emf.test;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.emf.EMFDataProvider;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class EMFDataProvider_Path2 {
	public static void main(String[] args) {
		XWT.registerMetaclass(EMFDataProvider.class);
		URL url = EMFDataProvider_Path2.class.getResource(EMFDataProvider_Path2.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
