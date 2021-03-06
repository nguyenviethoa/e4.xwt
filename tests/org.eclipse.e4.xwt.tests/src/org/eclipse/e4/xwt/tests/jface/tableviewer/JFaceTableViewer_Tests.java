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
package org.eclipse.e4.xwt.tests.jface.tableviewer;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class JFaceTableViewer_Tests extends XWTTestCase {

	public void testTableViewer() throws Exception {
		URL url = JFaceTableViewer_Tests.class
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
				assertTrue(items.length == 2);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
			}
		});
	}

	public void testTableViewer_FullSelection() throws Exception {
		URL url = JFaceTableViewer_Tests.class
				.getResource(TableViewer_FullSelection.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				assertEquals(tableViewer.getTable().getStyle()
						& SWT.FULL_SELECTION, SWT.FULL_SELECTION);
			}
		});
	}

	public void testTableViewer_DataBinding() throws Exception {
		URL url = JFaceTableViewer_Tests.class
				.getResource(TableViewer_DataBinding.class.getSimpleName()
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
				assertTrue(items.length == 2);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
			}
		});
	}

	public void testTableViewerColumns() throws Exception {
		URL url = JFaceTableViewer_Tests.class
				.getResource(TableViewerColumns_Test.class.getSimpleName()
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
				assertTrue(items.length == 2);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
			}
		});
	}

	public void testTableViewerColumnProperties() throws Exception {
		URL url = JFaceTableViewer_Tests.class
				.getResource(TableViewer_ColumnProperties.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				Object[] items = tableViewer.getColumnProperties();
				assertTrue(items != null);
				assertTrue(items.length == 2);
				assertEquals(items[0], "Name");
				assertEquals(items[1], "Age");
			}
		});
	}

	public void testTableViewerColumn_text() throws Exception {
		URL url = JFaceTableViewer_Tests.class
				.getResource(TableViewerColumn_text.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				checkListViewer();
			}

			public void checkListViewer() {
				Object element = XWT.findElementByName(root, "TableViewer");
				assertTrue(element instanceof TableViewer);
				TableViewer tableViewer = (TableViewer) element;
				TableColumn[] columns = tableViewer.getTable().getColumns();
				assertTrue(columns != null);
				assertTrue(columns.length == 2);
				assertEquals(columns[0].getText(), "column0");
				assertEquals(columns[1].getText(), "column1");
			}
		});
	}
	
	public void testTableViewerColumnsPath_bindingPath() throws Exception {
		URL url = TableViewerColumn_bindingPath.class
				.getResource(TableViewerColumn_bindingPath.class.getSimpleName()
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
				assertTrue(items.length == 2);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
			}
		});
	}
	
	public void testTableViewerColumnsPath_bindingPath_itemText() throws Exception {
		URL url = TableViewerColumn_bindingPath.class
				.getResource(TableViewerColumn_bindingPath_itemText.class.getSimpleName()
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
				assertTrue(items.length == 3);
				assertEquals(items[0].getText(0), "Thomas");
				assertEquals(items[0].getText(1), "32");
				assertNull(items[0].getImage(2));				
				assertEquals(items[1].getText(0), "Jin");
				assertEquals(items[1].getText(1), "27");
				assertNull(items[1].getImage(2));				
				assertEquals(items[2].getText(0), "Susan");
				assertEquals(items[2].getText(1), "27");
				assertNotNull(items[2].getImage(2));				
			}
		});
	}
}
