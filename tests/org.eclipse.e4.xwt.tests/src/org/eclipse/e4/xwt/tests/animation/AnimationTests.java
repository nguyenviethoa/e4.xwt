/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.animation;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AnimationTests extends XWTTestCase {
	protected Color initialColor;
	protected int initialInt;

	public void test_Color_Background() throws Exception {
		URL url = Color_Background_Composite.class
				.getResource(Color_Background_Composite.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "startButton");
					assertTrue(element instanceof Button);
					Button button = (Button)element;
					selectButton(button);
					
					element = XWT.findElementByName(root, "labelTarget");
					assertTrue(element instanceof Label);
					Label label = (Label)element;
					initialColor = label.getBackground();
				} catch (Exception e) {
					fail();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "labelTarget");
					assertTrue(element instanceof Label);
					Label label = (Label)element;
					Color color = label.getBackground();
					assertFalse(initialColor.equals(color));
				} catch (Exception e) {
					fail();
				}
			}
		}		
		);
	}
	
	public void test_Color_Foreground() throws Exception {
		URL url = Color_Foreground_Composite.class
				.getResource(Color_Foreground_Composite.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "startButton");
					assertTrue(element instanceof Button);
					Button button = (Button)element;
					selectButton(button);
					
					element = XWT.findElementByName(root, "labelTarget");
					assertTrue(element instanceof Label);
					Label label = (Label)element;
					initialColor = label.getForeground();
				} catch (Exception e) {
					fail();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "labelTarget");
					assertTrue(element instanceof Label);
					Label label = (Label)element;
					Color color = label.getForeground();
					assertFalse(initialColor.equals(color));
				} catch (Exception e) {
					fail();
				}
			}
		}		
		);
	}
	
	public void test_Int_Alpha() throws Exception {
		URL url = Int_Alpha_Shell.class
				.getResource(Int_Alpha_Shell.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "startButton");
					assertTrue(element instanceof Button);
					Button button = (Button)element;
					selectButton(button);
					
					element = XWT.findElementByName(root, "window");
					assertTrue(element instanceof Shell);
					Shell shell = (Shell)element;
					initialInt = shell.getAlpha();
				} catch (Exception e) {
					fail();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},
		new Runnable() {
			public void run() {
				try {
					Object element = XWT.findElementByName(root, "window");
					assertTrue(element instanceof Shell);
					Shell shell = (Shell)element;
					int alpha = shell.getAlpha();
					assertTrue(alpha != initialInt);
				} catch (Exception e) {
					fail();
				}
			}
		}		
		);
	}
}
