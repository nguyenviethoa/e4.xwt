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

import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.LoadingContext;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.css.CSSHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The abstract class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractView {
	protected Composite parent;
	protected Object input;

	static {
		XWT.registerNamspaceHandler(CSSHandler.NAMESPACE, CSSHandler.handler);
	}

	public AbstractView(Composite parent) {
		this.parent = parent;
		initialize();
	}

	protected void initialize() {
		parent.setLayout(new FillLayout());
		parent.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	abstract public Class<?> getInputType();

	abstract protected URL getURL();

	public void setInput(Object input) {
		if (this.input == input) {
			return;
		}
		Class<?> inputType = getInputType();
		if (inputType.isInstance(input)) {
			this.input = input;
			parent.setVisible(false);
			for (Control child : parent.getChildren()) {
				child.dispose();
			}
			ILoadingContext loadingContext = XWT.getLoadingContext();
			try {
				XWT.setLoadingContext(new LoadingContext(this.getClass().getClassLoader()));
				XWT.load(parent, getURL(), input);
				parent.layout(true, true);
			} catch (Exception e) {
			} finally {
				XWT.setLoadingContext(loadingContext);
				parent.setVisible(true);
			}
		}
	}
}
