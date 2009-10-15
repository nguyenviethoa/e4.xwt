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
package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.ObservableValueManager;
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

		IObservableValueManager manager = UserData.getObservableValueManager(observed);
		if (manager == null) {
			manager = new ObservableValueManager(observed);
			UserData.setObservableValueManager(observed, manager);
		}
		try {
			controller.setEvent(event, UserData.getWidget(observed), manager, property, IObservableValueManager.class.getDeclaredMethod("changeValueHandle", Object.class, org.eclipse.swt.widgets.Event.class));
			manager.registerValue(property, this);
		} catch (Exception e) {
			LoggerManager.log(e);
			return;
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
