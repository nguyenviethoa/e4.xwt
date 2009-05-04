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
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ControlDataBinding {
	private Control source;
	private Control target;

	private String sourceProperty;
	private String targetProperty;

	public ControlDataBinding(Control source, Control target,
			String sourceProperty, String targetProperty) {
		this.source = source;
		this.target = target;
		this.sourceProperty = sourceProperty;
		this.targetProperty = targetProperty;
	}

	/**
	 * Get bind value of two bindings.
	 */
	public Object getValue() {
		IObservableValue targetWidget = createWidget(target, targetProperty);
		IObservableValue sourceWidget = createWidget(source, sourceProperty);
		IBindingContext bindingContext = new BindingContext();
		bindingContext.bind(sourceWidget, targetWidget);
		if (sourceWidget != null) {
			return sourceWidget.getValue();
		}
		return null;
	}

	public IObservableValue createWidget(Object object, String property) {
		if (object instanceof Control) {
			return ObservableValueUtil.observePropertyValue((Control) object, property);
		}
		return null;
	}
}
