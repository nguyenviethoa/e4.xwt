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
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.internal.core.Binding;
import org.eclipse.e4.xwt.internal.core.BindingExpressionPath;
import org.eclipse.e4.xwt.internal.core.ScopeManager;
import org.eclipse.e4.xwt.metadata.ModelUtils;
import org.eclipse.jface.viewers.Viewer;
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

	private IObservable observableSource;
	private IObservable observableWidget;

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
		IObservable observableWidget = getObservableWidget();
		IObservable observableSource = getObservableSource(ScopeManager.VALUE);

		/* If observableWidget is null, we need only return the data from provider. */
		if (observableWidget == null) {
			if (observableSource == null) {
				// TODO should raise an exception
				return null;
			}
			if (observableSource instanceof IObservableValue) {
				return ((IObservableValue)observableSource).getValue();				
			}
			return observableSource;
		}
		
		IDataProvider dataProvider = getDataProvider();		
		IBindingContext bindingContext = dataProvider.getBindingContext();
		if (bindingContext != null && observableSource != null) {
			Object target = getControl();
			if (target instanceof Text && getTargetProperty().equalsIgnoreCase("text")) {
				if (isSourceProeprtyReadOnly()) {
					Text text = (Text) target;
					text.setEditable(false);
				}
			} else if (target instanceof Button && getTargetProperty().equalsIgnoreCase("selection")) {
				if (isSourceProeprtyReadOnly()) {
					Button button = (Button) target;
					button.setEnabled(false);
				}
			} else if ((target instanceof Combo || target instanceof CCombo) && getTargetProperty().equalsIgnoreCase("text")) {
				if (isSourceProeprtyReadOnly()) {
					Control control = (Control) target;
					control.setEnabled(false);
				}
			} else if (target instanceof MenuItem && getTargetProperty().equalsIgnoreCase("selection")) {
				if (isSourceProeprtyReadOnly()) {
					MenuItem menuItem = (MenuItem) target;
					menuItem.setEnabled(false);
				}
			}
			bindingContext.bind(observableSource, observableWidget, this);
		}
		
		if (observableSource instanceof IObservableValue) {
			return ((IObservableValue)observableSource).getValue();				
		}
		return observableSource;
	}

	public boolean isSourceProeprtyReadOnly() {
		IDataProvider dataProvider = getDataProvider();
		try {
			return ScopeManager.isProeprtyReadOnly(dataProvider, getSourcePropertyExpression());
		} catch (XWTException e) {
		}
		return false;
	}

	
	public IObservable getObservableSource(int observeKind) {
		if (observableSource == null) {
			IDataProvider dataProvider = getDataProvider();
			try {
				observableSource = ScopeManager.observe(getControl(), dataProvider.getData(null), getSourcePropertyExpression(), getUpdateSourceTrigger(), observeKind);
			} catch (XWTException e) {
			}
		}
		return observableSource;
	}

	public IObservable getObservableWidget() {
		if (observableWidget == null) {
			Object target = getControl();
			Object host = getHost();
			String targetProperty = getTargetProperty();
			targetProperty = ModelUtils.normalizePropertyName(targetProperty);
			int observeKind = ScopeManager.AUTO;
			if (host instanceof Viewer && "input".equals(targetProperty)) {
				// It is possible to use List
				getObservableSource(ScopeManager.COLLECTION);
				if (observableSource instanceof IObservableList) {
					return null;
				}
				else if (observableSource instanceof IObservableSet) {
					return null;
				}
			}
			try {
				BindingExpressionPath path = getTargetPropertyExpression();
				if (path.isEmptyPath()) {
					return null;
				}
				observableWidget = ScopeManager.observe(target, host, path, getUpdateSourceTrigger(), observeKind);
			} catch (XWTException e) {
			}
		}
		return observableWidget;
	}
}
