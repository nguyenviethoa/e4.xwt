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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.core.Binding;
import org.eclipse.e4.xwt.internal.core.ScopeManager;
import org.eclipse.e4.xwt.internal.utils.UserData;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ControlDataBinding extends AbstractDataBinding {
	private Object source;

	public ControlDataBinding(Object source, Binding binding,
			IDataProvider dataProvider) {
		super(binding, dataProvider);
		this.source = source;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue sourceWidget = null;
		IObservableValue targetWidget = null;
		Object target = getControl();
		if (target != null) {
			IObservable observable = ScopeManager.observeValue(target, target,
					getTargetProperty(), getUpdateSourceTrigger());
			if (observable instanceof IObservableValue) {
				targetWidget = (IObservableValue) observable;
			}
		}
		if (source == null) {
			return null;
		}

		Object control = UserData.getWidget(source);
		if (control == null) {
			control = getControl();
		}

		IObservable observable = ScopeManager.observeValue(control, source,
				getSourceProperty(), getUpdateSourceTrigger());
		if (observable instanceof IObservableValue) {
			sourceWidget = (IObservableValue) observable;
		}

		if (targetWidget == null) {
			if (sourceWidget != null) {
				Object value = sourceWidget.getValue();
				IValueConverter converter = getConverter();
				if (converter != null) {
					value = converter.convert(value);
				}
				return value;
			}
			return source;
		}

		DataBindingContext dataBindingContext = XWT.getDataBindingContext(
				getControl(), getContextName());
		IBindingContext bindingContext = new BindingContext(dataBindingContext);
		bindingContext.bind(sourceWidget, targetWidget, this);
		if (sourceWidget != null) {
			Object value = sourceWidget.getValue();
			IValueConverter converter = getConverter();
			if (converter != null) {
				value = converter.convert(value);
			}
			return value;
		}
		return source;
	}

	/**
	 * 
	 * @return
	 */
	protected Object getSource() {
		return source;
	}
}
