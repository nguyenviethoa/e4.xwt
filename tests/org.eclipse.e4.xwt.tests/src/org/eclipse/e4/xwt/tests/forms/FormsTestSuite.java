package org.eclipse.e4.xwt.tests.forms;

import org.eclipse.e4.xwt.tests.forms.tableviewer.master.detail.set.Forms_TableViewer_Set_MasterDetail_Tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FormsTestSuite extends TestSuite {
	public static final Test suite() {
		return new FormsTestSuite();
	}

	public FormsTestSuite() {
		addTest(new TestSuite(FormsTests.class));
		addTest(new TestSuite(Forms_TableViewer_Set_MasterDetail_Tests.class));
	}
}
