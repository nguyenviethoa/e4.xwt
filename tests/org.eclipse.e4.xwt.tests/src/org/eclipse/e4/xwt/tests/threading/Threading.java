/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.threading;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

public class Threading {
	public static void main(String[] args) {
		new Thread() {
			@Override
			public void run() {
				URL url = Threading.class.getResource(Threading.class
						.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
				try {
					XWT.open(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				URL url = Threading.class.getResource(Threading.class
						.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
				try {
					XWT.open(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
