package org.eclipse.e4.xwt.internal.databinding.menuitem;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemSelectionObservableValue extends AbstractMenuItemObservableValue {

	public MenuItemSelectionObservableValue(MenuItem menuItem) {
		super(menuItem);
	}

	public MenuItemSelectionObservableValue(Realm realm, MenuItem menuItem) {
		super(realm, menuItem);
	}

	@Override
	protected Object doGetValue() {
		return getMenuItem().getSelection();
	}

	public Object getValueType() {
		return Boolean.class;
	}
	
	protected void doSetValue(Object value) {
		 getMenuItem().setSelection((Boolean)value);
	}
}
