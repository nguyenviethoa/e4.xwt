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
package demo.details;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Event;

/**
 * @author tguiu
 */

public class ChangeHandler {
	protected void modify(Event event) {
		ListViewer contacts = (ListViewer) XWT.findElementByName(event.widget, "contacts");
		contacts.refresh();
	}
}
