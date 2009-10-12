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
package org.eclipse.e4.xwt.tests;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class XWTTestCase extends TestCase {
	protected Control root;

	protected void runTest(URL url) {
		runTest(url, null, null);
	}

	protected void runTest(URL url, Runnable prepareAction, Runnable checkAction) {
		runTest(url, Collections.EMPTY_MAP, prepareAction, checkAction);
	}

	protected void runTest(URL url, Object dataContext, Runnable prepareAction, Runnable checkAction) {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(IXWTLoader.DATACONTEXT_PROPERTY, dataContext);
		runTest(url, options, prepareAction, checkAction);
	}

	protected void runTest(final URL url, Map<String, Object> options, Runnable prepareAction, Runnable checkAction) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			root = XWT.loadWithOptions(url, options);
			assertNotNull(root);
			Shell shell = root.getShell();
			shell.open();
			Display display = shell.getDisplay();
			if (prepareAction != null) {
				display.asyncExec(prepareAction);
			}
			while (display.readAndDispatch())
				;
			if (checkAction != null) {
				display.syncExec(checkAction);
				while (display.readAndDispatch())
					;
			}
			assertFalse(root.isDisposed());
			shell.close();
			while (display.readAndDispatch())
				;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	protected void runDebugTest(final URL url, Runnable prepareAction, Runnable checkAction1) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			root = XWT.load(url);
			assertNotNull(root);
			Shell shell = root.getShell();
			shell.open();
			Display display = shell.getDisplay();
			if (prepareAction != null) {
				display.asyncExec(prepareAction);
			}
			while (!display.isDisposed())
				display.readAndDispatch();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	protected void selectButton(Button button) {
		selectButton(button, true);
	}

	protected void selectButton(Button button, boolean selection) {
		Point size = button.getSize();
		Display display = button.getDisplay();
		Event upEvent = new Event();
		upEvent.widget = button;
		upEvent.button = 1;
		upEvent.type = SWT.MouseUp;
		upEvent.x = size.x / 2;
		upEvent.y = size.y / 2;
		display.post(upEvent);

		button.setSelection(selection);
		button.notifyListeners(SWT.Selection, upEvent);
	}

	protected void assertText(String name, String value) {
		Object element = XWT.findElementByName(root, name);
		assertTrue(element instanceof Text);
		Text text = (Text) element;
		assertEquals(value, text.getText());
	}

	protected void setText(String name, String value) {
		Object element = XWT.findElementByName(root, name);
		assertTrue(element instanceof Text);
		Text text = (Text) element;
		text.setText(value);
	}
	
	
	protected void assertEqualsArray(Object[] source, Object[] target) {
		assertEquals(source.length, target.length);
		for(int i = 0; i<source.length; i++) {
			assertEquals(source[i], target[i]);			
		}
	}
}
