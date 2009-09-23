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

import org.eclipse.e4.core.services.annotations.In;
import org.eclipse.e4.core.services.annotations.Out;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.css.CSSHandler;
import org.eclipse.e4.xwt.ui.workbench.IContentPart;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The default class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class XWTAbstractPart implements IContentPart {
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private String persistedState;

	protected Composite parent;

	public XWTAbstractPart() {
	}

	static {
		try {
			XWT.registerNamspaceHandler(CSSHandler.NAMESPACE, CSSHandler.handler);
		} catch (Exception e) {
		}
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

	public Object getDataContext() {
		return getContext();
	}	

	@In
	public void setParent(Composite parent) {
		if (parent != null && this.parent == null) {
			this.parent = parent;
			parent.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		}
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
	
	public ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}
	
	protected void refresh(URL url, Object dataContext, ClassLoader loader) {
		parent.setVisible(false);
		for (Control child : parent.getChildren()) {
			child.dispose();
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(loader);
			HashMap<String, Object> newOptions = new HashMap<String, Object>();
			newOptions.put(XWTLoader.CONTAINER_PROPERTY, parent);
			newOptions.put(XWTLoader.DATACONTEXT_PROPERTY, dataContext);
			newOptions.put(XWTLoader.CLASS_PROPERTY, this);
			XWT.loadWithOptions(url, newOptions);
			GridLayoutFactory.fillDefaults().generateLayout(parent);
			parent.layout(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			parent.setVisible(true);
		}
	}
}