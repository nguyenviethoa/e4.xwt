package org.eclipse.e4.xwt.tests.resources;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ResourcesTestSuite extends TestSuite {
	public static final Test suite() {
		return new ResourcesTestSuite();
	}

	public ResourcesTestSuite() {
		addTest(new TestSuite(ResourcesTests.class));
	}
}
