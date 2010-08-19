/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.springframework.tests;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

	protected void runTest(URL url, Runnable... checkActions) {
		runTest(url, new HashMap<String, Object>(), checkActions);
	}

	protected void runTest(URL url, Object dataContext,
			Runnable... checkActions) {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(IXWTLoader.DATACONTEXT_PROPERTY, dataContext);
		runTest(url, options, checkActions);
	}

	protected void runTest(final URL url, Map<String, Object> options,
			Runnable... checkActions) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			root = XWT.loadWithOptions(url, options);
			assertNotNull(root);
			Shell shell = root.getShell();
			shell.open();
			/**
			 * The shells of the tests failed are not cleanup properly.
			 * This is a minimalistic solution to clean up the desktop...
			 */
			Display display = shell.getDisplay();
			try{
				for (Runnable runnable : checkActions) {
					while (display.readAndDispatch())
						;
					display.syncExec(runnable);
					while (display.readAndDispatch())
						;
					while (display.readAndDispatch())
						;
				}
				assertFalse(root.isDisposed());
			} finally {
				try{
				shell.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (display.readAndDispatch())
				;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	protected void runDebugTest(final URL url, Runnable prepareAction,
			Runnable checkAction1) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
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

	protected void checkVisibility(String name, Class<? extends Control> type){
		Object element = XWT.findElementByName(root, name);
		if (element == null) {
			fail(name + " is not found.");
		}
		assertTrue(type.isInstance(element));
		Control section = (Control) element;
		assertTrue(section.getVisible());
	}

	protected void checkChildren(String name, String path, int number){
		Object element = XWT.findElementByName(root, name);
		if (path != null) {
			try {
				Method method = element.getClass().getMethod("get" + path);
				if (method == null) {
					fail("Property " + path + " is not found in " + element.getClass().getName());
				}
				element = method.invoke(element);
				assertTrue(Composite.class.isInstance(element));
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		assertTrue(Composite.class.isInstance(element));
		Composite composite = (Composite) element;
		assertEquals(composite.getChildren().length, number);
	}

	protected void checkChildren(String name, int number){
		checkChildren(name, null, number);
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
		for (int i = 0; i < source.length; i++) {
			assertEquals(source[i], target[i]);
		}
	}
}
