package org.eclipse.e4.xwt.tests.databinding.pojo;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class PojoDataBindingTests extends XWTTestCase {

	public void testDataBinding() throws Exception {
		URL url = PojoDataBindingTests.class.getResource(DataBinding.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "Button");
				assertTrue(element instanceof Button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "Name");
				assertTrue(element instanceof Text);
				Text text = (Text) element;
				assertEquals(text.getText(), "Soyatec");
			}
		});
	}

	public void testDataBindingPath() throws Exception {
		URL url = PojoDataBindingTests.class.getResource(DataBindingPath.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "Button");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "ManagerCity");
				assertTrue(element instanceof Text);
				Text text = (Text) element;
				assertEquals(text.getText(), "ShenZhen");
			}
		});
	}
}
