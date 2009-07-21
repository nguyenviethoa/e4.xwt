package org.eclipse.e4.xwt.tests.name;

import junit.framework.Test;
import junit.framework.TestSuite;

public class NameTestSuite extends TestSuite {
	public static final Test suite() {
		return new NameTestSuite();
	}

	public NameTestSuite() {
		addTest(new TestSuite(NameTests.class));
	}
}
