package org.eclipse.e4.xwt.emf.test.modelmix;

import org.eclipse.emf.ecore.EObject;

public class Author {
	private String name;
	private EObject content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EObject getContent() {
		return content;
	}

	public void setContent(EObject content) {
		this.content = content;
	}
}
