/**
 * 
 */
package org.eclipse.e4.xwt.tests.snippet019.array;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Bean {
	/* package */PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	
	static Bean[] EMPTY = new Bean[0];
	
	private String text;
	private Bean[] list;

	public Bean(String text) {
		this.text = text;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		changeSupport.firePropertyChange("text", this.text,
				this.text = value);
	}

	public Bean[] getList() {
		if (list == null)
			return EMPTY;
		return list;
	}

	public void setList(Bean[] list) {
		if (list != null)
			list = copy(list);
		changeSupport.firePropertyChange("list", this.list,
				this.list = list);
	}
	
	protected Bean[] copy(Bean[] beans) {
		Bean[] copy = new Bean[beans.length];
		System.arraycopy(beans, 0, copy, 0, beans.length);
		return copy;
	}

	public boolean hasListeners(String propertyName) {
		return changeSupport.hasListeners(propertyName);
	}
}