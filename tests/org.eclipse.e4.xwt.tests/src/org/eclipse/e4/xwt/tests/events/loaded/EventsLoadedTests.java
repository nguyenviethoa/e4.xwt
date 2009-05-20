package org.eclipse.e4.xwt.tests.events.loaded;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class EventsLoadedTests extends XWTTestCase {

	public void testLoaded() throws Exception {
		URL url = org.eclipse.e4.xwt.tests.events.loaded.Button.class.getResource(org.eclipse.e4.xwt.tests.events.loaded.Button.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "Button");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertEquals(button.getText(), org.eclipse.e4.xwt.tests.events.loaded.ButtonHandler.Message);
			}
		});
	}
}
