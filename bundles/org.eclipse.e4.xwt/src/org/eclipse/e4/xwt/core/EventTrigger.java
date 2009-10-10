package org.eclipse.e4.xwt.core;

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
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
			
			Widget widget = UserDataHelper.getWidget(target);
			Controller eventController = (Controller) widget.getData(IUserDataConstants.XWT_CONTROLLER_KEY);
			if (eventController == null) {
				eventController = new Controller();
				widget.setData(IUserDataConstants.XWT_CONTROLLER_KEY, eventController);
			}
			Runnable runnable = createRunnable(source);
			try {
				Method method = runnable.getClass().getDeclaredMethod("run");
				eventController.setEvent(event, widget, this, method);
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}
	
	protected Runnable createRunnable(Object target) {
		return new SettersAction(target);
	}
	
	class SettersAction implements Runnable {
		protected Object target;
		public SettersAction(Object target) {
			this.target = target;
		}
		public void run() {
		}
	}
}
