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
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.swt.SWTProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class DataBinding {

	private IDataProvider dataProvider;
	private Object target;
	private String path;
	private String type;

	private IObservableValue observableSource;
	private IObservableValue observableWidget;

	/**
	 * Constructor for dataProvider.
	 */
	public DataBinding(IDataProvider dataProvider, Object target, String path, String type) {
		assert dataProvider != null : "DataProvider is null";
		assert target != null : "Binding widget is null";
		assert path != null : "Binding path is null";
		this.dataProvider = dataProvider;
		this.setTarget(target);
		this.path = path;
		this.type = type;
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
		Object valueType = observableWidget.getValueType();
		IObservableValue observableSource = getObservableSource(valueType);
		IBindingContext bindingContext = dataProvider.getBindingContext();
		if (bindingContext != null) {
			bindingContext.bind(observableSource, observableWidget);
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
			Control control = (Control) target;
			if (SWTProperties.TEXT.equalsIgnoreCase(type)) {
				if (control instanceof Text)
					return SWTObservables.observeText(control, SWT.Modify);
				return SWTObservables.observeText(control);
			} else if (SWTProperties.VISIBLE.equalsIgnoreCase(type)) {
				return SWTObservables.observeVisible(control);
			} else if (SWTProperties.BACKGROUND.equalsIgnoreCase(type)) {
				return SWTObservables.observeBackground(control);
			} else if (SWTProperties.ENABLED.equalsIgnoreCase(type)) {
				return SWTObservables.observeEnabled(control);
			} else if (SWTProperties.FONT.equalsIgnoreCase(type)) {
				return SWTObservables.observeFont(control);
			} else if (SWTProperties.FOREGROUND.equalsIgnoreCase(type)) {
				return SWTObservables.observeForeground(control);
			} else if (SWTProperties.MAX.equalsIgnoreCase(type)) {
				return SWTObservables.observeMax(control);
			} else if (SWTProperties.MIN.equalsIgnoreCase(type)) {
				return SWTObservables.observeMin(control);
			} else if (SWTProperties.SELECTION.equalsIgnoreCase(type)) {
				return SWTObservables.observeSelection(control);
			} else if (SWTProperties.SELECTION_INDEX.equalsIgnoreCase(type)) {
				return SWTObservables.observeSingleSelectionIndex(control);
			} else if (SWTProperties.TOOLTIP_TEXT.equalsIgnoreCase(type)) {
				return SWTObservables.observeTooltipText(control);
			}
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
