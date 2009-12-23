package org.eclipse.e4.xwt.tests.jface;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.jface.comboviewer.array.JFaceComboViewer_ArrayTests;
import org.eclipse.e4.xwt.tests.jface.comboviewer.collection.JFaceComboViewer_CollectionTests;
import org.eclipse.e4.xwt.tests.jface.listviewer.array.JFaceListViewer_ArrayTests;
import org.eclipse.e4.xwt.tests.jface.listviewer.collection.JFaceListViewer_CollectionTests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.JFaceTableViewer_Tests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.filter.JFaceTableViewer_Filters_Tests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.JFaceTableViewer_MasterDetail_Tests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.array.JFaceTableViewer_Array_MasterDetail_Tests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.list.JFaceTableViewer_List_MasterDetail_Tests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.master.detail.set.JFaceTableViewer_Set_MasterDetail_Tests;

public class JFaceTestSuite extends TestSuite {
	public static final Test suite() {
		return new JFaceTestSuite();
	}

	public JFaceTestSuite() {
		addTest(new TestSuite(JFaceTests.class));
		addTest(new TestSuite(JFaceListViewer_ArrayTests.class));
		addTest(new TestSuite(JFaceListViewer_CollectionTests.class));
		addTest(new TestSuite(JFaceTableViewer_Tests.class));
		addTest(new TestSuite(JFaceTableViewer_Filters_Tests.class));
		addTest(new TestSuite(JFaceTableViewer_MasterDetail_Tests.class));
		addTest(new TestSuite(JFaceTableViewer_List_MasterDetail_Tests.class));
		addTest(new TestSuite(JFaceTableViewer_Set_MasterDetail_Tests.class));
		addTest(new TestSuite(JFaceTableViewer_Array_MasterDetail_Tests.class));
		addTest(new TestSuite(JFaceComboViewer_ArrayTests.class));
		addTest(new TestSuite(JFaceComboViewer_CollectionTests.class));	
	}
}
