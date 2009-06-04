package org.eclipse.e4.xwt.tests.jface;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.jface.listviewer.array.JFaceListViewer_ArrayTests;
import org.eclipse.e4.xwt.tests.jface.listviewer.collection.JFaceListViewer_CollectionTests;
import org.eclipse.e4.xwt.tests.jface.tableviewer.JFaceTableViewer_Tests;

public class JFaceTestSuite extends TestSuite {
	public static final Test suite() {
		return new JFaceTestSuite();
	}

	public JFaceTestSuite() {
		addTest(new TestSuite(JFaceTests.class));
		addTest(new TestSuite(JFaceListViewer_ArrayTests.class));
		addTest(new TestSuite(JFaceListViewer_CollectionTests.class));
		addTest(new TestSuite(JFaceTableViewer_Tests.class));
	}
}
