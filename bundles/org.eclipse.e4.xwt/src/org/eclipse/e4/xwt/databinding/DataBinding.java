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
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;

/**
 * The default implementation of the dataBinding object.
 * 
 * @author jliu (jin.liu@soyatec.com)
 */
public class DataBinding extends AbstractDataBinding {

	private IObservableValue observableSource;
	private IObservableValue observableWidget;

	/**
	 * Constructor for dataProvider.
	 */
	public DataBinding(Object target, String sourceProperty, String targetProperty, BindingMode mode, IValueConverter converter, IDataProvider dataProvider) {
		super(sourceProperty, targetProperty, target, mode, converter, dataProvider);
		assert dataProvider != null : "DataProvider is null";
		assert sourceProperty != null : "Binding path is null";
		setDataProvider(dataProvider);
	}

	/**
	 * Constructor for dataProvider.
	 */
	public DataBinding(IObservableValue observableSource, Object target, String sourceProperty, String targetProperty, BindingMode mode, IValueConverter converter, IDataProvider dataProvider) {
		super(sourceProperty, targetProperty, target, mode, converter, dataProvider);
		assert dataProvider != null : "DataProvider is null";
		assert sourceProperty != null : "Binding path is null";
		this.observableSource = observableSource;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue observableWidget = getObservableWidget();
		IDataProvider dataProvider = getDataProvider();
		/* If observableWidget is null, we need only return the data from provider. */
		if (observableWidget == null) {
			return dataProvider.getData(getSourceProperty());
		}
		IObservableValue observableSource = getObservableSource();
		IBindingContext bindingContext = dataProvider.getBindingContext();
		if (bindingContext != null && observableSource != null) {
			bindingContext.bind(observableSource, observableWidget, this);
		}
		if (observableSource != null) {
			return observableSource.getValue();
		}
		return null;
	}

	public IObservableValue getObservableSource() {
		if (observableSource == null) {
			IDataProvider dataProvider = getDataProvider();
			String sourceProperty = getSourceProperty();
			Object valueType = dataProvider.getDataType(sourceProperty);
			observableSource = dataProvider.createObservableValue(valueType, sourceProperty);
		}
		return observableSource;
	}

	public IObservableValue getObservableWidget() {
		if (observableWidget == null) {
			observableWidget = ObservableValueUtil.createWidget(getTarget(), getTargetProperty());
		}
		return observableWidget;
	}
}
