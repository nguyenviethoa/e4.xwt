package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.swt.widgets.Widget;

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
	
	public void on(final Object target) {
		if (value == null) {
			return;
		}
		Widget widget = UserDataHelper.getWidget(target);
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
		observableValue.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(ValueChangeEvent event) {
				Widget widget = UserDataHelper.getWidget(target);
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
				if (!currentValue.equals(normalizedValue)) {
					return;					
				}
				
				for (SetterBase setter : getSetters()) {
					setter.applyTo(target);
				}
			}
		});
	}
}
