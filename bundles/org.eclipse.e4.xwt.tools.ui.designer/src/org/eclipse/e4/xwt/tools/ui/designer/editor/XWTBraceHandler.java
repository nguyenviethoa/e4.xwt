/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.designer.editor;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.BraceHandler;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlFactory;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.common.util.EMap;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class XWTBraceHandler extends BraceHandler {

	public XWTBraceHandler(XamlDocument document) {
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.BraceHandler#expendNamespaces(org.eclipse.e4.xwt.tools.ui.xaml.XamlNode, java.lang.String)
	 */
	protected String expendNamespaces(XamlNode element, String value) {
		if (value.indexOf(':') == -1) {
			return value;
		}
		EMap<String, String> declaredNamespaces = getDocument().getDeclaredNamespaces();
		int length = IConstants.XAML_CLR_NAMESPACE_PROTO.length();
		for (String prefix : declaredNamespaces.keySet()) {
			String namespace = declaredNamespaces.get(prefix);
			if (namespace.startsWith(IConstants.XAML_CLR_NAMESPACE_PROTO)) {
				String packageName = namespace.substring(length);
				value = value.replace(prefix + ":", packageName + '.');
			}
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.BraceHandler#handleContent(org.eclipse.e4.xwt.tools.ui.xaml.XamlNode, java.lang.String)
	 */
	protected void handleContent(XamlNode element, String text) {
		if (text.startsWith("{") && text.endsWith("}")) {
			parse(element, text);
			return;
		} else {
			// handle the case: <x:Array Type="ns:Type" >
			if (IConstants.XAML_X_TYPE.equals(element.getName()) || IConstants.XAML_X_STATIC.equals(element.getName())) {
				int index = text.indexOf(':');
				if (index != -1) {
					String ns = text.substring(0, index);
					String content = text.substring(index + 1);
					String namespace = getDocument().getDeclaredNamespace(ns);
					if (namespace != null) {
						XamlElement childElement = element.getChild(content, namespace);
						if (childElement == null) {
							childElement = XamlFactory.eINSTANCE.createElement(content, namespace);
							childElement.getChildNodes().add(childElement);
						}
						return;
					}
				}
			}
		}
		if (element instanceof Attribute && IConstants.XWT_X_NAMESPACE.equals(element.getNamespace()) && IConstants.XAML_STYLE.equalsIgnoreCase(element.getName())) {
			// handle the expansion of x:Style = "(j:class).variable"
			text = expendNamespaces(element, text);
		}
		element.setValue(text);
	}
}
