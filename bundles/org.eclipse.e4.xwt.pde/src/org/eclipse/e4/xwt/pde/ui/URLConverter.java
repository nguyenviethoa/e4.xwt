package org.eclipse.e4.xwt.pde.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.databinding.conversion.IConverter;

public class URLConverter implements IConverter {

	public Object convert(Object fromObject) {
		try {
			return new URL((String) fromObject);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return URL.class;
	}
}
