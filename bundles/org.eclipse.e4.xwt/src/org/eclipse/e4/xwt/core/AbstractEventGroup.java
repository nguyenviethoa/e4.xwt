package org.eclipse.e4.xwt.core;

import org.eclipse.e4.xwt.IEventGroup;

public abstract class AbstractEventGroup implements IEventGroup {
	protected String[] names; 
	
	public AbstractEventGroup(String ... names) {
		this.names = names;
	}
	
	public String[] getEventNames() {
		return names;
	}
}
