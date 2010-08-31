package org.eclipse.e4.xwt.internal.core;

import java.net.URL;

import org.eclipse.e4.xwt.IUIPattern;
import org.eclipse.e4.xwt.internal.xml.Element;

public class UIPattern implements IUIPattern {
	protected Element content;
	protected URL url;
	
	public UIPattern(URL url, Element content) {
		this.content = content;
		this.url = url;
	}

	public Element getContent() {
		return content;
	}

	public URL getURL() {
		return url;
	}
}
