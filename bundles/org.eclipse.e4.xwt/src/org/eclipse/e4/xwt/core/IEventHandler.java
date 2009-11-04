package org.eclipse.e4.xwt.core;

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

public interface IEventHandler {

	boolean hasEvent(Object receiver, IEvent event);

	void addEvent(int eventType, String name, IEvent event,
			Widget control, Object receiver, Object arg, Method method);

	void setEvent(IEvent event, Widget control,
			Object receiver, Object arg, Method method);

	void handleEvent(Event e);
}