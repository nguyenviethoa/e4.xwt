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
package org.eclipse.e4.xwt.tests.xaml;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;

/**
 * @author jliu
 */
public class Name {

	public static void main(String[] args) {

		URL url = Name.class.getResource(Name.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void findElement(Event event) {
		if (XWT.findElementByName(event.widget, "target") != null) {
			MessageDialog.openInformation(XWT.findShell(event.widget),
					"Message", "Element is Found");
		} else {
			MessageDialog.openError(XWT.findShell(event.widget), "Message",
					"No Found");
		}
	}
}
