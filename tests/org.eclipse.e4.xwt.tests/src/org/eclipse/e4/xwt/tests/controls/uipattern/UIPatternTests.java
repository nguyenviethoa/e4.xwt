/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.controls.uipattern;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IUIPattern;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

public class UIPatternTests extends XWTTestCase {
	public void testControlSize() throws Exception {
		URL url = UIPatternTests.class.getResource(Control_Size.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		IUIPattern pattern = XWT.loadAsPattern(url);
		runTest(pattern, new Runnable() {
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
		
		runTest(pattern, new Runnable() {
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
}
