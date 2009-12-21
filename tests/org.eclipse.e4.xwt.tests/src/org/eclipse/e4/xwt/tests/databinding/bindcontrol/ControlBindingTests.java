package org.eclipse.e4.xwt.tests.databinding.bindcontrol;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;

public class ControlBindingTests extends XWTTestCase {

	public void testMenuItemEnabled() throws Exception {
		URL url = ControlBindingTests.class.getResource(BindMenuItem.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "EnabledButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "EnabledMenuItem");
				assertTrue(element instanceof MenuItem);
				MenuItem menuItem = (MenuItem) element;
				assertTrue(menuItem.getEnabled());
			}
		});
	}

	public void testMenuItemSelection() throws Exception {
		URL url = ControlBindingTests.class.getResource(BindMenuItem.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "SelectionButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root,
						"SelectionMenuItem");
				assertTrue(element instanceof MenuItem);
				MenuItem menuItem = (MenuItem) element;
				assertTrue(menuItem.getSelection());
			}
		});
	}

	public void testMenuItemUnselection() throws Exception {
		URL url = ControlBindingTests.class.getResource(BindMenuItem.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "SelectionButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
				selectButton(button, false);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root,
						"SelectionMenuItem");
				assertTrue(element instanceof MenuItem);
				MenuItem menuItem = (MenuItem) element;
				assertFalse(menuItem.getSelection());
			}
		});
	}

	public void testTwoRadios() throws Exception {
		URL url = ControlBindingTests.class.getResource(TwoRadios.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button radio1 = (Button) XWT.findElementByName(root, "button1");
				Button radio2 = (Button) XWT.findElementByName(root, "button2");
				checkInitial(radio1, radio2);
			}
			private void checkInitial(Button radio1, Button radio2) {
				if (!radio2.getEnabled())
					fail("radio2 should have been enabled");
			}
		});
	}
	
	public void testTwoRadios_Select1() throws Exception {
		URL url = ControlBindingTests.class.getResource(TwoRadios.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button radio1 = (Button) XWT.findElementByName(root, "button1");
				selectButton(radio1, true);
			}
		}, 
		new Runnable() {
			public void run() {
				Button radio1 = (Button) XWT.findElementByName(root, "button1");
				Button radio2 = (Button) XWT.findElementByName(root, "button2");
				checkSelected(radio1, radio2);
			}

			private void checkSelected(Button radio1, Button radio2) {
				if (radio2.getEnabled())
					fail("radio2 should have been disabled");
			}
		});
	}

	public void testTwoRadios_UnSelect1() throws Exception {
		URL url = ControlBindingTests.class.getResource(TwoRadios.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button radio1 = (Button) XWT.findElementByName(root, "button1");
				selectButton(radio1, true);
			}
		}, 
		new Runnable() {
			public void run() {
				Button radio1 = (Button) XWT.findElementByName(root, "button1");
				selectButton(radio1, false);
			}
		}, 
		new Runnable() {
			public void run() {
				Button radio2 = (Button) XWT.findElementByName(root, "button2");
				if (!radio2.getEnabled())
					fail("radio2 should have been disabled");
			}
		});
	}
}
