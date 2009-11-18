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
package org.eclipse.e4.xwt.tests.events.initialize;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class UserControl extends Composite {
	public static final String Message = "OK -> Initialized";

	public UserControl(Composite parent, int style) {
		super(parent, style);
	}

	protected void initializeComponent() {
		Button button = (Button) XWT.findElementByName(this, "button");
		button.setText(Message);
	}

	public static void main(String[] args) {

		URL url = UserControl.class.getResource(UserControl.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
