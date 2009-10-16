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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;

public class RadioEventGroup extends AbstractEventGroup {

	public RadioEventGroup(String... names) {
		super(names);
	}

	public void fireEvent(IObservableValueManager manager, IProperty property) {
		Object host = manager.getHost();
		Controller controller = UserData.findEventController(host);
		if (controller == null) {
			controller = UserData.updateEventController(host);
		}
		IMetaclass metaclass = XWT.getMetaclass(host); 

		EventProperty reaisedEventProperty = (EventProperty) property;
		String raisedName = reaisedEventProperty.getEvent().getName();
		for (String name : getEventNames()) {
			if (name.equalsIgnoreCase(raisedName)) {
				continue;
			}
			String eventPropertyName = IEventConstants.getEventPropertyName(name);
			IProperty eventProperty = metaclass.findProperty(eventPropertyName);

			IObservableValue value = manager.getValue(eventProperty);
			if (value != null) {
				value.setValue(false);
			}
		}
	}

	public void registerEvent(IObservableValueManager manager,
			IProperty property) {
		Object host = manager.getHost();
		Controller controller = UserData.findEventController(host);
		if (controller == null) {
			controller = UserData.updateEventController(host);
		}
		IMetaclass metaclass = XWT.getMetaclass(host); 

		EventProperty reaisedEventProperty = (EventProperty) property;
		String raisedName = reaisedEventProperty.getEvent().getName();
		for (String name : getEventNames()) {
			if (name.equalsIgnoreCase(raisedName)) {
				continue;
			}
			String eventPropertyName = IEventConstants.getEventPropertyName(name);
			String eventName = IEventConstants.getEventName(name);
			IEvent event = metaclass.findEvent(eventName);
			IProperty eventProperty = metaclass.findProperty(eventPropertyName);
			
			try {
				controller.setEvent(event, UserData.getWidget(host), manager, eventProperty, IObservableValueManager.class.getDeclaredMethod("changeValueHandle", Object.class, org.eclipse.swt.widgets.Event.class));
			} catch (Exception e) {
				LoggerManager.log(e);
				return;
			}
		}
	}
}
