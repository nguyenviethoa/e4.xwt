package org.eclipse.e4.xwt.tests.namespace.handler;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Label;

public class NamespaceHandlerTests extends XWTTestCase {

	public void testLabelExt() throws Exception {
		XWT.registerNamspaceHandler("http://www.eclipse.org/ext",
				new ExtHandler());
		URL url = NamespaceHandlerTests.class.getResource(LabelExt.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkLabel();
			}

			public void checkLabel() {
				Object element = XWT.findElementByName(root, "targetLabel");
				assertTrue(element instanceof Label);
				Label label = (Label) element;
				assertEquals(label.getData("id"), "Ext-Id");
			}
		});
	}
}
