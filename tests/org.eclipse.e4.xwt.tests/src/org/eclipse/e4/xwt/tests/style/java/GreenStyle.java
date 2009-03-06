package org.eclipse.e4.xwt.tests.style.java;

import org.eclipse.e4.xwt.IStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public class GreenStyle implements IStyle {

	public void applyStyle(Object target) {
		if (target instanceof Control) {
			Control control = (Control) target;
			control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		}
	}
}
