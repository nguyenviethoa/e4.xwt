package org.eclipse.e4.xwt.tests.style;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class UserControl_Default_x {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = UserControl_Default_x.class.getResource(UserControl_Default_x.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
