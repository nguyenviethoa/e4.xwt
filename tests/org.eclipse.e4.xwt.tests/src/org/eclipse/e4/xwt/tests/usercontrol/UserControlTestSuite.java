package org.eclipse.e4.xwt.tests.usercontrol;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.button.ButtonTests;

public class UserControlTestSuite extends TestSuite {
	public static final Test suite() {
		return new UserControlTestSuite();
	}

	public UserControlTestSuite() {
		addTest(new TestSuite(UserControlTests.class));
	}
}
