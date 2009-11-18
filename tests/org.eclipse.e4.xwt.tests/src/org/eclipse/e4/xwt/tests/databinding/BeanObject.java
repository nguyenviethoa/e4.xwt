package org.eclipse.e4.xwt.tests.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BeanObject {
	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
