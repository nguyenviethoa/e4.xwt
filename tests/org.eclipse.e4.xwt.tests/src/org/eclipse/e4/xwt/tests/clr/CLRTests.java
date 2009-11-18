package org.eclipse.e4.xwt.tests.clr;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;

public class CLRTests extends XWTTestCase {

	public void testCLR() throws Exception {
		URL url = CLR.class.getResource(CLR.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.getCLR(root);
				assertTrue(element instanceof CLR);
			}
		});
	}

	public void testCLRComposite() throws Exception {
		URL url = CLR.class.getResource(CLRComposite.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element = XWT.getCLR(root);
				assertTrue(element instanceof CLRComposite);
			}
		});
	}

	public void testCLRNull() throws Exception {
		URL url = CLR.class.getResource(CLRNull.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		HashMap<String, Object> options = new HashMap<String, Object>();
		final CLRNull clr = new CLRNull();
		options.put(IXWTLoader.CLASS_PROPERTY, clr);
		runTest(url, options, null, new Runnable() {
			public void run() {
				Object element = XWT.getCLR(root);
				assertTrue(element == clr);
				assertEquals(clr.getCount(), 1);
			}
		});
	}
}
