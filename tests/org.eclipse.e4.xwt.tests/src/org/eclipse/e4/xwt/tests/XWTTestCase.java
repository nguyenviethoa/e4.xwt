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

import junit.framework.TestCase;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class XWTTestCase extends TestCase {
	protected Control root;

	protected void runTest(final URL url) {
		runTest(url, null, null);
	}

	protected void runTest(final URL url, Runnable prepareAction, Runnable checkAction) {
		try {
			root = XWT.load(url);
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
		}
	}

	protected void runDebugTest(final URL url, Runnable prepareAction, Runnable checkAction1) {
		try {
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
		}
	}

	protected void selectButton(Button button) {
		Point size = button.getSize();
		Display display = button.getDisplay();
		Event upEvent = new Event();
		upEvent.widget = button;
		upEvent.button = 1;
		upEvent.type = SWT.MouseUp;
		upEvent.x = size.x / 2;
		upEvent.y = size.y / 2;
		display.post(upEvent);

		button.setSelection(true);
		button.notifyListeners(SWT.Selection, upEvent);
	}
}
