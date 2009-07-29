/*******************************************************************************
 * Copyright (c) 2009 Siemens AG and others.
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 *     Kai Tödter - initial implementation
 *     Yves YANG - porting on XWT
 ******************************************************************************/

package org.eclipse.e4.demo.contacts.views;

import org.eclipse.e4.demo.contacts.model.ContactsRepositoryFactory;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;

public class ListView extends XWTStaticPart {
	@Override
	public Object getDataContext() {
		return ContactsRepositoryFactory.getContactsRepository().getAllContacts();
	}
	
	public void dispose() {
	}
	
	protected void selection(Event event) {
		TableViewer contactsViewer = (TableViewer) XWT.findElementByName(event.widget, "TableViewer");
		StructuredSelection selection = (StructuredSelection) contactsViewer.getSelection();
		getContext().set(IServiceConstants.SELECTION, selection.size() == 1 ? selection.getFirstElement() : selection.toArray());
	}
}
