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

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.css.CSSHandler;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The abstract class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractRootView {
	protected Composite parent;

	static {
		XWT.registerNamspaceHandler(CSSHandler.NAMESPACE, CSSHandler.handler);
	}

	public AbstractRootView(Composite parent) {
		this.parent = parent;
		initialize();
	}

	protected void initialize() {
		parent.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	abstract public Class<?> getInputType();

	abstract protected URL getURL();

	protected void doSetInput(Object input, Map<String, Object> options) {
		Class<?> inputType = getInputType();
		if (inputType == null || inputType.isInstance(input)) {
			refresh(input, options);
		}
	}

	public void refresh(Object input, Map<String, Object> options) {
		parent.setVisible(false);
		for (Control child : parent.getChildren()) {
			child.dispose();
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			HashMap<String, Object> newOptions = new HashMap<String, Object>();
			if (options != null) {
				newOptions.putAll(options);
			}
			newOptions.put(XWTLoader.CONTAINER_PROPERTY, parent);
			newOptions.put(XWTLoader.DATACONTEXT_PROPERTY, input);
			XWT.loadWithOptions(getURL(), newOptions);
			GridLayoutFactory.fillDefaults().generateLayout(parent);
			parent.layout(true, true);
		} catch (Exception e) {
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			parent.setVisible(true);
		}
	}
}
