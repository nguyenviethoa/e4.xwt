package org.eclipse.e4.xwt.core;

import java.util.ArrayList;
import java.util.Collection;

public class EventTrigger extends TriggerBase {
	protected String sourceName;
	protected String routedEvent;
	protected Collection<TriggerAction> actions = new ArrayList<TriggerAction>();

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

	public Collection<TriggerAction> getActions() {
		return actions;
	}

	public void setActions(Collection<TriggerAction> actions) {
		this.actions = actions;
	}

}
