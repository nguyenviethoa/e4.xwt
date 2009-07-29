/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation for XWT
 *******************************************************************************/
package org.eclipse.e4.demo.e4photo.xwt;

import java.net.URL;

import org.eclipse.e4.demo.e4photo.Exif;
import org.eclipse.e4.xwt.jface.AbstractDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author tguiu (thomas.guiu@soyatec.com)
 */
public class EditDialog extends AbstractDialog {
	public EditDialog(Shell parentShell, String title, Exif exif) {
		super(parentShell, title, exif);
	}

	protected URL getContentURL() {
		return EditDialog.class.getResource("EditDialog.xwt");
	}
}
