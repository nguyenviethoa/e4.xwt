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
package org.eclipse.e4.xwt.tests.events.loaded.multipleClass;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class MultipleClassLoadedTests extends XWTTestCase {

	public void testLoaded() throws Exception {
		URL url = org.eclipse.e4.xwt.tests.events.loaded.multipleClass.Button.class
				.getResource(org.eclipse.e4.xwt.tests.events.loaded.multipleClass.Button.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				if (getButtonText("RootButton1").equals("1")) {
					assertEquals("2", getButtonText("RootButton2")); 
				}
				else if (getButtonText("RootButton1").equals("2")) {
					assertEquals("1", getButtonText("RootButton2")); 
				}
				else {
					fail();
				}				
				assertEquals("1", getButtonText("ChildButton")); 
			}

			public String getButtonText(String name) {
				Object element = XWT.findElementByName(root, name);
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				return button.getText();
			}
		});
	}
}
