package org.eclipse.e4.xwt.tests.controls;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

public class ControlsTests extends XWTTestCase {
	public void testControlLocation() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Location.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url);
	}

	public void testControlBackground() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Background.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url);
	}

	public void testControlBounds() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Bounds.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url);
	}

	public void testControlOrientation() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Orientation.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url);
	}

	public void testControlSize() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Size.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkLabel();
				checkButton();
			}

			public void checkLabel() {
				Object element = XWT.findElementByName(root, "targetLabel");
				assertTrue(element instanceof Label);
				Label label = (Label) element;
				Point size = label.getSize();
				assertTrue(size.x == 100 && size.y == 40);
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				Point size = button.getSize();
				assertTrue(size.x == 200 && size.y == 200);
			}
		});
	}

	public void testControlSizeGridData() throws Exception {
		URL url = ControlsTests.class.getResource(Control_Size_GridData.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkLabel();
				checkButton();
			}

			public void checkLabel() {
				Object element = XWT.findElementByName(root, "targetLabel");
				assertTrue(element instanceof Label);
				Label label = (Label) element;
				Object data = label.getLayoutData();
				assertTrue(data instanceof GridData);
				GridData gridData = (GridData) data;
				assertTrue(gridData.widthHint == 100 && gridData.heightHint == 40);
			}

			public void checkButton() {
				Object element = XWT.findElementByName(root, "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				Object data = button.getLayoutData();
				assertTrue(data instanceof GridData);
				GridData gridData = (GridData) data;
				assertTrue(gridData.widthHint == 200 && gridData.heightHint == 200);
			}
		});
	}
}
