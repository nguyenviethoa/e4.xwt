package org.eclipse.e4.xwt.internal.utils;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IEventGroup;
import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.databinding.EventPropertyObservableValue;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.swt.widgets.Event;

public class ObservableValueManager implements IObservableValueManager {
	protected HashMap<String, IObservableValue> map;
	protected Object host;
	
	public ObservableValueManager(Object host) {
		this.host = host;
	}
	
	public void changeValueHandle(Object object, Event event){
		EventProperty property = (EventProperty) object;
		IObservableValue value = map.get(property.getName());
		value.setValue(true);
		
		IMetaclass metaclass = XWT.getMetaclass(host);
		IEventGroup eventGroup = metaclass.getEventGroup(property.getEvent().getName());
		eventGroup.fireEvent(host, property);
	}
	
	public void registerValue(IProperty property, IObservableValue observableValue) {
		if (map == null) {
			map = new HashMap<String, IObservableValue>();
		}
		map.put(property.getName(), observableValue);
		
		IMetaclass metaclass = XWT.getMetaclass(host);
		IEventGroup eventGroup = metaclass.getEventGroup(property.getName());
		if (eventGroup != null) {
			eventGroup.registerEvent(this, property);
		}
	}
	
	public IObservableValue getValue(IProperty property) {
		if (map == null) {
			return null;
		}
		return map.get(property.getName());
	}
}
