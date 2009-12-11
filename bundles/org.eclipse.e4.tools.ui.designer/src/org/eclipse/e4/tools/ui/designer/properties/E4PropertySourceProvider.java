/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.properties;

import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4PropertySourceProvider implements IPropertySourceProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
	 */
	public IPropertySource getPropertySource(Object object) {
		if (object instanceof VisualEditPart) {
			return new E4PropertySource((VisualEditPart) object);
		}
		return null;
	}

}
