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

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractDataBinding implements IDataBinding {
	private Object target;

	private IValueConverter converter;
	private IDataProvider dataProvider;

	private String sourceProperty;
	private String targetProperty;

	private BindingMode mode = BindingMode.TwoWay;

	public AbstractDataBinding(String sourceProperty, String targetProperty, Object target, BindingMode mode, IValueConverter converter, IDataProvider dataProvider) {
		assert target != null : "Binding widget is null";
		this.mode = mode;
		this.sourceProperty = sourceProperty;
		this.targetProperty = targetProperty;
		this.converter = converter;
		this.target = target;
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
		return converter;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
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
	 * @param converter
	 */
	public void setConverter(IValueConverter converter) {
		this.converter = converter;
	}

	/**
	 * 
	 * @return
	 */
	protected String getSourceProperty() {
		return sourceProperty;
	}

	/**
	 * 
	 * @param sourceProperty
	 */
	protected void setSourceProperty(String sourceProperty) {
		this.sourceProperty = sourceProperty;
	}

	/**
	 * 
	 * @return
	 */
	protected String getTargetProperty() {
		return targetProperty;
	}

	/**
	 * 
	 * @param targetProperty
	 */
	protected void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}
}
