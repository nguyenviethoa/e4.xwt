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
package org.eclipse.e4.xwt.core;

import java.util.HashMap;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.databinding.BeanObservableValue;
import org.eclipse.e4.xwt.databinding.ObservableValueUtil;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.swt.widgets.Widget;

public class Trigger extends TriggerBase {
	private String property;
	private String sourceName;
	private Operator operator = Operator.EQ;
	private Object value;
	private SetterBase[] setters;
	
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public SetterBase[] getSetters() {
		if (setters == null) {
			return SetterBase.EMPTY_SETTERS;
		}
		return setters;
	}

	public void setSetters(SetterBase[] setters) {
		this.setters = setters;
	}

	public void on(Object target) {
		if (property != null) {
			final Object source = getElementByName(target, sourceName);
			IObservableValue observableValue = ObservableValueUtil.createWidget(source, property);			
			observableValue.addValueChangeListener(new AbstractValueChangeListener(target) {
				public void handleValueChange(ValueChangeEvent event) {
					Class<?> valueType = BeanObservableValue.getValueType(source.getClass(), property);
					if (valueType == null) {
						LoggerManager.log("Type of the property " + property + " is not found in " + source
								.getClass().getName());
						return;
					}
					Widget widget = UserDataHelper.getWidget(source);
					if (widget == null) {
						return;
					}
					
					//
					// test value ==
					//
					IConverter converter = XWT.findConvertor(value.getClass(), valueType);
					Object realValue = value;
					if (converter != null) {
						realValue = converter.convert(value);						
					}
					Object newValue = event.diff.getNewValue();
					if (!newValue.equals(realValue)) {
						restoreValues();
						return;
					}
					
					for (SetterBase setter : getSetters()) {
						try {
							Object oldValue = setter.applyTo(element);
							if (oldvalues == null) {
								oldvalues = new HashMap<SetterBase, Object>();
							}
							oldvalues.put(setter, oldValue);
						} catch (RuntimeException e) {
							continue;
						}
					}
				}
			});
		}
	}
}
