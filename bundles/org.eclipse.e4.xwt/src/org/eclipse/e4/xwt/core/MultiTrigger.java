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
import org.eclipse.e4.xwt.databinding.ObservableValueUtil;

public class MultiTrigger extends TriggerBase {
	protected Condition[] conditions;
	protected SetterBase[] setters;

	public Condition[] getConditions() {
		return conditions;
	}

	public void setConditions(Condition[] conditions) {
		this.conditions = conditions;
	}

	public SetterBase[] getSetters() {
		return setters;
	}

	public void setSetters(SetterBase[] setters) {
		this.setters = setters;
	}

	class ValueChangeListener extends AbstractValueChangeListener {
		public ValueChangeListener(Object element) {
			super(element);
		}

		public void handleValueChange(ValueChangeEvent event) {
			for (Condition condition : getConditions()) {
				if (!condition.evaluate(element)) {
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
		ValueChangeListener changeListener = new ValueChangeListener(target);
		for (Condition condition : getConditions()) {
			String propertyName = condition.getProperty();
			String sourceName = condition.getSourceName();

			Object source = getElementByName(target, sourceName);

			IObservableValue observableValue = ObservableValueUtil
					.createWidget(source, propertyName);
			observableValue.addValueChangeListener(changeListener);
		}
	}
}
