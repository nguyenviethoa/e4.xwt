package org.eclipse.e4.xwt.tests.style;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class StyleTests extends XWTTestCase {

	public void test_Style_Trigger_setter() {
		URL url = StyleTests.class.getResource(Style_Trigger_setter.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button);
			}
		},
		new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				assertFalse(button.isVisible());
			}
		});
	}

}
