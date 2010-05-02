package org.eclipse.e4.xwt.tests.controls.layout;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LayoutTestSuite extends TestSuite {
	public static final Test suite() {
		return new LayoutTestSuite();
	}

	public LayoutTestSuite() {
		addTest(new TestSuite(LayoutTests.class));
	}
}
