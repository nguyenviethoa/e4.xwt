package org.eclipse.e4.xwt.tests.trigger;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

public class Button_Click_Trigger_SourceName {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		URL url = Button_Click_Trigger_SourceName.class
				.getResource(Button_Click_Trigger_SourceName.class
						.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
