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
package org.eclipse.e4.xwt.jface;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class DefaultViewerLabelProvider implements ITableLabelProvider, ILabelProvider {
	protected Viewer viewer;

	public DefaultViewerLabelProvider(Viewer viewer) {
		this.viewer = viewer;
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

	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}
	
	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public String getColumnText(Object element, int columnIndex) {
		Object[] properties = getPaths();
		if (properties == null) {
			throw new XWTException("displayPath is missing in TableViewerColumn or TableViewer.columnProperties is missing.");
		}
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
	
	protected abstract Object[] getPaths();
}
