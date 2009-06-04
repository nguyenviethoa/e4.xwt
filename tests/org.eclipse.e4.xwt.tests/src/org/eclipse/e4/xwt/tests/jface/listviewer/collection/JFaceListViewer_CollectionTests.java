package org.eclipse.e4.xwt.tests.jface.listviewer.collection;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;

public class JFaceListViewer_CollectionTests extends XWTTestCase {

	public void testListViewer() throws Exception {
		URL url = JFaceListViewer_CollectionTests.class.getResource(ListViewer.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, null, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "ListViewer");
				assertTrue(element instanceof org.eclipse.jface.viewers.ListViewer);
				org.eclipse.jface.viewers.ListViewer listViewer = (org.eclipse.jface.viewers.ListViewer) element;
				String[] items = listViewer.getList().getItems();
				assertTrue(items.length == 2);
				assertEquals(items[0], "Thomas");
				assertEquals(items[1], "Jin");
			}
		});
	}

	// TODO add Font and Color
}