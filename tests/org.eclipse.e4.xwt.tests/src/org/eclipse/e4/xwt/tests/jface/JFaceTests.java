package org.eclipse.e4.xwt.tests.jface;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.jface.viewers.ListViewer;

public class JFaceTests extends XWTTestCase {

	public void testListViewer() throws Exception {
		URL url = JFaceTests.class.getResource(ListViewer_Name.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "listView");
				assertTrue(element instanceof ListViewer);
			}
		});
	}

	// TODO add Font and Color
}
