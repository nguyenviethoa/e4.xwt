package org.eclipse.e4.xwt.tests.events.initialize;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class EventsInitTests extends XWTTestCase {

	public void testInitializeComponent() throws Exception {
		URL url = UserControl.class.getResource(UserControl.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "button");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertEquals(button.getText(), UserControl.Message);
			}
		});
	}
}
