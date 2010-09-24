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

import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.Containment;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.ModelUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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

			RunableAction runnable = createRunnable(source);
			try {
				runnable.setEventTrigger(event);
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
	
	class RunableAction implements Listener, Runnable {
		protected Object target;
		private int count;
		private Event event;
		private int eventType;
		public RunableAction(Object target) {
			this.target = target;
		}
		
		public void run() {
			count--;
			if (count == 0 && !event.widget.isDisposed()) {
				final Display display = event.widget.getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						display.removeFilter(eventType, RunableAction.this);
						event.widget.notifyListeners(eventType, event);
						display.addFilter(eventType, RunableAction.this);						
					}
				});
			}
		}
		
		protected void setEventTrigger(IEvent event) {
			Widget widget = UserData.getWidget(target);
			String name = event.getName();
			eventType = Controller.getEventTypeByName(name);
			if (eventType != SWT.None) {
				widget.getDisplay().addFilter(eventType, this);
			}
		}
		
		public void handleEvent(Event event) {
			Widget widget = UserData.getWidget(target);
			if (event.widget != widget) {
				return;
			}
			
			// execute the animation actions first and then normal events 
			count = EventTrigger.this.getActions().length;
			
			this.event = Controller.copy(event);
			for (TriggerAction triggerAction : EventTrigger.this.getActions()) {
				triggerAction.run(event, target, this);
			}
			event.type = SWT.NONE;
		}
	}
}
