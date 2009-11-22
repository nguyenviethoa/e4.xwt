package org.eclipse.e4.xwt.tests.swt;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SWTTestSuite extends TestSuite {
	public static final Test suite() {
		return new SWTTestSuite();
	}

	public SWTTestSuite() {
		addTest(new TestSuite(SWTStyleTests.class));
	}
}
