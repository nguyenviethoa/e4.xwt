package org.eclipse.e4.xwt.tests.databinding;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class DataBindingTests extends XWTTestCase {

	public void testDataBindingPath() throws Exception {
		URL url = DataBindingTests.class.getResource(DataBindingPath.class
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

	public void testDataBindingPath_UpdateSourceTrigger() throws Exception {
		URL url = DataBindingTests.class
				.getResource(DataBinding_UpdateSourceTrigger.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object input = XWT.findElementByName(root, "inputText");
				Object target = XWT.findElementByName(root, "targetText");
				assertTrue(input instanceof Text);
				Text inputText = (Text) input;
				assertTrue(target instanceof Text);
				Text targetText = (Text) target;
				inputText.setText("new value");
				targetText.setFocus();
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object target = XWT.findElementByName(root, "targetText");
				assertTrue(target instanceof Text);
				Text targetText = (Text) target;
				assertEquals(targetText.getText(), "new value");
			}
		});
	}

	public void testDataBindingPath_UpdateSourceTrigger_2() throws Exception {
		URL url = DataBindingTests.class
				.getResource(DataBinding_UpdateSourceTrigger.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object input = XWT.findElementByName(root, "inputText");
				Object target = XWT.findElementByName(root, "targetText");
				assertTrue(input instanceof Text);
				Text inputText = (Text) input;
				assertTrue(target instanceof Text);
				Text targetText = (Text) target;
				inputText.setText("new value");
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object target = XWT.findElementByName(root, "targetText");
				assertTrue(target instanceof Text);
				Text targetText = (Text) target;
				assertEquals(targetText.getText(), "toto");
			}
		});
	}
}
