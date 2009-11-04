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
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.utils.OperatorHelper;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class DataTrigger extends TriggerBase {
	private Object value;
	private Operator operator = Operator.EQ;
	private IBinding binding;
	private SetterBase[] setters;

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	public IBinding getBinding() {
		return binding;
	}
	
	public void setBinding(IBinding binding) {
		this.binding = binding;
	}

	public SetterBase[] getSetters() {
		if (setters == null) {
			return Setter.EMPTY_SETTERS;
		}
		return setters;
	}

	public void setSetters(SetterBase[] setters) {
		this.setters = setters;
	}	
	
	public void on(Object target) {
		if (value == null) {
			return;
		}
		Widget widget = UserData.getWidget(target);
		if (widget == null) {
			return;
		}		
		Object bindingTarget = null;
		if (binding != null) {
			if (binding instanceof IDynamicBinding) {
				IDynamicBinding dynamicBinding = (IDynamicBinding) binding;
				bindingTarget = dynamicBinding.createBoundSource();
			}
			else {
				bindingTarget = binding.getValue();
			}			
		}
		else {
			bindingTarget = XWT.getDataContext(widget);
		}
		if (!(bindingTarget instanceof IObservableValue)) {
			return;
		}
		IObservableValue observableValue = (IObservableValue) bindingTarget;
		observableValue.addValueChangeListener(new AbstractValueChangeListener(target) {
			public void handleValueChange(ValueChangeEvent event) {
				Widget widget = UserData.getWidget(element);
				if (widget == null) {
					return;
				}
				Object currentValue = binding.getValue();
				if (currentValue == null) {
					return;
				}
				Class<?> currentValueType = currentValue.getClass();
				Class<?> valueType = value.getClass();
				Object normalizedValue = value;
				if (!currentValueType.isAssignableFrom(valueType) && !valueType.isAssignableFrom(currentValueType)) {
					IConverter converter = XWT.findConvertor(valueType, currentValueType);
					if (converter != null) {
						normalizedValue = converter.convert(normalizedValue);
					}
				}
				if (!OperatorHelper.compare(currentValue, operator, normalizedValue)) {
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
