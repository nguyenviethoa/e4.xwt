package org.eclipse.e4.xwt.tests.databinding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.databinding.bindcontrol.ControlBindingInitTests;

public class BindingTestSuite extends TestSuite {
	public static final Test suite() {
		return new BindingTestSuite();
	}

	public BindingTestSuite() {
		addTest(new TestSuite(ControlBindingInitTests.class));
	}
}
