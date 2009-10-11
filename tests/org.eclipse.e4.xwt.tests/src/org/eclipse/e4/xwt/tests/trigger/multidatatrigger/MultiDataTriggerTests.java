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
package org.eclipse.e4.xwt.tests.trigger.multidatatrigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.e4.xwt.tests.trigger.multitrigger.MultiTriggerTests;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class MultiDataTriggerTests extends XWTTestCase {

	public MultiDataTriggerTests() {
	}

	public void test_MultiDataTrigger1() {
		URL url = MultiDataTriggerTests.class.getResource(MultiDataTrigger.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button1 = (Button) XWT.findElementByName(root, "Button");
				selectButton(button1);
				Text text = (Text) XWT.findElementByName(root, "Text");
				text.setText("11");
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				assertTrue(root.isVisible());
			}
		});
	}
	
	public void test_MultiDataTrigger2() {
		URL url = MultiDataTriggerTests.class.getResource(MultiDataTrigger.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button1 = (Button) XWT.findElementByName(root, "Button");
				selectButton(button1, false);
				Text text = (Text) XWT.findElementByName(root, "Text");
				text.setText("15");
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				assertTrue(root.isVisible());
			}
		});
	}

	public void test_MultiDataTrigger3() {
		URL url = MultiDataTriggerTests.class.getResource(MultiDataTrigger.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button1 = (Button) XWT.findElementByName(root, "Button");
				selectButton(button1, false);
				Text text = (Text) XWT.findElementByName(root, "Text");
				text.setText("15");
				selectButton(button1, true);
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				assertFalse(root.isVisible());
			}
		});
	}
}
