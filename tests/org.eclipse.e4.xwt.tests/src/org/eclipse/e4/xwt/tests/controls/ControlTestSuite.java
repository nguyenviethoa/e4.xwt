package org.eclipse.e4.xwt.tests.controls;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.button.ButtonTests;
import org.eclipse.e4.xwt.tests.controls.ccombo.CComboTests;
import org.eclipse.e4.xwt.tests.controls.combo.ComboTests;

public class ControlTestSuite extends TestSuite {
	public static final Test suite() {
		return new ControlTestSuite();
	}

	public ControlTestSuite() {
		addTest(new TestSuite(ControlsTests.class));
		addTest(new TestSuite(ButtonTests.class));
		addTest(new TestSuite(ComboTests.class));
		addTest(new TestSuite(CComboTests.class));
	}
}
