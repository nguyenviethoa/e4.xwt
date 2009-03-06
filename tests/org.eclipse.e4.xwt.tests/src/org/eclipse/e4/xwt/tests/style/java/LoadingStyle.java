package org.eclipse.e4.xwt.tests.style.java;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

public class LoadingStyle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		URL url = LoadingStyle.class.getResource("Style" + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(XWT.DEFAULT_STYLES_PROPERTY, new GreenStyle());
			XWT.open(url, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
