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
package org.eclipse.e4.xwt.tests.jface.listviewer.collection;

import java.net.URL;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Event;

public class ListViewer_CollectionViewSource {
	public static void main(String[] args) {

		URL url = ListViewer_CollectionViewSource.class
				.getResource(ListViewer_CollectionViewSource.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addPerson(Object sender, Event event) {
		org.eclipse.jface.viewers.Viewer viewer = (org.eclipse.jface.viewers.Viewer) XWT
				.findElementByName(event.widget, "ListViewer1");
		IObservableCollection collection = (IObservableCollection) viewer
				.getInput();
		Employee employee = new Employee();
		employee.setName("New hired one");
		collection.add(employee);
	}
}
