package org.eclipse.e4.xwt.tests.databinding.datacontext;

public class Factory {
	public static Person createPerson() {
		Person person = new Person();
		person.setFirstName("Luc");
		person.setLastName("GAMEL");
		return person;
	}

	public static Company createCompany() {
		Company company = new Company();
		company.setName("Soyatec");
		company.setManager(createPerson());
		return company;
	}
}
