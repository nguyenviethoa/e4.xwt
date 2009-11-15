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
package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.IDataBinding;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.internal.core.Binding;
import org.eclipse.e4.xwt.internal.core.UpdateSourceTrigger;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractDataBinding implements IDataBinding {
	private IDataProvider dataProvider;

	private BindingMode mode = BindingMode.TwoWay;
	
	private Binding binding;
	
	public AbstractDataBinding(Binding binding, IDataProvider dataProvider) {
		this.binding = binding;
		this.dataProvider = dataProvider;
	}

	/**
	 * @return the dataProvider
	 */
	public IDataProvider getDataProvider() {
		return dataProvider;
	}

	/**
	 * @param dataProvider
	 *            the dataProvider to set
	 */
	public void setDataProvider(IDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * 
	 */
	public BindingMode getBindingMode() {
		return mode;
	}

	/**
	 * 
	 */
	public IValueConverter getConverter() {
		return binding.getConverter();
	}

	/**
	 * @return the target
	 */
	public Object getControl() {
		return binding.getControl();
	}

	/**
	 * @return the target
	 */
	public Object getHost() {
		return binding.getHost();
	}

	/**
	 * 
	 * @return
	 */
	public BindingMode getMode() {
		return mode;
	}

	/**
	 * 
	 * @param mode
	 */
	public void setMode(BindingMode mode) {
		this.mode = mode;
	}

	/**
	 * 
	 * @return
	 */
	protected String getSourceProperty() {
		return binding.getPath();
	}

	/**
	 * 
	 * @return
	 */
	protected String getTargetProperty() {
		return binding.getType();
	}

	public UpdateSourceTrigger getUpdateSourceTrigger() {
		return binding.getUpdateSourceTrigger();
	}
}
