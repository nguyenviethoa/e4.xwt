package org.eclipse.e4.xwt.tests.controls.layout;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;

public class LayoutTests extends XWTTestCase {
	public void testForm_NullLayout() throws Exception {
		URL url = LayoutTests.class
				.getResource(NullLayout_Test.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Button button = (Button) XWT.findElementByName(root, "button");
				assertTrue(button.getVisible());
				Rectangle rectangle = button.getBounds();
				assertEquals(rectangle, new Rectangle(10, 10, 200, 50));
			}
		});
	}
}
