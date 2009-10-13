package org.eclipse.e4.xwt;

public interface IEventGroup {
	String[] getEventNames();
	
	void handleBefore(Object element, String event);
	void handleAfter(Object element, String event);
}
