package org.eclipse.e4.xwt.tests.databinding;

import org.eclipse.e4.xwt.IValueConverter;

public class MyConverter implements IValueConverter {

	public Object convertBack(Object value) {
		throw new UnsupportedOperationException();
	}

	public Object convert(Object fromObject) {
		return "-> " + fromObject;
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return String.class;
	}

}
