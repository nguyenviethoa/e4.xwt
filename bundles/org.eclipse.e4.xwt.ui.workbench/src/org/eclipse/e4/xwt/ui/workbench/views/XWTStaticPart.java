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
package org.eclipse.e4.xwt.ui.workbench.views;

import java.net.URL;

import org.eclipse.e4.core.services.annotations.PostConstruct;
import org.eclipse.e4.xwt.IConstants;

/**
 * The default class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class XWTStaticPart extends XWTAbstractPart {
	@PostConstruct
	protected void refresh() {
		refresh(getURL(), getDataContext(), getClassLoader());
	}
	
	protected URL getURL() {
		return this.getClass().getResource(this.getClass().getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
	}
}
