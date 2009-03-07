package org.eclipse.e4.xwt.css;

import org.eclipse.e4.xwt.IStyle;

public class CCSStyle implements IStyle {
	protected String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void applyStyle(Object target) {
	}
}
