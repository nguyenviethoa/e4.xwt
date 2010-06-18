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
package org.eclipse.e4.xwt.tests.forms;

import java.net.URL;
import java.util.Map;

import org.eclipse.e4.xwt.forms.XWTForms;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class FormTestCase extends XWTTestCase {
	protected void runTest(final URL url, Map<String, Object> options,
			Runnable... checkActions) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			root = XWTForms.loadWithOptions(url, options);
			assertNotNull(root);
			Shell shell = root.getShell();
			shell.open();
			Display display = shell.getDisplay();

			for (Runnable runnable : checkActions) {
				while (display.readAndDispatch())
					;
				display.syncExec(runnable);
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
}
