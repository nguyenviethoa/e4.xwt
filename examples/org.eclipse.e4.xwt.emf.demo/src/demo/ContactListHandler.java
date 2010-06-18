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
package demo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import demo.details.ChangeHandler;

public class ContactListHandler {
	private Map<Object, Control> controls = new HashMap<Object, Control>();
	private ListViewer contacts;
	private Composite parent;
	private Button writable;

	private void init(Widget widget) {
		if (contacts == null)
			contacts = (ListViewer) XWT.findElementByName(widget, "contacts");
		if (parent == null)
			parent = (Composite) XWT.findElementByName(widget, "details");
		if (writable == null)
			writable = (Button) XWT.findElementByName(widget, "writable");
	}

	protected void updateWritable(Event event) {
		init(event.widget);
		perform();
	}

	protected void updateDetails(Event event) {
		init(event.widget);
		perform();
	}

	private void perform() {
		if (contacts.getSelection().isEmpty())
			return;
		Object selection = ((IStructuredSelection) contacts.getSelection()).getFirstElement();
		Control control = controls.get(computeKey(selection));
		if (control == null) {

			// Load
			try {
				URL url = null;
				if (writable.getSelection())
					url = ChangeHandler.class.getResource("details_writable.xwt");
				else
					url = ChangeHandler.class.getResource("details.xwt");

				control = XWT.load(parent, url, selection);
				parent.layout();
				controls.put(computeKey(selection), control);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		StackLayout layout = (StackLayout) parent.getLayout();
		layout.topControl = control;
		parent.layout();
	}

	private Object computeKey(Object selection) {
		return selection.toString() + (writable.getSelection() ? "TRUE" : "FALSE");
	}
}
