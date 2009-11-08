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

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ControlDataBinding extends AbstractDataBinding {
	private Object source;

	public ControlDataBinding(Object source, Object target, String sourceProperty, String targetProperty, BindingMode mode, IValueConverter converter, IDataProvider dataProvider) {
		super(sourceProperty, targetProperty, target, mode, converter, dataProvider);
		this.source = source;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue sourceWidget;
		IObservableValue targetWidget = ObservableValueFactory.createWidgetValue(getTarget(), getTargetProperty());
		Class<?> type = Object.class;
		if (targetWidget != null) {
			Object valueType = targetWidget.getValueType();
			if (valueType instanceof Class<?>) {
				type = (Class<?>) valueType;
			}
		}
		
		if (source instanceof IObservableValue) {
//			IBindingContext bindingContext = new BindingContext();
//			sourceWidget = new WritableValue() {
//				@Override
//				public Object doGetValue() {
//					return getDataProvider().getData(getSourceProperty());
//				}
//			};			
//			bindingContext.bind((IObservableValue) source, sourceWidget, new IDataBindingInfo() {
//				public IDataProvider getDataProvider() {
//					return ControlDataBinding.this.getDataProvider();
//				}
//
//				public IValueConverter getConverter() {
//					return ControlDataBinding.this.getConverter();
//				}
//
//				public BindingMode getBindingMode() {
//					return BindingMode.TwoWay;
//				}
//			});
			sourceWidget = BeansObservables.observeDetailValue((IObservableValue)source, getSourceProperty(), type);			
		} else {
			sourceWidget = ObservableValueFactory.createWidgetValue(source, getSourceProperty());
		}

		if (targetWidget == null) {
			if (sourceWidget != null) {
				return sourceWidget.getValue();
			}
			return source;
		}
		IBindingContext bindingContext = new BindingContext();
		bindingContext.bind(sourceWidget, targetWidget, this);
		if (sourceWidget != null) {
			return sourceWidget.getValue();
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

	/**
	 * 
	 * @param source
	 */
	protected void setSource(Object source) {
		this.source = source;
	}
}
