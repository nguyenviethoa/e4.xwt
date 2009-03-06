package org.eclipse.e4.xwt.tests.style.java;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class JavaInlineStyle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = JavaInlineStyle.class.getResource(JavaInlineStyle.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
