package org.eclipse.e4.xwt.tests.attachedproperty;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AttachedPropertySuite extends TestSuite {
	public static final Test suite() {
		return new AttachedPropertySuite();
	}

	public AttachedPropertySuite() {
		addTest(new TestSuite(AttachedPropertyTests.class));
	}
}
