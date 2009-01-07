package org.eclipse.e4.xwt.tests;

import org.eclipse.e4.xwt.tests.controls.ControlTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XwtTestSuite extends TestSuite {
	public static final Test suite() {
		return new XwtTestSuite();
	}

	public XwtTestSuite() {
		addTest(ControlTestSuite.suite());
	}
}
