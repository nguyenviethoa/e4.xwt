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
package org.eclipse.e4.xwt.tests.usercontrol;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class UserControlTests extends XWTTestCase {
	public void testUserControl() throws Exception {
		URL url = UserControlTests.class.getResource(Container.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Control[] childControls = ((Composite) root).getChildren();
				assertTrue(childControls.length == 1);
				assertTrue(childControls[0] instanceof UserControl);
				Object element = XWT.findElementByName(
						(UserControl) childControls[0], "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Control[] childControls = ((Composite) root).getChildren();
				assertTrue(childControls.length == 1);
				assertTrue(childControls[0] instanceof UserControl);
				Object element = XWT.findElementByName(
						(UserControl) childControls[0], "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertTrue(UserControl.SELECTION_MESSAGE.equals(button
						.getText()));
			}
		});
	}

	public void testUserControlName() throws Exception {
		URL url = UserControlTests.class.getResource(Container.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object childCOntrol = XWT.findElementByName(root,
						"childControl");
				assertTrue(childCOntrol instanceof UserControl);
				Object element = XWT.findElementByName(
						(UserControl) childCOntrol, "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				selectButton(button);
			}
		}, new Runnable() {
			public void run() {
				checkButton();
			}

			public void checkButton() {
				Object childCOntrol = XWT.findElementByName(root,
						"childControl");
				assertTrue(childCOntrol instanceof UserControl);
				Object element = XWT.findElementByName(
						(UserControl) childCOntrol, "targetButton");
				assertTrue(element instanceof Button);
				Button button = (Button) element;
				assertTrue(UserControl.SELECTION_MESSAGE.equals(button
						.getText()));
			}
		});
	}
}
