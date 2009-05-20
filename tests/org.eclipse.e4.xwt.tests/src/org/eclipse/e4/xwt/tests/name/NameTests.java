package org.eclipse.e4.xwt.tests.name;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Label;

public class NameTests extends XWTTestCase {

	public void testName() throws Exception {
		URL url = NameTests.class.getResource(Name.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "Message");
				assertTrue(element instanceof Label);
			}
		});
	}

	public void testNameX() throws Exception {
		URL url = NameTests.class.getResource(Name_x.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				Object element = XWT.findElementByName(root, "Message");
				assertTrue(element instanceof Label);
			}
		});
	}
}
