package org.eclipse.e4.xwt.tests.namespace.handler;

import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.swt.widgets.Widget;

public class ExtHandler implements INamespaceHandler {
	public void handleAttribute(Widget widget, Object target, String name,
			String value) {
		widget.setData(name, value);
	}
}
