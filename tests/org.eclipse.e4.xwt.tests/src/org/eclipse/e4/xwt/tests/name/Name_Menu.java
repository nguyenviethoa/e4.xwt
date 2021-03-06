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
package org.eclipse.e4.xwt.tests.name;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author jliu
 */
public class Name_Menu {
	public static void main(String[] args) {

		URL url = Name_Menu.class.getResource(Name_Menu.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleButton(Event event) {
		MenuItem message = (MenuItem) XWT.findElementByName(event.widget,
				"Message");
		if (message == null) {
			MessageDialog.openError(XWT.findShell(event.widget), "Test Name",
					"MenuItem message is not found");
		} else {
			MessageDialog.openInformation(XWT.findShell(event.widget),
					"Test Name", "Name works.");
		}
	}
}
