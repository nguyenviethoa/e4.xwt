package org.eclipse.e4.xwt.tests.controls;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.button.ButtonTests;

public class ControlTestSuite extends TestSuite {
	public static final Test suite() {
		return new ControlTestSuite();
	}

	public ControlTestSuite() {
		addTest(new TestSuite(ControlsTests.class));
		addTest(new TestSuite(ButtonTests.class));
	}
}
