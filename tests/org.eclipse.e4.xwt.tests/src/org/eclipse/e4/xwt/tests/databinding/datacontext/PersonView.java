package org.eclipse.e4.xwt.tests.databinding.datacontext;

import java.net.URL;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

public class PersonView extends Composite {

	public PersonView(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	
	public static void main(String[] args) {
		URL url = PersonView.class.getResource("Person.xwt");
		try {
			XWT.open(url, Factory.createPerson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
