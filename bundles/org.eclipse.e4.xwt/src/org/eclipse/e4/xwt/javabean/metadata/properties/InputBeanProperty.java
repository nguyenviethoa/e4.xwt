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
package org.eclipse.e4.xwt.javabean.metadata.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.collection.CollectionViewSource;
import org.eclipse.e4.xwt.jface.DefaultColumnViewerLabelProvider;
import org.eclipse.e4.xwt.jface.DefaultListViewerLabelProvider;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.jface.ObservableMapLabelProvider;
import org.eclipse.e4.xwt.metadata.DelegateProperty;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;

/**
 * Handle manually the type conversion. Maybe it can be done using the
 * IConverter. Only the type of IProperty should be IObservableCollection
 * 
 * @author yyang
 * 
 */
public class InputBeanProperty extends DelegateProperty {

	public InputBeanProperty(IProperty delegate) {
		super(delegate);
	}

	@Override
	public void setValue(Object target, Object value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchFieldException {
		if (value == null) {
			return;
		}
		Class<?> elementType = getElementType();
		if (value.getClass().isArray()) {
			elementType = value.getClass().getComponentType();
		}
		if (value instanceof IObservableList) {
			IObservableList list = (IObservableList) value;
			Object listElementType = list.getElementType();
			if (listElementType instanceof Class<?>) {
				elementType = (Class<?>) listElementType;
			}
		} else if (elementType == Object.class
				&& value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) value;
			for (Iterator<?> iterator = collection.iterator(); iterator
					.hasNext();) {
				Object object = (Object) iterator.next();
				if (object != null) {
					elementType = object.getClass();
					break;
				}
			}
		}

		if (target instanceof ContentViewer) {
			ContentViewer viewer = (ContentViewer) target;
			IContentProvider contentProvider = viewer.getContentProvider();

			String[] propertyNames = JFacesHelper.getViewerProperties(viewer);
			if (value instanceof List<?> || value.getClass().isArray()) {
				if (contentProvider == null) {
					contentProvider = new ObservableListContentProvider();
					viewer.setContentProvider(contentProvider);
				}
				if (propertyNames != null && propertyNames.length > 0 && hasDefaultLabelProvider(viewer) 
						&& contentProvider instanceof ObservableListContentProvider) {
					ObservableListContentProvider listContentProvider = (ObservableListContentProvider) contentProvider;
					viewer.setLabelProvider(new ObservableMapLabelProvider(
							viewer, listContentProvider.getKnownElements(),
							propertyNames));
				}
			} else if (value instanceof Set<?>) {
				if (contentProvider == null) {
					contentProvider = new ObservableSetContentProvider();
					viewer.setContentProvider(contentProvider);
				}
				if (propertyNames != null && propertyNames.length > 0 && hasDefaultLabelProvider(viewer)
						&& contentProvider instanceof ObservableSetContentProvider) {
					ObservableSetContentProvider setContentProvider = (ObservableSetContentProvider) contentProvider;
					viewer.setLabelProvider(new ObservableMapLabelProvider(
							viewer, setContentProvider.getKnownElements(), propertyNames));
				}
			}
		}
		if (value instanceof CollectionViewSource) {
			value = ((CollectionViewSource) value).getView();
		} else if (!(value instanceof IObservableCollection)) {
			IConverter converter = XWT.findConvertor(value.getClass(),
					IObservableCollection.class);
			if (converter != null) {
				value = converter.convert(value);
			}
		}
		super.setValue(target, value);
	}
	
	protected boolean hasDefaultLabelProvider(ContentViewer viewer ) {
		IBaseLabelProvider labelProvider = viewer.getLabelProvider();
		return (labelProvider == null || labelProvider.getClass() == DefaultColumnViewerLabelProvider.class ||
				labelProvider.getClass() == DefaultListViewerLabelProvider.class);
	}

	protected Class<?> getElementType() {
		IProperty property = getDelegate();
		Class<?> type = property.getType();
		if (type == null) {
			return Object.class;
		}
		if (type.isArray()) {
			return type.getComponentType();
		}
		return Object.class;
	}
}
