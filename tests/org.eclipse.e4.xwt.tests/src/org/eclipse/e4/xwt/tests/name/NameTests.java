/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.name;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;

public class NameTests extends XWTTestCase {

	public void testName() throws Exception {
		URL url = NameTests.class.getResource(Name.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "Message");
				assertTrue(element instanceof Label);
			}
		});
	}

	public void testNameX() throws Exception {
		URL url = NameTests.class.getResource(Name_x.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "Message");
				assertTrue(element instanceof Label);
			}
		});
	}

	public void testElementName() throws Exception {
		URL url = NameTests.class.getResource(ElementName_get.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.getElementName(root);
				assertEquals("LabelElement", element);
			}
		});
	}

	public void testElementNameX() throws Exception {
		URL url = NameTests.class.getResource(ElementName_x_get.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.getElementName(root);
				assertEquals("LabelElement", element);
			}
		});
	}

	public void testNameMenu() throws Exception {
		URL url = NameTests.class.getResource(Name_Menu.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "Message");
				assertTrue(element instanceof MenuItem);
			}
		});
	}
}
