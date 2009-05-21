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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataBinding;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ControlDataBinding implements IDataBinding {
	private Control source;
	private Control target;

	private String sourceProperty;
	private String targetProperty;
	private IValueConverter converter;

	private BindingMode mode = BindingMode.TwoWay;

	public ControlDataBinding(Control source, Control target, String sourceProperty, String targetProperty, BindingMode mode, IValueConverter converter) {
		this.source = source;
		this.target = target;
		this.sourceProperty = sourceProperty;
		this.targetProperty = targetProperty;
		this.mode = mode;
		this.converter = converter;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue sourceWidget = createWidget(source, sourceProperty);
		IObservableValue targetWidget = createWidget(target, targetProperty);
		if (targetWidget == null) {
			if (sourceWidget != null) {
				return sourceWidget.getValue();
			}
			return null;
		}
		IBindingContext bindingContext = new BindingContext();
		bindingContext.bind(sourceWidget, targetWidget, this);
		if (sourceWidget != null) {
			return sourceWidget.getValue();
		}
		return null;
	}

	public IObservableValue createWidget(Object object, String property) {
		if (object instanceof Control) {
			try {
				return ObservableValueUtil.observePropertyValue((Control) object, property);
			} catch (XWTException e) {
			}
		}
		return null;
	}

	public BindingMode getBindingMode() {
		return mode;
	}

	public IValueConverter getConverter() {
		return converter;
	}

	public IDataProvider getDataProvider() {
		return null;
	}

	public Object getTarget() {
		return target;
	}
}
