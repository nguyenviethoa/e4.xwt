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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.annotations.In;
import org.eclipse.e4.core.services.annotations.Out;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.css.CSSHandler;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The default class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class XWTPart {
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private String persistedState;

	protected Composite parent;
	
	static {
		XWT.registerNamspaceHandler(CSSHandler.NAMESPACE, CSSHandler.handler);
	}

	private IEclipseContext context;

	public IEclipseContext getContext() {
		return context;
	}

	@In
	public void setContext(IEclipseContext context) {
		if (context == null) {
			return;
		}
		this.context = context;
	}

	public XWTPart() {
	}

	@In
	public void setParent(Composite parent) {
		if (parent != null && this.parent == null) {
			this.parent = parent;
			parent.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		}
	}

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
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	@In
	void setPersistedState(String persistedState) {
		changeSupport.firePropertyChange("persistedState", this.persistedState, this.persistedState = persistedState);
	}

	@Out
	public String getPersistedState() {
		return persistedState;
	}
}
