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
package org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.set;

import java.net.URL;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;

/**
 * @author jliu
 */
public class TableViewer_MasterDetail_NestedTable_AddPath {
	public static void main(String[] args) {

		URL url = TableViewer_MasterDetail_NestedTable_AddPath.class
				.getResource(TableViewer_MasterDetail_NestedTable_AddPath.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addEmployee(Object sender, Event event) {
		TableViewer tableViewer = (TableViewer) XWT.findElementByName(
				event.widget, "IndustryViewer");
		Industry industry = (Industry) XWT.getDataContext(tableViewer);
		Company company = null;
		for (Company memeber : industry.getMembers()) {
			if ("Soyatec".equals(memeber.getName())) {
				company = memeber;
				break;
			}
		}
		IObservableValue observableValue = XWT.findObservableValue(
				event.widget, tableViewer, "singleSelection");
		observableValue.setValue(company);

		IObservableSet members = XWT.findObservableSet(event.widget,
				tableViewer, "singleSelection.employees");
		observableValue.setValue(company);
		Employee employee = new Employee();
		employee.setName("Bruno");
		employee.setAge(24);
		members.add(employee);
	}
}
