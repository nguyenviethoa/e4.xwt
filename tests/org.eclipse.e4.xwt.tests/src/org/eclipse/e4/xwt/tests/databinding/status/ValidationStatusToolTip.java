package org.eclipse.e4.xwt.tests.databinding.status;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

public class ValidationStatusToolTip extends Composite {
	public static void main(String[] args) {

		URL url = ValidationStatusToolTip.class.getResource(ValidationStatusToolTip.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ValidationStatusToolTip(Composite parent, int style) {
		super(parent, style);
	}

}
