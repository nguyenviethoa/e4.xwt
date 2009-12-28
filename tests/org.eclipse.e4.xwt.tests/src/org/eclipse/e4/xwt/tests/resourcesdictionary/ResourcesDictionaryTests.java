package org.eclipse.e4.xwt.tests.resourcesdictionary;

import java.net.URL;
import java.util.Map;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.tests.XWTTestCase;

public class ResourcesDictionaryTests extends XWTTestCase {

	public void testFindResource() throws Exception {
		URL url = ResourcesDictionaryTests.class.getResource(Container.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Object element1 = XWT.findResource(root, "myDBC1");
				assertTrue(element1 instanceof MyData);
				Object element2 = XWT.findResource(root, "myDBC2");
				assertTrue(element2 instanceof MyData);
			}
		});
	}

	public void testGetResources() throws Exception {
		URL url = ResourcesDictionaryTests.class.getResource(Container.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		runTest(url, new Runnable() {
			public void run() {
				Map<String, Object> dictionary = XWT.getResources(root);
				assertEquals(2, dictionary.size());
			}
		});
	}
}
