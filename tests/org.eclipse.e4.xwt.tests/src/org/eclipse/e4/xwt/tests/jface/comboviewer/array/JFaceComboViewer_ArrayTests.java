package org.eclipse.e4.xwt.tests.jface.comboviewer.array;

import java.net.URL;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class JFaceComboViewer_ArrayTests extends XWTTestCase {

	public void testListViewer() throws Exception {
		URL url = JFaceComboViewer_ArrayTests.class.getResource(ComboViewer.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "ComboViewer");
				assertTrue(element instanceof org.eclipse.jface.viewers.ListViewer);
				org.eclipse.jface.viewers.ListViewer listViewer = (org.eclipse.jface.viewers.ListViewer) element;
				String[] items = listViewer.getList().getItems();
				assertTrue(items.length == 2);
				assertEquals(items[0], "Thomas");
				assertEquals(items[1], "Jin");
			}
		});
	}
	
	public void test_ListViewer_DataBinding() {
		URL url = JFaceComboViewer_ArrayTests.class.getResource(ComboViewer.class
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
				org.eclipse.jface.viewers.ListViewer listView = (org.eclipse.jface.viewers.ListViewer) XWT.findElementByName(root, "ListViewer");
				assertTrue(listView.getInput() instanceof IObservableCollection);
				IObservableCollection collection = (IObservableCollection) listView.getInput();
				assertTrue(collection.size() == 3);
			}
		});
	}
}
