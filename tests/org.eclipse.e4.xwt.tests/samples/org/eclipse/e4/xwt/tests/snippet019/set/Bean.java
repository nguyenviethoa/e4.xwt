/**
 * 
 */
package org.eclipse.e4.xwt.tests.snippet019.set;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

public class Bean {
	/* package */PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private String text;
	private Set<Bean> list;

	public Bean(String text) {
		this.text = text;
		list = new HashSet<Bean>();
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

	public Set<Bean> getList() {
		if (list == null)
			return null;
		return new HashSet<Bean>(list);
	}

	public void setList(Set<Bean> list) {
		if (list != null)
			list = new HashSet<Bean>(list);
		changeSupport.firePropertyChange("list", this.list,
				this.list = list);
	}

	public boolean hasListeners(String propertyName) {
		return changeSupport.hasListeners(propertyName);
	}
}