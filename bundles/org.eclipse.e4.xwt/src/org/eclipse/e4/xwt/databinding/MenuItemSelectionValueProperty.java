package org.eclipse.e4.xwt.databinding;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemSelectionValueProperty extends WidgetValueProperty {
	protected MenuItemSelectionValueProperty() {
		super();
	}

	protected MenuItemSelectionValueProperty(int event) {
		super(event);
	}

	public Object getValueType() {
		return Boolean.TYPE;
	}

	protected Object doGetValue(Object source) {
		return doGetBooleanValue(source) ? Boolean.TRUE : Boolean.FALSE;
	}

	protected void doSetValue(Object source, Object value) {
		if (value == null)
			value = Boolean.FALSE;
		doSetBooleanValue(source, ((Boolean) value).booleanValue());
	}

	protected boolean doGetBooleanValue(Object source) {
		return ((MenuItem) source).getSelection();
	}

	protected void doSetBooleanValue(Object source, boolean value) {
		((MenuItem) source).setSelection(value);
	}

	public String toString() {
		return "MenuItem.selection <boolean>"; //$NON-NLS-1$
	}
}
