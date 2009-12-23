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

import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.jface.viewers.AbstractListViewer;

/**
 * 
 * 
 * @author yyang
 */
public class DefaultComboViewerLabelProvider extends DefaultViewerLabelProvider {
	protected String displayMemberPath; 
	
	public DefaultComboViewerLabelProvider(AbstractListViewer viewer) {
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
		String path = displayMemberPath;
		if (path == null) {
			path = (String) UserData.getLocalData(viewer, PropertiesConstants.PROPERTY_DISPLAY_MEMBER_PATH);
		}
		if (path == null) {
			return Core.EMPTY_ARRAY;
		}
		return new String[] {path};
	}
}
