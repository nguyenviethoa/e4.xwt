package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
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
