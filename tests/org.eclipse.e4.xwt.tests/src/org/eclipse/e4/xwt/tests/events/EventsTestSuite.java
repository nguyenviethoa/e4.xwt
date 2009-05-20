package org.eclipse.e4.xwt.tests.events;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.events.initialize.EventsInitTests;
import org.eclipse.e4.xwt.tests.events.loaded.EventsLoadedTests;

public class EventsTestSuite extends TestSuite {
	public static final Test suite() {
		return new EventsTestSuite();
	}

	public EventsTestSuite() {
		addTest(new TestSuite(EventsTests.class));
		addTest(new TestSuite(EventsInitTests.class));
		addTest(new TestSuite(EventsLoadedTests.class));
	}
}
