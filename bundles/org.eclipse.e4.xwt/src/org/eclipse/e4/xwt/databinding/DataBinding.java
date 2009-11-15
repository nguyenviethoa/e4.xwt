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
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.internal.core.Binding;
import org.eclipse.e4.xwt.internal.core.ScopeManager;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

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
	public DataBinding(Binding binding, IDataProvider dataProvider) {
		super(binding, dataProvider);
		assert dataProvider != null : "DataProvider is null";
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue observableWidget = getObservableWidget();
		IDataProvider dataProvider = getDataProvider();
		
		IObservableValue observableSource = getObservableSource();
		IBindingContext bindingContext = dataProvider.getBindingContext();

		/* If observableWidget is null, we need only return the data from provider. */
		if (observableWidget == null) {
			if (observableSource == null) {
				// TODO should raise an exception
				return null;
			}
			return observableSource.getValue();
		}
		
		if (bindingContext != null && observableSource != null) {
			Object target = getControl();
			if (target instanceof Text && getTargetProperty().equalsIgnoreCase("text")) {
				String sourceProperty = getSourceProperty();
				if (dataProvider.isPropertyReadOnly(sourceProperty)) {
					Text text = (Text) target;
					text.setEditable(false);
				}
			} else if (target instanceof Button && getTargetProperty().equalsIgnoreCase("selection")) {
				String sourceProperty = getSourceProperty();
				if (dataProvider.isPropertyReadOnly(sourceProperty)) {
					Button button = (Button) target;
					button.setEnabled(false);
				}
			} else if ((target instanceof Combo || target instanceof CCombo) && getTargetProperty().equalsIgnoreCase("text")) {
				String sourceProperty = getSourceProperty();
				if (dataProvider.isPropertyReadOnly(sourceProperty)) {
					Control control = (Control) target;
					control.setEnabled(false);
				}
			} else if (target instanceof MenuItem && getTargetProperty().equalsIgnoreCase("selection")) {
				String sourceProperty = getSourceProperty();
				if (dataProvider.isPropertyReadOnly(sourceProperty)) {
					MenuItem menuItem = (MenuItem) target;
					menuItem.setEnabled(false);
				}
			}
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
			observableSource = ScopeManager.observeValue(getControl(), dataProvider.getData(null), sourceProperty, getUpdateSourceTrigger());
		}
		return observableSource;
	}

	public IObservableValue getObservableWidget() {
		if (observableWidget == null) {
			Object target = getControl();
			Object host = getHost();
			observableWidget = ScopeManager.observableValue(target, host, getTargetProperty(), getUpdateSourceTrigger());
		}
		return observableWidget;
	}
}
