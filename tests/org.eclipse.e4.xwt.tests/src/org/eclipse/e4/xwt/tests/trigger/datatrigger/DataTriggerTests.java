package org.eclipse.e4.xwt.tests.trigger.datatrigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.e4.xwt.tests.trigger.multitrigger.Button_Click_MultiTrigger;
import org.eclipse.e4.xwt.tests.trigger.multitrigger.MultiTriggerTests;
import org.eclipse.swt.widgets.Button;

public class DataTriggerTests extends XWTTestCase {

	public DataTriggerTests() {
	}

	public void test_DataTrigger1() {
		URL url = DataTriggerTests.class.getResource(DataTrigger.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button, false);
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				assertEquals("Alone", button.getText());
			}
		});
	}
	
	public void test_DataTrigger2() {
		URL url = DataTriggerTests.class.getResource(DataTrigger.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button, false);
				selectButton(button, true);
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				assertEquals("", button.getText());
			}
		});
	}
}
