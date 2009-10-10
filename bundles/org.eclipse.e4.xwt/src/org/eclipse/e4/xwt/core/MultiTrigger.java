package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
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

	class ValueChangeListener implements IValueChangeListener {
		protected Object element;

		public ValueChangeListener(Object element) {
			this.element = element;
		}

		public void handleValueChange(ValueChangeEvent event) {
			for (Condition condition : getConditions()) {
				if (!condition.evoluate(element)) {
					return;
				}
			}
			for (SetterBase setter : getSetters()) {
				setter.applyTo(element);
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
