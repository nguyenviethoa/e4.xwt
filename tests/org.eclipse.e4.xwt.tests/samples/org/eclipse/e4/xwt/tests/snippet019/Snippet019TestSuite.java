package org.eclipse.e4.xwt.tests.snippet019;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Snippet019TestSuite extends TestSuite {
	public static final Test suite() {
		return new Snippet019TestSuite();
	}

	public Snippet019TestSuite() {
		addTest(new TestSuite(Snippet019Tests.class));
		addTest(new TestSuite(org.eclipse.e4.xwt.tests.snippet019.set.Snippet019Tests.class));
		addTest(new TestSuite(org.eclipse.e4.xwt.tests.snippet019.array.Snippet019Tests.class));
	}
}
