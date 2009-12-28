package org.eclipse.e4.xwt.tests.resourcesdictionary;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ResourcesDictionaryTestSuite extends TestSuite {
	public static final Test suite() {
		return new ResourcesDictionaryTestSuite();
	}

	public ResourcesDictionaryTestSuite() {
		addTest(new TestSuite(ResourcesDictionaryTests.class));
	}
}
