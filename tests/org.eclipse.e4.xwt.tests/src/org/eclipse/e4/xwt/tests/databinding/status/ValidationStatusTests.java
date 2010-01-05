/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec & hceylan - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.databinding.status;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ValidationStatusTests extends XWTTestCase {

	public void testValidationDefault1() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusName.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Label status = (Label) XWT.findElementByName(root, "statusLabel");
				
				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", status.getText());
				
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", status.getText());
			}
		});
	}

	public void testValidationDefault2() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusName.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Label status = (Label) XWT.findElementByName(root, "statusLabel");

				text1.setText("4");
				text2.setFocus();
				assertEquals("Value must be 5", status.getText());
				
				text1.setFocus();
				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", status.getText());
				
				text2.setText("4");
				text1.setFocus();
				assertEquals("Value must be 6", status.getText());

				text2.setFocus();
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", status.getText());
			}
		});
	}
	
	public void testValidationStaticResource1() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusStaticResource.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Label status = (Label) XWT.findElementByName(root, "statusLabel");
				
				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", status.getText());
				
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", status.getText());
			}
		});
	}

	public void testValidationStaticResource2() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusStaticResource.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Label status = (Label) XWT.findElementByName(root, "statusLabel");

				text1.setText("4");
				text2.setFocus();
				assertEquals("Value must be 5", status.getText());
				
				text1.setFocus();
				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", status.getText());
				
				text2.setText("4");
				text1.setFocus();
				assertEquals("Value must be 6", status.getText());

				text2.setFocus();
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", status.getText());
			}
		});
	}
	
	
	public void testValidationToolTip1() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusToolTip.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				
				assertEquals("Value must be 5", text1.getToolTipText());
				assertEquals("Value must be 5", text2.getToolTipText());

				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", text1.getToolTipText());
				
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", text2.getToolTipText());
				assertEquals("OK", text1.getToolTipText());
			}
		});
	}

	public void testValidationToolTip2() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusToolTip.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");

				text1.setText("4");
				text2.setFocus();
				assertEquals("Value must be 5", text1.getToolTipText());
				assertEquals("Value must be 5", text2.getToolTipText());
				
				text1.setFocus();
				text1.setText("5");
				text2.setFocus();
				assertEquals("Value must be 6", text1.getToolTipText());
				assertEquals("Value must be 6", text2.getToolTipText());
				
				text2.setText("4");
				text1.setFocus();
				assertEquals("Value must be 6", text1.getToolTipText());
				assertEquals("Value must be 6", text2.getToolTipText());

				text2.setFocus();
				text2.setText("6");
				text1.setFocus();
				assertEquals("OK", text1.getToolTipText());
				assertEquals("OK", text2.getToolTipText());
			}
		});
	}
	
	public void testValidationTriggerLocal() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusTriggerLocal.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Color red = root.getDisplay().getSystemColor(SWT.COLOR_RED);
				Color black = root.getDisplay().getSystemColor(SWT.COLOR_BLACK);

				assertEquals(red, text1.getForeground());
				assertEquals(red, text2.getForeground());

				text1.setText("4");
				text2.setFocus();
				assertEquals(red, text1.getForeground());
				
				text1.setFocus();
				text1.setText("5");
				text2.setFocus();
				assertNotSame(red, text1.getForeground());
				
				text2.setText("4");
				text1.setFocus();
				assertEquals(red, text2.getForeground());

				text2.setFocus();
				text2.setText("6");
				text1.setFocus();
				assertNotSame(red, text1.getForeground());
			}
		});
	}

	public void testValidationTriggerShared() throws Exception {
		URL url = ValidationStatusTests.class.getResource(ValidationStatusTriggerShared.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Text text1 = (Text) XWT.findElementByName(root, "text1");
				Text text2 = (Text) XWT.findElementByName(root, "text2");
				Label statusLabel = (Label) XWT.findElementByName(root, "statusLabel");
				
				Color red = root.getDisplay().getSystemColor(SWT.COLOR_RED);
				Color black = root.getDisplay().getSystemColor(SWT.COLOR_BLACK);

				assertEquals(red, statusLabel.getForeground());

				text1.setText("4");
				text2.setFocus();
				assertEquals(red, statusLabel.getForeground());
				assertEquals("Value must be 5", statusLabel.getText());

				text1.setFocus();
				text1.setText("5");
				text2.setFocus();
				assertEquals(red, statusLabel.getForeground());
				assertEquals("Value must be 6", statusLabel.getText());
				
				text2.setText("4");
				text1.setFocus();
				assertEquals(red, statusLabel.getForeground());
				assertEquals("Value must be 6", statusLabel.getText());

				text2.setFocus();
				text2.setText("6");
				text1.setFocus();
				assertNotSame(red, statusLabel.getForeground());
				assertEquals("OK", statusLabel.getText());
			}
		});
	}
}
