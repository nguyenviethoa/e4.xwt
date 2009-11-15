package org.eclipse.e4.xwt.tests.snippet017;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.button.ButtonTests;
import org.eclipse.e4.xwt.tests.controls.ccombo.CComboTests;
import org.eclipse.e4.xwt.tests.controls.combo.ComboTests;

public class Snippet017TestSuite extends TestSuite {
	public static final Test suite() {
		return new Snippet017TestSuite();
	}

	public Snippet017TestSuite() {
		addTest(new TestSuite(Snippet017Tests.class));
		addTest(new TestSuite(org.eclipse.e4.xwt.tests.snippet017.inner.Snippet017Tests.class));
		addTest(new TestSuite(org.eclipse.e4.xwt.tests.snippet017.pojo.Snippet017Tests.class));
	}
}
