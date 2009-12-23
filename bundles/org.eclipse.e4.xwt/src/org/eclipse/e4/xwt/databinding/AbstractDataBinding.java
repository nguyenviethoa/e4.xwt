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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.e4.xwt.IDataBinding;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.IValueValidator;
import org.eclipse.e4.xwt.internal.core.Binding;
import org.eclipse.e4.xwt.internal.core.BindingExpressionPath;
import org.eclipse.e4.xwt.internal.core.UpdateSourceTrigger;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractDataBinding implements IDataBinding {
	private IDataProvider dataProvider;

	private Binding binding;

	private BindingExpressionPath targetPropertySegments;

	public BindingExpressionPath getSourcePropertyExpression() {
		return binding.getPathPropertySegments();
	}

	public BindingExpressionPath getTargetPropertyExpression() {
		return targetPropertySegments;
	}

	public AbstractDataBinding(Binding binding, IDataProvider dataProvider) {
		this.binding = binding;
		this.dataProvider = dataProvider;

		targetPropertySegments = new BindingExpressionPath(getTargetProperty());
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
		return binding.getMode();
	}

	/**
	 * Returns the name of the data binding context this binding is associated
	 * with
	 * 
	 * @return
	 */
	protected String getContextName() {
		return binding.getContextName();
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
		return binding.getMode();
	}

	/**
	 * 
	 * @param mode
	 */
	public void setMode(BindingMode mode) {
		binding.setMode(mode);
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

	public IValueValidator[] getValidators() {
		return binding.getValidators();
	}

	public UpdateSourceTrigger getUpdateSourceTrigger() {
		return binding.getUpdateSourceTrigger();
	}

	protected IObservable getObservableSource() {
		return binding.getObservableSource();
	}

	protected void setObservableSource(IObservable observableSource) {
		binding.setObservableSource(observableSource);
	}
}
