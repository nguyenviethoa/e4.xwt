package org.eclipse.e4.xwt.tests.databinding.dataprovider.custom;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;

public class MyValueProperty extends ValueProperty {

	public MyValueProperty() {
	}

	public Object getValueType() {
		return null;
	}

	public IObservableValue observe(Realm realm, Object source) {
		return null;
	}

	public IObservableList observeDetail(IObservableList master) {
		return null;
	}

	public IObservableMap observeDetail(IObservableSet master) {
		return null;
	}

	public IObservableMap observeDetail(IObservableMap master) {
		return null;
	}

}
