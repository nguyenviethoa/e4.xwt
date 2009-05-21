package org.eclipse.e4.xwt.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.ControlTestSuite;
import org.eclipse.e4.xwt.tests.events.EventsTestSuite;
import org.eclipse.e4.xwt.tests.name.NameTestSuite;

public class XwtTestSuite extends TestSuite {
	public static final Test suite() {
		return new XwtTestSuite();
	}

	/**
	 * The execution of the tests must be started from simple to complex in order.
	 * <ol>
	 * <il>XAML</il> <il>UI</il> <il>DataBining</il>
	 * </ol>
	 */
	public XwtTestSuite() {
		// XAML
		addXAMLTests();

		// widgets SWT
		addSWTTests();

		// widgets JFace
		addJFaceTests();

		// events
		addEventTests();

		// data binding
		addDataBindingTests();

		// Widget binding
		addWidgetBindingTests();

		// Data provider
		addDataProviderTests();

		// Style provider
		addStyleTests();
	}

	protected void addXAMLTests() {
		addTest(NameTestSuite.suite());
	}

	protected void addSWTTests() {
		addTest(ControlTestSuite.suite());
	}

	protected void addJFaceTests() {
	}

	protected void addEventTests() {
		addTest(EventsTestSuite.suite());
	}

	protected void addDataBindingTests() {
	}

	protected void addWidgetBindingTests() {
	}

	protected void addDataProviderTests() {
	}

	protected void addStyleTests() {
	}

	protected void addCustimizationTests() {
	}
}
