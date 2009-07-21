package org.eclipse.e4.xwt.tests.events.loaded.multipleClass;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class MultipleClassLoadedTests extends XWTTestCase {

	public void testLoaded() throws Exception {
		URL url = org.eclipse.e4.xwt.tests.events.loaded.multipleClass.Button.class.getResource(org.eclipse.e4.xwt.tests.events.loaded.multipleClass.Button.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkButton("RootButton1", "1");
				checkButton("RootButton2", "2");
				checkButton("ChildButton", "1");
			}

			public void checkButton(String name, String text) {
				Object element = XWT.findElementByName(root, name);
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertEquals(button.getText(), text);
			}
		});
	}
}
