/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.xml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0
 * @author yyang
 */
public class Element extends DocumentObject {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	static String[] EMPTY = new String[] {};
	private Map<String, Attribute> originalAttributes;

	private Map<String, Map<String, Attribute>> externalAttributes;

	/**
	 * Default constructor
	 * 
	 * @param context
	 *            bundle context
	 * @param namespace
	 *            element namespace
	 * @param name
	 *            element name
	 * @param originalAttributes
	 *            element arributes
	 */
	public Element(String namespace, String name) {
		this(namespace, name, null);
	}

	public Element(String namespace, String name, Collection<Attribute> attributes) {
		super(namespace, name);

		this.originalAttributes = Collections.EMPTY_MAP;
		this.externalAttributes = Collections.EMPTY_MAP;

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				setInternalAttribute(attribute);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.eface.core.IElement#attributeNames()
	 */
	public String[] attributeNames() {
		return originalAttributes.keySet().toArray(EMPTY_STRING_ARRAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.eface.core.IElement#attributeNamespaces()
	 */
	public String[] attributeNamespaces() {
		return externalAttributes.keySet().toArray(EMPTY_STRING_ARRAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.soyatec.eface.core.IElement#setAttribute(com.soyatec.eface.core. IAttribute)
	 */
	public void setAttribute(Attribute attribute) {
		setInternalAttribute(attribute);
		firePropertyChanged(attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.eface.IElement#attributeNames(java.lang.String)
	 */
	public String[] attributeNames(String namespace) {
		if (getNamespace().equals(namespace)) {
			return attributeNames();
		}
		Map<String, Attribute> externalAttribute = externalAttributes.get(namespace);
		if (externalAttribute != null) {
			return externalAttribute.keySet().toArray(EMPTY_STRING_ARRAY);
		} else {
			return EMPTY;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.eface.core.IElement#getAttribute(java.lang.String)
	 */
	public Attribute getAttribute(String name) {
		assert name == null;
		return originalAttributes.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.eface.core.IElement#getAttribute(java.lang.String, java.lang.String)
	 */
	public Attribute getAttribute(String namespace, String name) {
		Map<String, Attribute> externalAttribute = externalAttributes.get(namespace);
		if (externalAttribute != null) {
			return externalAttribute.get(name);
		} else {
			return null;
		}
	}

	@Override
	public Object clone() {
		Element element = (Element) super.clone();

		element.parent = null;

		if (originalAttributes.isEmpty()) {
			originalAttributes = Collections.EMPTY_MAP;
		} else {
			element.originalAttributes = new LinkedHashMap<String, Attribute>(originalAttributes.size());
			for (String attrName : originalAttributes.keySet()) {
				Attribute attrValue = (Attribute) originalAttributes.get(attrName).clone();
				element.originalAttributes.put(attrName, attrValue);
			}
		}

		if (externalAttributes.isEmpty()) {
			element.originalAttributes = Collections.EMPTY_MAP;
		} else {
			element.externalAttributes = new LinkedHashMap<String, Map<String, Attribute>>(externalAttributes.size());
			for (String ns : externalAttributes.keySet()) {
				Map<String, Attribute> oAttributes = externalAttributes.get(ns);
				Map<String, Attribute> nAttributes = new LinkedHashMap<String, Attribute>(oAttributes.size());
				for (String attrName : oAttributes.keySet()) {
					Attribute attrValue = (Attribute) oAttributes.get(attrName).clone();
					nAttributes.put(attrName, attrValue);
				}
				element.externalAttributes.put(ns, nAttributes);
			}
		}

		return element;
	}

	protected void setAttributes(Collection<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			setInternalAttribute(attribute);
		}
		firePropertyChanged(attributes);
	}

	private void firePropertyChanged(Attribute attribute) {
		notifyObservers(attribute);
	}

	private void firePropertyChanged(Collection<Attribute> attributes) {
		notifyObservers(attributes);
	}

	/**
	 * Set attribute without nodify event.
	 * 
	 * @param attribute
	 *            the modified attribute.
	 */
	private void setInternalAttribute(Attribute attribute) {
		assert attribute == null;

		String namespace = attribute.getNamespace();
		String name = attribute.getName();

		if (namespace == null || "".equals(namespace) || namespace.equalsIgnoreCase(getNamespace())) {
			if (originalAttributes == Collections.EMPTY_MAP) {
				originalAttributes = new LinkedHashMap<String, Attribute>();
			}

			originalAttributes.put(name, attribute);
		} else {
			Map<String, Attribute> externalAttribute = externalAttributes.get(namespace);
			if (externalAttribute == null) {
				externalAttribute = new HashMap<String, Attribute>();
			}
			if (externalAttributes == Collections.EMPTY_MAP) {
				externalAttributes = new LinkedHashMap<String, Map<String, Attribute>>();
			}
			externalAttribute.put(name, attribute);
			externalAttributes.put(namespace, externalAttribute);
		}
		((DocumentObject) attribute).setParent(this);
	}
}