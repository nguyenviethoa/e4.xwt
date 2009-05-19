package org.eclipse.e4.xwt.tests.databinding.bindcontrol;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;

public class ControlBinding {

	public static void main(String[] args) {
		URL url = ControlBinding.class.getResource(ControlBinding.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
