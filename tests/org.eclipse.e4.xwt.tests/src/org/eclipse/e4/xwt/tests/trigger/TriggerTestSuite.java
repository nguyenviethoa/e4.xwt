/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.trigger;

import org.eclipse.e4.xwt.tests.trigger.datatrigger.DataTriggerTests;
import org.eclipse.e4.xwt.tests.trigger.multidatatrigger.MultiDataTriggerTests;
import org.eclipse.e4.xwt.tests.trigger.multitrigger.MultiTriggerTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TriggerTestSuite extends TestSuite {
	public static final Test suite() {
		return new TriggerTestSuite();
	}

	public TriggerTestSuite() {
		addTest(new TestSuite(TriggerTests.class));
		addTest(new TestSuite(MultiTriggerTests.class));
		addTest(new TestSuite(DataTriggerTests.class));
		addTest(new TestSuite(MultiDataTriggerTests.class));
	}
}
