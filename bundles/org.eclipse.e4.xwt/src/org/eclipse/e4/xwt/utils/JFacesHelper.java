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
package org.eclipse.e4.xwt.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

public class JFacesHelper {

	public static Class[] getSupportedElements() {
		return JFACES_SUPPORTED_ELEMENTS;
	}

	public static boolean isViewer(Object obj) {
		if (JFACES_VIEWER == null)
			return false;
		return JFACES_VIEWER.isAssignableFrom(obj.getClass());
	}

	public static Control getControl(Object obj) {
		if (!isViewer(obj))
			throw new IllegalStateException("Expecting a jfaces viewer:" + obj);
		try {
			Method method = JFACES_VIEWER.getMethod("getControl", null);
			return (Control) method.invoke(obj, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Class[] JFACES_SUPPORTED_ELEMENTS;
	private static Class JFACES_VIEWER;

	static {
		List<Class> collector = new ArrayList<Class>();
		try {
			JFACES_VIEWER = Class.forName("org.eclipse.jface.viewers.Viewer");
			collector.add(Class.forName("org.eclipse.jface.viewers.ListViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TableViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TableTreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.CheckboxTableViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.CheckboxTreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.dialogs.TitleAreaDialog"));

		} catch (ClassNotFoundException e) {
			System.out.println("No JFaces support");
		}
		JFACES_SUPPORTED_ELEMENTS = collector.toArray(new Class[collector.size()]);
	}
}
