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

public abstract class XWTPartSwitcher extends XWTStaticPart {
	
	protected void refresh() {
		XWTStaticPart switcher = getCurrentPart();
		switchPart(switcher);
	}

	protected abstract XWTStaticPart getCurrentPart();
	
	public void switchPart(XWTStaticPart part) {
		refresh(part.getURL(), part.getDataContext(), part.getClassLoader());		
	}
}
