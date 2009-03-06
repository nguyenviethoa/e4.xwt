package org.eclipse.e4.xwt.tests.style.java;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

public class JavaGlobalStyle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		URL url = JavaGlobalStyle.class.getResource("Style" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.addDefaultStyle(new GreenStyle());
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
