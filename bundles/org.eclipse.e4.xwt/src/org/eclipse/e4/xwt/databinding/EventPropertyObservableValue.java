package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.metadata.IProperty;

public class EventPropertyObservableValue extends XWTObservableValue {
	private IProperty property;

	public EventPropertyObservableValue(Object observed, IProperty property) {
		super(Boolean.class, observed);
		this.property = property;
	}

	@Override
	protected void doSetApprovedValue(Object value) {
		UserData.setData(getObserved(), property.getName(), value);
	}

	protected Object doGetValue() {
		return UserData.getLocalData(getObserved(), property.getName());
	}
}
