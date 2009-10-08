package org.eclipse.e4.xwt.tests.style;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class Style_EventTrigger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = Style_EventTrigger.class.getResource(Style_EventTrigger.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
