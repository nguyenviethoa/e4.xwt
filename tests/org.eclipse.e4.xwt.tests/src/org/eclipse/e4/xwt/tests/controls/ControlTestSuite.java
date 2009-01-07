package org.eclipse.e4.xwt.tests.controls;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ControlTestSuite extends TestSuite {
	public static final Test suite() {
		return new ControlTestSuite();
	}

	public ControlTestSuite() {
		addTest(new TestSuite(SimpleControlTest.class));
	}
}
