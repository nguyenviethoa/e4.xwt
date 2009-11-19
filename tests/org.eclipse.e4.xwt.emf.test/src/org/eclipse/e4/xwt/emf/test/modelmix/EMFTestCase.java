package org.eclipse.e4.xwt.emf.test.modelmix;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.emf.test.XWTTestCase;

public class EMFTestCase extends XWTTestCase {
	public void testEMFDataProvider_DataContext() {
		URL url = EMFTestCase.class
				.getResource(POJO_EMF_DataContext.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, POJO_EMF_DataContext.createAuthor(), null,
				new Runnable() {
					public void run() {
						assertText("titleText", "Harry Potter");
						assertText("authorText", "Neal Stephenson");
					}
				});
	}

	public void testEMFDataProvider_Path() {
		URL url = EMFTestCase.class
				.getResource(POJO_EMF_Path.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, POJO_EMF_Path.createAuthor(), null,
				new Runnable() {
					public void run() {
						assertText("titleText", "Harry Potter");
						assertText("authorText", "Neal Stephenson");
					}
				});
	}
}
