package org.eclipse.e4.xwt.tests.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.e4.xwt.tests.i18n.messages"; // NON-NLS-1

	public static String title;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
