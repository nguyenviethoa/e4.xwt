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
package org.eclipse.e4.demo.contacts.views;

import java.net.URL;

import org.eclipse.e4.demo.contacts.handlers.FadeAnimation;
import org.eclipse.e4.demo.contacts.model.Contact;
import org.eclipse.e4.xwt.ui.workbench.views.XWTInputPart;

/**
 * 
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class DetailsView extends XWTInputPart {

	public Class<Contact> getInputType() {
		return Contact.class;
	}
	
	protected void refresh(URL url, Object dataContext, ClassLoader loader) {
		FadeAnimation animation = new FadeAnimation(parent); 
		super.refresh(url, dataContext, loader);
		animation.play();
	}
}
