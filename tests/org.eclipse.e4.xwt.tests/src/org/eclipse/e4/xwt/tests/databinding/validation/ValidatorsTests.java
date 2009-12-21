/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec & hceylan - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.databinding.validation;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Text;

public class ValidatorsTests extends XWTTestCase {

	public void testValidation() throws Exception {
		URL url = ValidatorsTests.class.getResource(Validation.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");

				checkValidationFailure1(text1, text2);
				checkValidationFailure2(text1, text2);
				checkValidationOk(text1, text2);
			}

			private void checkValidationOk(Text text1, Text text2) {
				text2.setText("test3");
				text1.setText("LongEnough");
				assertEquals(text2.getText(), "LongEnough");
			}

			private void checkValidationFailure2(Text text1, Text text2) {
				text2.setText("test2");
				text1.setText("short"); //no string
				assertEquals(text2.getText(), "test2");
			}

			private void checkValidationFailure1(Text text1, Text text2) {
				text2.setText("test1");
				text1.setText(""); //no string
				assertEquals(text2.getText(), "test1");
			}
		});
	}

}
