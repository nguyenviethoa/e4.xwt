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

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

public class EventTrigger extends TriggerBase {
	protected String sourceName;
	protected String routedEvent;
	protected TriggerAction[] actions;

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getRoutedEvent() {
		return routedEvent;
	}

	public void setRoutedEvent(String routedEvent) {
		this.routedEvent = routedEvent;
	}

	public TriggerAction[] getActions() {
		return actions;
	}

	public void setActions(TriggerAction[] actions) {
		this.actions = actions;
	}

	public void on(Object target) {
		if (routedEvent != null) {
			Object source = getElementByName(target, getSourceName());
			IMetaclass iMetaclass = XWT.getMetaclass(source);
			IEvent event = iMetaclass.findEvent(getRoutedEvent());
			if (event == null) {
				LoggerManager.log("Event " + " is not found in " + source
						.getClass().getName());
				return;
			}
			
			Widget widget = UserData.getWidget(target);
			Controller eventController = UserData.updateEventController(widget);
			SettersAction runnable = createRunnable(source);
			try {
				Method method = runnable.getClass().getDeclaredMethod("run", Object.class, Event.class);
				eventController.setEvent(event, widget, this, this, method);
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}
	
	protected SettersAction createRunnable(Object target) {
		return new SettersAction(target);
	}
	
	class SettersAction {
		protected Object target;
		public SettersAction(Object target) {
			this.target = target;
		}
		public void run(Object object, Event event) {
		}
	}
}
