package org.eclipse.e4.xwt.tests.databinding.datacontext;

import java.net.URL;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

public class CompanyView extends Composite {

	public CompanyView(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		URL url = CompanyView.class.getResource("CompanyView.xwt");
		try {
			XWT.open(url, Factory.createCompany());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
