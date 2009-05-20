package org.eclipse.e4.xwt.tests.resources;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

public class ResourcesTests extends XWTTestCase {

	public void testImage() throws Exception {
		URL url = ResourcesTests.class.getResource(Image_Test.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkLabel();
				checkButton();
			}

			public void checkLabel() {
				Object element = XWT.findElementByName(root, "targetLabel");
				assertTrue(element instanceof Label);
				Label label = (Label) element;
				assertNotNull(label.getImage());
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertNotNull(button.getImage());
			}
		});
	}

	// TODO add Font and Color
}
