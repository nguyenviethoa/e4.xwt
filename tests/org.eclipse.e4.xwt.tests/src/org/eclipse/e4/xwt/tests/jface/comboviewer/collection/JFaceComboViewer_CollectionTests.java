package org.eclipse.e4.xwt.tests.jface.comboviewer.collection;

import java.net.URL;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class JFaceComboViewer_CollectionTests extends XWTTestCase {

	public void test_ComboViewer() throws Exception {
		URL url = JFaceComboViewer_CollectionTests.class
				.getResource(ComboViewer.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "ComboViewer");
				assertTrue(element instanceof org.eclipse.jface.viewers.ComboViewer);
				org.eclipse.jface.viewers.ComboViewer comboViewer = (org.eclipse.jface.viewers.ComboViewer) element;
				String[] items = comboViewer.getCombo().getItems();
				assertTrue(items.length == 2);
				assertEquals(items[0], "Thomas");
				assertEquals(items[1], "Jin");
			}
		});
	}

	public void test_ComboViewer_Simple() throws Exception {
		URL url = JFaceComboViewer_CollectionTests.class
				.getResource(ComboViewer_Simple.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "ComboViewer");
				assertTrue(element instanceof org.eclipse.jface.viewers.ComboViewer);
				org.eclipse.jface.viewers.ComboViewer comboViewer = (org.eclipse.jface.viewers.ComboViewer) element;
				String[] items = comboViewer.getCombo().getItems();
				assertTrue(items.length == 2);
				assertEquals(items[0], "Thomas");
				assertEquals(items[1], "Jin");
			}
		});
	}

	public void test_ComboViewer_DataBinding() {
		URL url = JFaceComboViewer_CollectionTests.class
				.getResource(ComboViewer.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				org.eclipse.jface.viewers.ComboViewer comboViewer = (org.eclipse.jface.viewers.ComboViewer) XWT
						.findElementByName(root, "ComboViewer");
				assertTrue(comboViewer.getInput() instanceof IObservableCollection);
				IObservableCollection collection = (IObservableCollection) comboViewer
						.getInput();
				assertTrue(collection.size() == 3);
			}
		});
	}

	public void test_ComboViewer_DisplayMemberPath() {
		URL url = JFaceComboViewer_CollectionTests.class
				.getResource(ComboViewer_DisplayMemberPath.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				org.eclipse.jface.viewers.ComboViewer comboViewer = (org.eclipse.jface.viewers.ComboViewer) XWT
						.findElementByName(root, "ComboViewer");
				assertTrue(comboViewer.getInput() instanceof IObservableCollection);
				IObservableCollection collection = (IObservableCollection) comboViewer
						.getInput();
				assertTrue(collection.size() == 3);
			}
		});
	}

	public void test_ComboViewer_LabelProvider_DisplayMemberPath() {
		URL url = JFaceComboViewer_CollectionTests.class
				.getResource(ComboViewer_LabelProvider_DisplayMemberPath.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				org.eclipse.jface.viewers.ComboViewer comboViewer = (org.eclipse.jface.viewers.ComboViewer) XWT
						.findElementByName(root, "ComboViewer");
				assertTrue(comboViewer.getInput() instanceof IObservableCollection);
				IObservableCollection collection = (IObservableCollection) comboViewer
						.getInput();
				assertTrue(collection.size() == 3);
			}
		});
	}
}
