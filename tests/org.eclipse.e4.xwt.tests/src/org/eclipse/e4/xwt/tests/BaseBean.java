/**
 *
 */
package org.eclipse.e4.xwt.tests;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A Base bean that implements the JavaBean change support
 *
 * @author hceylan
 *
 */
public class BaseBean {

	protected final PropertyChangeSupport changeSupport;

	/**
	 *
	 */
	public BaseBean() {
		super();

		this.changeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Adds the listener for the property with the name propertyName
	 *
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes the listener for the property with the name propertyName
	 *
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
