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
package demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import demo_model.Company;
import demo_model.Demo_modelFactory;
import demo_model.Demo_modelPackage;
import demo_model.Employee;

public class Main {

	public static final String DATA_FILE = "data.xmi";

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		XWT.addTracking(Tracking.DATABINDING);
		URL url = Main.class.getResource("main.xwt");
		try {
			EPackage.Registry.INSTANCE.put(Demo_modelPackage.eNS_URI, Demo_modelPackage.eINSTANCE);
			XWT.open(url, loadModel(DATA_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ResourceDictionary readData() throws IOException, FileNotFoundException {
		Company company = loadModel(DATA_FILE);
		ResourceDictionary dico = new ResourceDictionary();
		dico.put(company.getName(), company);
		return dico;
	}

	public static Company loadModel(String path) throws IOException, FileNotFoundException {
		XMIResourceImpl res = new XMIResourceImpl();
		res.load(new FileInputStream(path), Collections.EMPTY_MAP);
		Company company = (Company) res.getContents().get(0);
		return company;
	}

	private static void writeData() throws IOException, FileNotFoundException {
		Company company = Demo_modelFactory.eINSTANCE.createCompany();
		company.setName("My Company");

		Employee employee = Demo_modelFactory.eINSTANCE.createEmployee();
		employee.setCompany(company);
		employee.setFirstname("toto");
		company.getEmployees().add(employee);
		saveModel(company, DATA_FILE);
	}

	public static void saveModel(Company company, String path) throws FileNotFoundException, IOException {
		XMIResourceImpl res = new XMIResourceImpl();
		res.getContents().add(company);
		res.save(new FileOutputStream(path), Collections.EMPTY_MAP);
	}
}
