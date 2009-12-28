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
package org.eclipse.e4.xwt.internal.core;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.core.IDynamicBinding;
import org.eclipse.e4.xwt.databinding.BindingMode;
import org.eclipse.e4.xwt.databinding.IBindingContext;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.swt.widgets.Widget;

/**
 * Generic Binding definition
 *
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class DynamicBinding implements IDynamicBinding {
	private Object control;

	private Object host;

	private BindingMode mode = BindingMode.TwoWay;

	private IXWTLoader xwtLoader;

	/**
	 * The name of the {@link DataBindingContext} that we will look up in static
	 * resources
	 */
	private IBindingContext bindingContext;

	/**
	 * which used to decide binding type, not only text.
	 */
	private String type;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.e4.xwt.core.IDynamicBinding#getContextName()
	 */
	public IBindingContext getBindingContext() {
		if (this.bindingContext == null) {
			Object element = (control == null ? host : control);
			this.bindingContext = XWT.getBindingContext(element);
		}
		return this.bindingContext;
	}

	public Object getHost() {
		return host;
	}

	public void setHost(Object host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.e4.xwt.core.IDynamicBinding#setBindingContext(IBindingContext)
	 */
	public void setBindingContext(IBindingContext bindingContext) {
		this.bindingContext = bindingContext;
	}

	public void setControl(Object control) {
		this.control = control;
	}

	public Object getControl() {
		return this.control;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public BindingMode getMode() {
		return mode;
	}

	public void setMode(BindingMode mode) {
		this.mode = mode;
	}

	public void setXWTLoader(IXWTLoader xwtLoader) {
		this.xwtLoader = xwtLoader;
	}

	protected Object getDataContextHost() {
		Object control = getControl();
		if (control == null) {
			return null;
		}
		Object data = UserData.getLocalDataContext(control);
		if (data == null || data == this) {
			Widget parent = UserData.getParent(control);
			if (parent != null) {
				return UserData.getDataContextHost(parent);
			}
			return null;
		}
		if (data != null) {
			return control;
		}
		return UserData.getDataContextHost(control);
	}

	protected Object getDataContext() {
		if (control != null) {
			return UserData.getDataContext(control);
		}
		return null;
	}

	protected IDataProvider getDataProvider(Object dataContext) {
		if (dataContext != null) {
			if (dataContext instanceof IDataProvider) {
				return (IDataProvider) dataContext;
			} else {
				return xwtLoader.findDataProvider(dataContext);
			}
		}
		return null;
	}

	public IDataProvider getDataProvider() {
		return getDataProvider(getDataContext());
	}
}
