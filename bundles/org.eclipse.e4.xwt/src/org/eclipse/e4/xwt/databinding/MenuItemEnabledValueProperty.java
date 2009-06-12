package org.eclipse.e4.xwt.databinding;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemEnabledValueProperty extends WidgetValueProperty {
	protected MenuItemEnabledValueProperty() {
		super();
	}

	protected MenuItemEnabledValueProperty(int event) {
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
		return ((MenuItem) source).getEnabled();
	}

	protected void doSetBooleanValue(Object source, boolean value) {
		((MenuItem) source).setEnabled(value);
	}

	public String toString() {
		return "MenuItem.enabled <boolean>"; //$NON-NLS-1$
	}
}
