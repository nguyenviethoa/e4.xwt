package org.eclipse.e4.xwt.tests.style;

import junit.framework.Test;
import junit.framework.TestSuite;

public class StyleTestSuite extends TestSuite {
	public static final Test suite() {
		return new StyleTestSuite();
	}

	public StyleTestSuite() {
		addTest(new TestSuite(StyleTests.class));
	}
}
