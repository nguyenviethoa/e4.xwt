/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.threading;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Display;

/**
 * @author yyang(yves.yang@soyatec.com)
 */
public class ThreadingTests extends XWTTestCase {

	/**
	 * The extensibility of Value resolver like <class>.member
	 * 
	 */
	public void testThreading_Open() throws Exception {
		URL url = ThreadingTests.class.getResource(Threading.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);

		Thread thread1 = new Thread() {
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
		};

		Thread thread2 = new Thread() {
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
		};

		thread1.start();
		thread2.start();
		
		for (int i = 0; i < 100; i++) {
			if (Display.findDisplay(thread1) == null && Display.findDisplay(thread2) == null) {
				Thread.sleep(500);
			}
		}
		assertTrue(Display.findDisplay(thread1) != null || Display.findDisplay(thread2) != null);

		for (int i = 0; i < 100; i++) {
			if (Display.findDisplay(thread1) == null || Display.findDisplay(thread2) == null) {
				Thread.sleep(500);
			}
		}
		assertTrue(Display.findDisplay(thread1) != null && Display.findDisplay(thread2) != null);
	}
}
