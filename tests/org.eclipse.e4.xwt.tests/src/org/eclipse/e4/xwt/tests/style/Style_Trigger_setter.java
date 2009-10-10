package org.eclipse.e4.xwt.tests.style;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class Style_Trigger_setter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL url = Style_Trigger_setter.class.getResource(Style_Trigger_setter.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void StyleTriggerSetterTest() {
		
	}
}
