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
package org.eclipse.e4.xwt.tests.jface.tableviewer.filter;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

public class JFaceTableViewer_Filters_Tests extends XWTTestCase {

	public void testTableViewer() throws Exception {
		URL url = JFaceTableViewer_Filters_Tests.class
				.getResource(TableViewer_Test.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableItem[] items = tableViewer.getTable().getItems();
				assertTrue(items.length == 1);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
			}
		});
	}
}
