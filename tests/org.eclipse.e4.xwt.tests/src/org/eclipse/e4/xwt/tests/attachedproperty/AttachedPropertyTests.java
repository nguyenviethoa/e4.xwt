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
package org.eclipse.e4.xwt.tests.attachedproperty;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AttachedPropertyTests extends XWTTestCase {

	public void testButton_AttachedProperty() throws Exception {
		URL url = AttachedPropertyTests.class
				.getResource(Button_AttachedProperty.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "button");
				IProperty property = XWT.findProperty(Composite.class,
						"visible");
				Object value = XWT.getPropertyValue(button, property);
				assertEquals(true, value);
			}
		});
	}
}
