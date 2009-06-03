package org.eclipse.e4.xwt.css;

import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.swt.widgets.Widget;

public class CSSHandler implements INamespaceHandler {
	public static final String NAMESPACE = "http://www.eclipse.org/css";
	public static final CSSHandler handler = new CSSHandler();

	public void handleAttribute(Widget widget, Object target, String name, String value) {
		widget.setData("org.eclipse.e4.ui.css." + name, value);
	}
}
