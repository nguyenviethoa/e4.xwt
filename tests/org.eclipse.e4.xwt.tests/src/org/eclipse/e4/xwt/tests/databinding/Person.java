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

public class Person {
	private String name = "toto";
	private int age = 10;
	private boolean maried = true;
	private Country nationality = Country.FR;

	public Country getNationality() {
		return nationality;
	}

	public boolean isMaried() {
		return maried;
	}

	public int getAge() {
		return age;
	}

	private Address address;

	public Person() {
		address = new Address();
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
