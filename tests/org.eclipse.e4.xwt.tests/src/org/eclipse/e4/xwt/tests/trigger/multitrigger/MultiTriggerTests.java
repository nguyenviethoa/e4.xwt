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
package org.eclipse.e4.xwt.tests.trigger.multitrigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class MultiTriggerTests extends XWTTestCase {

	public MultiTriggerTests() {
	}

	public void test_Button_Click_MultiTrigger() {
		URL url = MultiTriggerTests.class
				.getResource(Button_Click_MultiTrigger.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button buttonHide = (Button) XWT.findElementByName(root,
						"ButtonHide");
				selectButton(buttonHide);
				Button buttonNotHide = (Button) XWT.findElementByName(root,
						"ButtonNotHide");
				selectButton(buttonNotHide);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button buttonHide = (Button) XWT.findElementByName(root,
						"ButtonHide");
				assertFalse(buttonHide.isVisible());
				Button buttonNotHide = (Button) XWT.findElementByName(root,
						"ButtonNotHide");
				assertTrue(buttonNotHide.isVisible());
			}
		});
	}

	public void test_Button_Click_MultiTrigger_SourceName1() {
		URL url = MultiTriggerTests.class
				.getResource(Button_Click_MultiTrigger_SourceName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button1 = (Button) XWT
						.findElementByName(root, "button1");
				selectButton(button1);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				assertTrue(root.isVisible());
			}
		});
	}

	public void test_Button_Click_MultiTrigger_SourceName2() {
		URL url = MultiTriggerTests.class
				.getResource(Button_Click_MultiTrigger_SourceName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button2 = (Button) XWT
						.findElementByName(root, "button2");
				selectButton(button2);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				assertTrue(root.isVisible());
			}
		});
	}

	public void test_Button_Click_MultiTrigger_SourceName3() {
		URL url = MultiTriggerTests.class
				.getResource(Button_Click_MultiTrigger_SourceName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button1 = (Button) XWT
						.findElementByName(root, "button1");
				selectButton(button1);
				Button button2 = (Button) XWT
						.findElementByName(root, "button2");
				selectButton(button2);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button1 = (Button) XWT
						.findElementByName(root, "button1");
				assertFalse(button1.isVisible());
				Button button2 = (Button) XWT
						.findElementByName(root, "button2");
				assertFalse(button2.isVisible());
			}
		});
	}
}
