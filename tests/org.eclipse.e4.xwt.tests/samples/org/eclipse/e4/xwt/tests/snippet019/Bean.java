/**
 * 
 */
package org.eclipse.e4.xwt.tests.snippet019;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class Bean {
	/* package */PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private String text;
	private List<Bean> list;

	public Bean(String text) {
		this.text = text;
		list = new ArrayList<Bean>();
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

	public List<Bean> getList() {
		if (list == null)
			return null;
		return new ArrayList<Bean>(list);
	}

	public void setList(List<Bean> list) {
		if (list != null)
			list = new ArrayList<Bean>(list);
		changeSupport.firePropertyChange("list", this.list,
				this.list = list);
	}

	public boolean hasListeners(String propertyName) {
		return changeSupport.hasListeners(propertyName);
	}
}