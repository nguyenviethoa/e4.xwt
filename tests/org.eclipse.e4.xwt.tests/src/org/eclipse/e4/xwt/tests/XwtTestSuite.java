package org.eclipse.e4.xwt.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.ControlTestSuite;
import org.eclipse.e4.xwt.tests.controls.button.ButtonTestSuite;
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
		addTest(NameTestSuite.suite());
		addTest(ButtonTestSuite.suite());
		addTest(ControlTestSuite.suite());
		addTest(EventsTestSuite.suite());
	}
}
