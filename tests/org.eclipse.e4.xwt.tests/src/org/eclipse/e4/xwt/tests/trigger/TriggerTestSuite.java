package org.eclipse.e4.xwt.tests.trigger;

import org.eclipse.e4.xwt.tests.trigger.datatrigger.DataTriggerTestCase;
import org.eclipse.e4.xwt.tests.trigger.multidatatrigger.MultiDataTriggerTestCase;
import org.eclipse.e4.xwt.tests.trigger.multitrigger.MultiTriggerTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TriggerTestSuite extends TestSuite {
	public static final Test suite() {
		return new TriggerTestSuite();
	}

	public TriggerTestSuite() {
		addTest(new TestSuite(TriggerTests.class));
		addTest(new TestSuite(MultiTriggerTestCase.class));
		addTest(new TestSuite(DataTriggerTestCase.class));
		addTest(new TestSuite(MultiDataTriggerTestCase.class));
	}
}
