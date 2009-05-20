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
package org.eclipse.e4.xwt.internal.jface;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;

public class DefaultLabelProvider implements ITableLabelProvider {
	protected TableViewer tableViewer;

	public DefaultLabelProvider(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Object[] properties = tableViewer.getColumnProperties();
		String propertyName = properties[columnIndex].toString();
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(element.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				if (propertyDescriptor.getName().equalsIgnoreCase(propertyName)) {
					Object value = propertyDescriptor.getReadMethod().invoke(element);
					if (value != null) {
						Class<?> type = value.getClass();
						if (type != String.class) {
							IConverter converter = XWT.findConvertor(type, String.class);
							if (converter != null) {
								value = converter.convert(value);
							}
						}
						return value.toString();
					}
					return null;
				}
			}
		} catch (Exception e) {
			throw new XWTException(e);
		}

		return "";
	}
}
