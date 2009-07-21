package org.eclipse.e4.xwt.tests.clr;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XWTTestSuite extends TestSuite {
	public static final Test suite() {
		return new XWTTestSuite();
	}

	public XWTTestSuite() {
		addTest(new TestSuite(CLRTests.class));
	}
}
