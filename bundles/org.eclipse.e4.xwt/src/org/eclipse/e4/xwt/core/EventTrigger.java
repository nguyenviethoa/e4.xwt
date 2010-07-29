/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
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

import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.Containment;
import org.eclipse.e4.xwt.internal.core.IEventController;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.ModelUtils;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class EventTrigger extends TriggerBase {
	private String sourceName;
	private String routedEvent;
	private TriggerAction[] actions = TriggerAction.EMPTY_ARRAY;

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

	@Containment
	public TriggerAction[] getActions() {
		return actions;
	}

	public void setActions(TriggerAction[] actions) {
		this.actions = actions;
	}

	@Override
	public void prepare(Object target) {
		String routedEvent = getRoutedEvent();
		if (routedEvent != null) {
			Object source = getElementByName(target, getSourceName());
			IMetaclass metaclass = XWT.getMetaclass(source);
			IEvent event = metaclass.findEvent(ModelUtils.normalizeEventName(routedEvent));
			if (event == null) {
				if (routedEvent != null && !routedEvent.toLowerCase().endsWith(IEventConstants.SUFFIX_KEY)) {
					LoggerManager.log("Event " + routedEvent + " is not found in " + source
							.getClass().getName() + ". Please add a suffix \"Event\"!");					
				}
				else {
					LoggerManager.log("Event " + routedEvent + " is not found in " + source
						.getClass().getName());
				}
				return;
			}			
			for (TriggerAction triggerAction : getActions()) {
				triggerAction.initialize(target);
			}

			Widget widget = UserData.getWidget(source);
			IEventController eventController = UserData.updateEventController(source);
			RunableAction runnable = createRunnable(source);
			try {
				Method method = runnable.getClass().getDeclaredMethod("run", Object.class, Event.class);
				eventController.setEvent(event, widget, runnable, this, method);
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}
	
	public void on(Object target) {
	}
	
	protected RunableAction createRunnable(Object target) {
		return new RunableAction(target);
	}
	
	class RunableAction {
		protected Object target;
		public RunableAction(Object target) {
			this.target = target;
		}
		public void run(Object object, Event event) {
			for (TriggerAction triggerAction : EventTrigger.this.getActions()) {
				triggerAction.run(target);
			}
		}
	}
}
