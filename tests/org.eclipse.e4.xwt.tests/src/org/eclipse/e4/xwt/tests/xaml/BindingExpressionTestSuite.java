package org.eclipse.e4.xwt.tests.xaml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BindingExpressionTestSuite extends TestSuite {
	public static final Test suite() {
		return new BindingExpressionTestSuite();
	}

	public BindingExpressionTestSuite() {
		addTest(new TestSuite(BindingExpressionTests.class));
	}
}
