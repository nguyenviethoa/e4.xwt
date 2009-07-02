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

import org.eclipse.e4.core.services.annotations.In;
import org.eclipse.e4.core.services.annotations.Out;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.css.CSSHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * The default class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class XWTAbstractPart {
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

	public XWTAbstractPart() {
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
}
