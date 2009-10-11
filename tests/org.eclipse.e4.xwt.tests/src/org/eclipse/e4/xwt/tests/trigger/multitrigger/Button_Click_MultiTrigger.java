package org.eclipse.e4.xwt.tests.trigger.multitrigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;


public class Button_Click_MultiTrigger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = Button_Click_MultiTrigger.class.getResource(Button_Click_MultiTrigger.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
