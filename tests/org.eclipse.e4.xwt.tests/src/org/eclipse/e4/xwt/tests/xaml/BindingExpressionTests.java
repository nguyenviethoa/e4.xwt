/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.xaml;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.internal.xml.DocumentObject;
import org.eclipse.e4.xwt.internal.xml.Element;
import org.eclipse.e4.xwt.internal.xml.ElementManager;

/**
 * This class test the function about OperatorHelper
 * 
 * @author YaHong.Song(yahong.song@soyatec.com)
 * 
 */
public class BindingExpressionTests extends TestCase {

	/**
	 * The testcase for EQ test
	 * 
	 * @throws Exception
	 */
	public void testBindingExpression() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_path() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_Path" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "employees.name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_Attached() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_Attached" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "(Person.name)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_Indexer() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_Indexer" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "persons[1].name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_Namespace() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_Namespace" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "(gui.Person.name)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_NamespaceUpdateSourceTrigger() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_NamespaceUpdateSourceTrigger" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "path", "(gui.Person.name)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testBindingExpression_UpdateSourceTrigger() throws Exception {
		URL url = BindingExpressionTests.class.getResource("BindingExpression_UpdateSourceTrigger" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			ElementManager elementManager = new ElementManager();
			Element element = elementManager.load(url);
			checkTextValue(element, "UpdateSourceTrigger", "PropertyChanged");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void checkTextValue(Element element, String name, String value) {
		Attribute attribute = element.getAttribute("Text");
		assertEquals(attribute.getName(), "Text");
		assertEquals(attribute.getChildren().length, 1);
		DocumentObject child = attribute.getChildren()[0];
		assertEquals(child.getName(), "Binding");
		assertTrue(child instanceof Element);
		
		Element childElement = (Element)child;
		Attribute pathAttribute = childElement.getAttribute(name);
		assertTrue(pathAttribute != null);
		assertEquals(pathAttribute.getContent(), value);			
	}
}