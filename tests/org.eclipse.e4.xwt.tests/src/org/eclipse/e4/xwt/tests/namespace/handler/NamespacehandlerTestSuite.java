package org.eclipse.e4.xwt.tests.namespace.handler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class NamespacehandlerTestSuite extends TestSuite {
	public static final Test suite() {
		return new NamespacehandlerTestSuite();
	}

	public NamespacehandlerTestSuite() {
		addTest(new TestSuite(NamespaceHandlerTests.class));
	}
}
