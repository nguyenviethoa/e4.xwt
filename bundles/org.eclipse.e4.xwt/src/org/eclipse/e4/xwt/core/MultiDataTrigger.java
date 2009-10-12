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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.swt.widgets.Widget;

public class MultiDataTrigger extends TriggerBase {
	protected Condition[] conditions;
	protected Setter[] setters;

	public Condition[] getConditions() {
		return conditions;
	}

	public void setConditions(Condition[] conditions) {
		this.conditions = conditions;
	}

	public Setter[] getSetters() {
		return setters;
	}

	public void setSetters(Setter[] setters) {
		this.setters = setters;
	}

	class ValueChangeListener extends AbstractValueChangeListener {
		public ValueChangeListener(Object element) {
			super(element);
		}

		public void handleValueChange(ValueChangeEvent event) {
			for (Condition condition : getConditions()) {
				if (!condition.evoluate(element)) {
					restoreValues();
					return;
				}
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
	}

	@Override
	public void on(Object target) {
		if (getConditions().length == 0) {
			return;
		}		
		Widget widget = UserDataHelper.getWidget(target);
		if (widget == null) {
			return;
		}		

		ValueChangeListener changeListener = new ValueChangeListener(target);
		for (Condition condition : getConditions()) {
			String sourceName = condition.getSourceName();
			
			IBinding binding = condition.getBinding();
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
				Object sourceObject = getElementByName(target, sourceName);
				Widget sourceWidget = UserDataHelper.getWidget(sourceObject);
				bindingTarget = XWT.getDataContext(sourceWidget);
			}
			
			if (!(bindingTarget instanceof IObservableValue)) {
				return;
			}
			IObservableValue observableValue = (IObservableValue) bindingTarget;
			observableValue.addValueChangeListener(changeListener);
		}
	}
}
