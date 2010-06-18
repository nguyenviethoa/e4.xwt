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
package org.eclipse.e4.xwt.tests.databinding.self;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Text;

public class DataBindingSelfTests extends XWTTestCase {

	public void testDataBinding() throws Exception {
		URL url = DataBindingSelfTests.class.getResource(DecoratedText.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "text1");
				assertTrue(element instanceof Text);
				Text text = (Text) element;
				text.setText("Soyatec");
			}
		}, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "text2");
				assertTrue(element instanceof Text);
				Text text = (Text) element;
				assertEquals(text.getText(), "Soyatec");
			}
		});
	}
}
