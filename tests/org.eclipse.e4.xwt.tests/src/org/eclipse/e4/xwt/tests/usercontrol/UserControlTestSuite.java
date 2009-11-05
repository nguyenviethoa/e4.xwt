package org.eclipse.e4.xwt.tests.usercontrol;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UserControlTestSuite extends TestSuite {
	public static final Test suite() {
		return new UserControlTestSuite();
	}

	public UserControlTestSuite() {
		addTest(new TestSuite(UserControlTests.class));
	}
}
