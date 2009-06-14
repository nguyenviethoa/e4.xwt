package org.eclipse.e4.xwt.internal.databinding.menuitem;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemEnabledObservableValue extends AbstractMenuItemObservableValue {

	public MenuItemEnabledObservableValue(MenuItem menuItem) {
		super(menuItem);
	}

	public MenuItemEnabledObservableValue(Realm realm, MenuItem menuItem) {
		super(realm, menuItem);
	}

	@Override
	protected Object doGetValue() {
		return getMenuItem().getEnabled();
	}

	public Object getValueType() {
		return Boolean.class;
	}
	
	protected void doSetValue(Object value) {
		 getMenuItem().setEnabled((Boolean)value);
	}
}
