package org.eclipse.e4.xwt.internal.utils;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IEventGroup;
import org.eclipse.e4.xwt.IObservableValueListener;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.swt.widgets.Event;

public class ObservableValueManager implements IObservableValueListener {
	protected HashMap<String, IObservableValue> map;
	protected Object host;
	
	public ObservableValueManager(Object host) {
		this.host = host;
	}
	
	public Object getHost() {
		return host;
	}
	
	public void changeValueHandle(Object object, Event event){
		// TODO the cast is not clean. 
		EventProperty property = (EventProperty) object;
		IObservableValue value = map.get(property.getName());
		if (value != null) {
			Boolean oldValue = (Boolean) value.getValue();
			if (oldValue == null) {
				oldValue = false;
			}
			value.setValue(!oldValue);
		}
		
		IMetaclass metaclass = XWT.getMetaclass(host);
		
		// TODO this conversion should be simplied
		String eventName = IEventConstants.normalize(property.getEvent().getName());
		IEventGroup eventGroup = metaclass.getEventGroup(eventName);
		if (eventGroup != null) {
			eventGroup.fireEvent(this, property);
		}
	}
	
	public void registerValue(IProperty property, IObservableValue observableValue) {
		if (map == null) {
			map = new HashMap<String, IObservableValue>();
		}
		map.put(property.getName(), observableValue);
		
		IMetaclass metaclass = XWT.getMetaclass(host);
		// TODO it is not clean. 
		EventProperty eventProperty  = (EventProperty) property;
		
		// TODO this conversion should be simplied
		String eventName = IEventConstants.normalize(eventProperty.getEvent().getName());		
		IEventGroup eventGroup = metaclass.getEventGroup(eventName);
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
