package org.eclipse.e4.xwt.core;

import java.util.ArrayList;
import java.util.Collection;

public class TriggerBase {
	protected Collection<TriggerAction> entryActions = new ArrayList<TriggerAction>();
	protected Collection<TriggerAction> exitActions = new ArrayList<TriggerAction>();

	public Collection<TriggerAction> getEntryActions() {
		return entryActions;
	}
	public void setEntryActions(Collection<TriggerAction> entryActions) {
		this.entryActions = entryActions;
	}
	public Collection<TriggerAction> getExitActions() {
		return exitActions;
	}
	public void setExitActions(Collection<TriggerAction> exitActions) {
		this.exitActions = exitActions;
	}
}
