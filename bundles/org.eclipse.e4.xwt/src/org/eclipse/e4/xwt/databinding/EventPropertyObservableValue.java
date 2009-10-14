package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IEvent;

public class EventPropertyObservableValue extends XWTObservableValue {
	private EventProperty property;

	public EventPropertyObservableValue(Object observed, EventProperty property) {
		super(Boolean.class, observed);
		this.property = property;
		
		Controller controller = UserData.findEventController(observed);
		if (controller == null) {
			controller = UserData.updateEventController(observed);
		}
		IEvent event = property.getEvent();
		if (!controller.hasEvent(observed, event)) {
			// make sure the Controller is set locally
			controller = UserData.updateEventController(observed);
			// controller.setEvent(event, control, observed, method);
		}
	}

	@Override
	protected void doSetApprovedValue(Object value) {
		UserData.setData(getObserved(), property.getName(), value);		
	}

	protected Object doGetValue() {
		return UserData.getLocalData(getObserved(), property.getName());
	}
}
