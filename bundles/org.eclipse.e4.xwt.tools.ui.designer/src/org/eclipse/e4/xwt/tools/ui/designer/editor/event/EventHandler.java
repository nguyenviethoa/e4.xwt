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
package org.eclipse.e4.xwt.tools.ui.designer.editor.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.editor.XWTDesigner;
import org.eclipse.e4.xwt.tools.ui.designer.jdt.ASTHelper;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Event;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class EventHandler {

	private XWTDesigner designer;
	private IType type;

	public EventHandler(XWTDesigner designer, IType type) {
		this.designer = designer;
		this.type = type;
		if (type == null) {
			throw new NullPointerException("Java Source Type is Null!");
		}
	}

	public boolean exist(String handlerName) {
		try {
			IMethod[] methods = type.getMethods();
			if (findMethod(methods, handlerName) != null) {
				return true;
			}
		} catch (JavaModelException e) {
		}
		return false;
	}

	public String suggestDefaultName(String value) {
		int i = 0;
		String name = value;
		while (exist(name)) {
			name = value + (++i);
		}
		return name;
	}
	
	private IMethod findMethod(IMethod[] methods, String methodName) {
		for (int i = 0; i < methods.length; i++) {
			IMethod method = methods[i];
			if (method.getElementName().equals(methodName))
				return method;
		}
		return null;
	}

	public void createHandler(String handlerName) {
		if (handlerName == null) {
			return;
		}
		List<Class<?>> argus = new ArrayList<Class<?>>();
		argus.add(Object.class);
		argus.add(Event.class);
		createHandler(handlerName, null, null, argus);
	}

	public void createHandler(String methodName, Class<?> returnType, String contentReturnValue, List<Class<?>> arguments) {
		ASTHelper.createMethod(type, methodName, returnType, contentReturnValue, arguments);
	}

	/**
	 * Create all handlers.
	 */
	public void createHandlers() {
		XamlDocument xamlDocument = designer.getXamlDocument();
		XamlElement rootElement = xamlDocument.getRootElement();
		List<XamlAttribute> handlerAttrs = new ArrayList<XamlAttribute>();
		retrieveHandlerAttrs(rootElement, handlerAttrs);
		if (handlerAttrs.isEmpty()) {
			return;
		}
		for (XamlAttribute xamlAttribute : handlerAttrs) {
			String handlerName = xamlAttribute.getValue();
			createHandler(handlerName);
		}
	}

	private void retrieveHandlerAttrs(XamlElement element, List<XamlAttribute> handlers) {
		EList<String> attributeNames = element.attributeNames();
		for (String attrName : attributeNames) {
			if (attrName.endsWith("Event")) {
				XamlAttribute attribute = element.getAttribute(attrName);
				if (attribute != null && attribute.getValue() != null) {
					handlers.add(attribute);
				}
			}
		}
		for (XamlElement child : element.getChildNodes()) {
			retrieveHandlerAttrs(child, handlers);
		}
	}
}
