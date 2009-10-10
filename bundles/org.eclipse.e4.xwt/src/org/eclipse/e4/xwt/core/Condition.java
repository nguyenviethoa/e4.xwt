package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;

public class Condition {
	public static final Condition[] EMPTY_ARRAY = new Condition[0];

	private IBinding binding;
	private String property;
	private Operator operator = Operator.EQ;

	private String sourceName;
	private Object value;

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public IBinding getBinding() {
		return binding;
	}

	public void setBinding(IBinding binding) {
		this.binding = binding;
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

	public boolean evoluate(Object element) {
		String propertyName = getProperty();
		String sourceName = getSourceName();
		IBinding binding = getBinding();
		Object value = getValue();
		if (value == null) {
			return false;
		}

		Object dataObject = TriggerBase.getElementByName(element, sourceName);

		if (propertyName != null) {
			IMetaclass metaclass = XWT.getMetaclass(dataObject);
			IProperty prop = metaclass.findProperty(propertyName);
			if (prop != null && value != null) {
				IConverter converter = XWT.findConvertor(value.getClass(), prop
						.getType());
				Object trueValue = value;
				if (converter != null) {
					trueValue = converter.convert(trueValue);
				}
				try {
					Object existingValue = prop.getValue(dataObject);
					return trueValue.equals(existingValue);
				} catch (Exception e) {
					LoggerManager.log(e);
				}
			}
		} else if (binding != null) {
			Object existingValue = binding.getValue();
			if (existingValue == null) {
				return false;
			}
			Class<?> currentValueType = existingValue.getClass();
			Class<?> valueType = value.getClass();
			Object normalizedValue = value;
			if (!currentValueType.isAssignableFrom(valueType) && !valueType.isAssignableFrom(currentValueType)) {
				IConverter converter = XWT.findConvertor(valueType, currentValueType);
				if (converter != null) {
					normalizedValue = converter.convert(normalizedValue);
				}
			}
			return existingValue.equals(normalizedValue);
		}
		return false;
	}
}
