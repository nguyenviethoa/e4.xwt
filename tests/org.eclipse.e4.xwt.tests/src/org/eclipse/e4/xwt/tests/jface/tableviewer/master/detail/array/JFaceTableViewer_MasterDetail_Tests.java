package org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.array;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

public class JFaceTableViewer_MasterDetail_Tests extends XWTTestCase {

	public void testTableViewer_MasterDetail() throws Exception {
		URL url = JFaceTableViewer_MasterDetail_Tests.class.getResource(TableViewer_MasterDetail.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 2);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
			}
		});
	}
}
