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
package org.eclipse.e4.xwt.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.attachedproperty.AttachedPropertyTests;
import org.eclipse.e4.xwt.tests.clr.XWTTestSuite;
import org.eclipse.e4.xwt.tests.controls.ControlsTestSuite;
import org.eclipse.e4.xwt.tests.databinding.BindingTestSuite;
import org.eclipse.e4.xwt.tests.events.EventsTestSuite;
import org.eclipse.e4.xwt.tests.jface.JFaceTestSuite;
import org.eclipse.e4.xwt.tests.name.NameTestSuite;
import org.eclipse.e4.xwt.tests.namespace.handler.NamespacehandlerTestSuite;
import org.eclipse.e4.xwt.tests.resources.ResourcesTestSuite;
import org.eclipse.e4.xwt.tests.trigger.TriggerTestSuite;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class XwtTestSuite extends TestSuite {
	public static final Test suite() {
		return new XwtTestSuite();
	}

	/**
	 * The execution of the tests must be started from simple to complex in order.
	 */
	public XwtTestSuite() {
		// XAML
		addXAMLTests();

		// XWT
		addXWTTests();

		// widgets SWT
		addControlsTests();

		addUserControlTests();

		addKeyBindingTests();

		// widgets JFace
		addJFaceTests();

		// i18n
		addI18NTests();

		// events
		addEventTests();

		// data binding
		addDataBindingTests();

		// Presentation tests
		addPresentationTests();

		// Style provider
		addStyleTests();

		// triggers
		addTriggerTests();
	}

	protected void addXAMLTests() {
		addTest(NameTestSuite.suite());
		addTest(ResourcesTestSuite.suite());
	}

	protected void addXWTTests() {
		addTest(XWTTestSuite.suite());
		addTest(new TestSuite(AttachedPropertyTests.class));
	}

	protected void addControlsTests() {
		addTest(ControlsTestSuite.suite());
	}

	protected void addUserControlTests() {
	}

	protected void addKeyBindingTests() {
	}

	protected void addPresentationTests() {
	}

	protected void addJFaceTests() {
		addTest(JFaceTestSuite.suite());
	}

	protected void addEventTests() {
		addTest(EventsTestSuite.suite());
	}

	protected void addDataBindingTests() {
		addTest(BindingTestSuite.suite());
	}

	protected void addI18NTests() {
	}

	protected void addStyleTests() {
	}

	protected void addTriggerTests() {
		addTest(TriggerTestSuite.suite());
	}

	protected void addCustomizationTests() {
		addTest(NamespacehandlerTestSuite.suite());
	}
}
