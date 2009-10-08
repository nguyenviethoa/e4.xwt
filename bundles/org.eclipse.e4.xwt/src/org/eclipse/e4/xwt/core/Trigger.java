package org.eclipse.e4.xwt.core;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.databinding.BeanObservableValue;
import org.eclipse.e4.xwt.databinding.ObservableValueUtil;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;

public class Trigger extends TriggerBase {
	protected String property;
	protected String sourceName;
	protected Object value;
	protected Collection<Setter> setters = new ArrayList<Setter>();

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

	public Collection<Setter> getSetters() {
		return setters;
	}

	public void setSetters(Collection<Setter> setters) {
		this.setters = setters;
	}

	public void apply(final Object target) {
		if (property != null) {
			IObservableValue observableValue = ObservableValueUtil.createWidget(target, property);			
			observableValue.addValueChangeListener(new IValueChangeListener() {
				public void handleValueChange(ValueChangeEvent event) {
					Class<?> valueType = BeanObservableValue.getValueType(target
							.getClass(), property);
					if (valueType == null) {
						LoggerManager.log("Type of the property " + property + " is not found in " + target
								.getClass().getName());
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
						return;
					}
					
					IMetaclass metaclass = XWT.getMetaclass(target);
					for (Setter setter : getSetters()) {
						String propName = setter.getProperty();
						String propValue = setter.getValue();
						IProperty prop = metaclass.findProperty(propName);
						if (prop != null && propValue != null) {
							Object toValue = XWT.convertFrom(prop.getType(), propValue);
							try {
								prop.setValue(target, toValue);
							} catch (Exception e) {
								LoggerManager.log(e);
							}
						}
					}
				}
			});
		}
	}
}
