package org.eclipse.e4.xwt.tests.trigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;

public class TriggerTests extends XWTTestCase {

	public TriggerTests() {
	}

	public void test_Button_Trigger() {
		URL url = TriggerTests.class.getResource(Button_Click_Trigger.class
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
				Button button = (Button) XWT.findElementByName(root, "Button");
				assertFalse(button.isVisible());
			}
		});
	}

	public void test_Button_Trigger_SourceName() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_SourceName.class
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
				assertFalse(root.isVisible());
				Button button = (Button) XWT.findElementByName(root, "Button");
				assertFalse(button.isVisible());
			}
		});
	}

	public void test_Button_Trigger_Setter_TargetName() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_Setter_TargetName.class
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
				Button button = (Button) XWT.findElementByName(root, "target");
				assertFalse(button.isVisible());
			}
		});
	}

	public void test_Button_Trigger_Setter_TargetName2() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_Setter_TargetName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "Button");
				selectButton(button, true);
				selectButton(button, false);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "target");
				assertTrue(button.isVisible());
			}
		});
	}

	public void test_Button_Trigger_SourceName_Setter_TargetName() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_SourceName_Setter_TargetName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "source");
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "target");
				assertFalse(button.isVisible());
			}
		});
	}

	public void test_Button_Click_Trigger_EventProperty() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_EventProperty.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "source");
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "target");
				assertEquals("OK", button.getText());
			}
		});
	}

	public void test_Button_Click_Trigger_EventProperty2() {
		URL url = TriggerTests.class
				.getResource(Button_Click_Trigger_EventProperty.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "source");
				selectButton(button, true);
				selectButton(button, false);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Button button = (Button) XWT.findElementByName(root, "target");
				assertEquals("Target", button.getText());
			}
		});
	}
}
