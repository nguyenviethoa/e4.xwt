package org.eclipse.e4.xwt.tests.controls.button;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ButtonTestSuite extends TestSuite {
	public static final Test suite() {
		return new ButtonTestSuite();
	}

	public ButtonTestSuite() {
		addTest(new TestSuite(ButtonTests.class));
	}
}
