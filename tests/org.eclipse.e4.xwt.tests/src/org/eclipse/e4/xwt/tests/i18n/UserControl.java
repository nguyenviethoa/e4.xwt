package org.eclipse.e4.xwt.tests.i18n;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class UserControl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = UserControl.class.getResource(UserControl.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
