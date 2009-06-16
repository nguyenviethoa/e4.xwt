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
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataBindingInfo;
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
		if (source instanceof IObservableValue) {
			IBindingContext bindingContext = new BindingContext();
			sourceWidget = new WritableValue() {
				@Override
				public Object doGetValue() {
					return getDataProvider().getData(super.doGetValue(), getSourceProperty());
				}
			};

			bindingContext.bind((IObservableValue) source, sourceWidget, new IDataBindingInfo() {
				public IDataProvider getDataProvider() {
					return ControlDataBinding.this.getDataProvider();
				}

				public IValueConverter getConverter() {
					return ControlDataBinding.this.getConverter();
				}

				public BindingMode getBindingMode() {
					return BindingMode.TwoWay;
				}
			});
		} else {
			sourceWidget = ObservableValueUtil.createWidget(source, getSourceProperty());
		}

		IObservableValue targetWidget = ObservableValueUtil.createWidget(getTarget(), getTargetProperty());
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
