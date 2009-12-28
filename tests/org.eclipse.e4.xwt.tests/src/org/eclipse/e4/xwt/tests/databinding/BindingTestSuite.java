package org.eclipse.e4.xwt.tests.databinding;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.databinding.bindcontrol.ControlBindingTests;
import org.eclipse.e4.xwt.tests.databinding.multibinding.MultiBindingTests;
import org.eclipse.e4.xwt.tests.databinding.pojo.PojoDataBindingTests;
import org.eclipse.e4.xwt.tests.databinding.self.DataBindingSelfTests;
import org.eclipse.e4.xwt.tests.databinding.status.ValidationStatusTests;
import org.eclipse.e4.xwt.tests.databinding.validation.ValidationsTests;

public class BindingTestSuite extends TestSuite {
	public static final Test suite() {
		return new BindingTestSuite();
	}

	public BindingTestSuite() {
		addTest(new TestSuite(DataBindingTests.class));
		addTest(new TestSuite(PojoDataBindingTests.class));
		addTest(new TestSuite(ControlBindingTests.class));
		addTest(new TestSuite(MultiBindingTests.class));
		addTest(new TestSuite(DataBindingSelfTests.class));
		addTest(new TestSuite(ValidationsTests.class));
		addTest(new TestSuite(ValidationStatusTests.class));
	}
}
