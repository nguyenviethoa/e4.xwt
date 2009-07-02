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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Control;

/**
 * The default class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class XWTStaticPart extends XWTAbstractPart {
	protected URL getURL() {
		return this.getClass().getResource(this.getClass().getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
	}

	public void refresh(Object input, Map<String, Object> options) {
		parent.setVisible(false);
		for (Control child : parent.getChildren()) {
			child.dispose();
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClassLoader());
			HashMap<String, Object> newOptions = new HashMap<String, Object>();
			if (options != null) {
				newOptions.putAll(options);
			}
			newOptions.put(XWTLoader.CONTAINER_PROPERTY, parent);
			newOptions.put(XWTLoader.DATACONTEXT_PROPERTY, input);
			newOptions.put(XWTLoader.CLASS_PROPERTY, this);
			XWT.loadWithOptions(getURL(), newOptions);
			GridLayoutFactory.fillDefaults().generateLayout(parent);
			parent.layout(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			parent.setVisible(true);
		}
	}

	protected ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}
}
