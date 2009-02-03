/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Person {
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	private String name = "toto";
	private Address address;

	public Person() {
		address = new Address();
	}

	public void setName(String value) {
		String oldValue = this.name;
		this.name = value;
		changeSupport.firePropertyChange("name", oldValue, value);
	}

	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		Address oldValue = this.address;
		this.address = address;
		changeSupport.firePropertyChange("address", oldValue, address);
	}

}
