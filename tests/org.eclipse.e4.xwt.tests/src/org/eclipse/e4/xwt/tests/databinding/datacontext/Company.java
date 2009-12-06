package org.eclipse.e4.xwt.tests.databinding.datacontext;

public class Company {
	protected String name;
	protected Person manager;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getManager() {
		return manager;
	}

	public void setManager(Person manager) {
		this.manager = manager;
	}
}
