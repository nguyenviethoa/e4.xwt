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
import org.eclipse.e4.xwt.dataproviders.IDataProvider;
import org.eclipse.swt.widgets.Control;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class DataBinding implements IDataBinding {

	private IDataProvider dataProvider;
	private Object target;
	private String path;
	private String type;
	private String mode;
	private IObservableValue observableSource;
	private IObservableValue observableWidget;

	/**
	 * Constructor for dataProvider.
	 */
	public DataBinding(IDataProvider dataProvider, Object target, String path, String type, String mode) {
		assert dataProvider != null : "DataProvider is null";
		assert target != null : "Binding widget is null";
		assert path != null : "Binding path is null";
		this.dataProvider = dataProvider;
		this.setTarget(target);
		this.path = path;
		this.type = type;
		this.mode = mode;
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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue observableWidget = getObservableWidget();
		/* If observableWidget is null, we need only return the data from provider. */
		if (observableWidget == null) {
			return dataProvider.getData(path);
		}
		// TODO
		Object valueType = dataProvider.getDataType(path);
		IObservableValue observableSource = getObservableSource(valueType);
		IBindingContext bindingContext = dataProvider.getBindingContext();
		if (bindingContext != null) {
			bindingContext.bind(observableSource, observableWidget, mode);
		}
		if (observableSource != null) {
			return observableSource.getValue();
		}
		return null;
	}

	public IObservableValue getObservableSource(Object valueType) {
		if (observableSource == null) {
			observableSource = createObservableSource(valueType);
		}
		return observableSource;
	}

	public IObservableValue getObservableWidget() {
		if (observableWidget == null) {
			observableWidget = createObservableWidget();
		}
		return observableWidget;
	}

	/**
	 * Create Observable Widget.
	 */
	protected IObservableValue createObservableWidget() {
		if (target instanceof Control) {
			return ObservableValueUtil.observePropertyValue((Control) target, type);
		}
		return null;
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
	 * Create Observable Source.
	 */
	protected IObservableValue createObservableSource(Object valueType) {
		return dataProvider.createObservableValue(valueType, path);
	}
}
