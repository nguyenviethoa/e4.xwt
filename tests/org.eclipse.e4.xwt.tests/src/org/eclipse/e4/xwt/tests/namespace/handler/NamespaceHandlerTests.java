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
package org.eclipse.e4.xwt.tests.namespace.handler;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Label;

public class NamespaceHandlerTests extends XWTTestCase {

	public void testLabelExt() throws Exception {
		XWT.registerNamspaceHandler("http://www.eclipse.org/ext",
				new ExtHandler());
		URL url = NamespaceHandlerTests.class.getResource(LabelExt.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkLabel();
			}

			public void checkLabel() {
				Object element = XWT.findElementByName(root, "targetLabel");
				assertTrue(element instanceof Label);
				Label label = (Label) element;
				assertEquals(label.getData("id"), "Ext-Id");
			}
		});
	}
}
