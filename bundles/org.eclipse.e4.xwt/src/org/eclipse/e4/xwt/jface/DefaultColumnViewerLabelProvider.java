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
package org.eclipse.e4.xwt.jface;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * 
 * 
 * @author yyang
 */
public class DefaultColumnViewerLabelProvider extends DefaultViewerLabelProvider {
	protected String displayMemberPath; 
	
	public DefaultColumnViewerLabelProvider(ColumnViewer viewer) {
		super(viewer);
	}

	public String getDisplayMemberPath() {
		return displayMemberPath;
	}

	public void setDisplayMemberPath(String displayMemberPath) {
		this.displayMemberPath = displayMemberPath;
	}
	
	@Override
	protected Object[] getPaths() {
		return ((ColumnViewer)viewer).getColumnProperties();
	}
}
