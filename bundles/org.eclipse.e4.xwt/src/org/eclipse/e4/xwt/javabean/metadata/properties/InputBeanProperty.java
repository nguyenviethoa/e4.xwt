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
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.collection.CollectionViewSource;
import org.eclipse.e4.xwt.jface.DefaultColumnViewerLabelProvider;
import org.eclipse.e4.xwt.jface.DefaultListViewerLabelProvider;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.DelegateProperty;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ColumnViewer;
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
		Class<?> elementType = getElementType();
		if (value.getClass().isArray()) {
			elementType = value.getClass().getComponentType();
		}
		if (value instanceof IObservableList) {
			IObservableList list = (IObservableList) value;
			Object listElementType = list.getElementType();
			if (listElementType instanceof Class<?>){
				elementType = (Class<?>) listElementType;
			}
		}
		
		if (target instanceof AbstractListViewer) {
			AbstractListViewer viewer = (AbstractListViewer) target;
			IContentProvider contentProvider = viewer.getContentProvider();
			IBaseLabelProvider labelProvider = viewer.getLabelProvider();

			if (contentProvider == null) {
				if (value instanceof List<?> || value.getClass().isArray()) {
						contentProvider = new ObservableListContentProvider();
						viewer.setContentProvider(contentProvider);
				} else if (value instanceof Set<?>) {
						contentProvider = new ObservableSetContentProvider();
						viewer.setContentProvider(contentProvider);
				}
			}
			if (labelProvider == null) {
				viewer.setLabelProvider(new DefaultListViewerLabelProvider(viewer));					
			}
		} else if (target instanceof ColumnViewer) {
			ColumnViewer viewer = (ColumnViewer) target;
			IContentProvider contentProvider = viewer.getContentProvider();
			IBaseLabelProvider labelProvider = viewer.getLabelProvider();

			String[] propertyNames = JFacesHelper.getViewerProperties(viewer);
			if (value instanceof List<?> || value.getClass().isArray()) {
				if (contentProvider == null) {
					contentProvider = new ObservableListContentProvider();
					viewer.setContentProvider(contentProvider);
				}
				if (propertyNames != null
						&& contentProvider instanceof ObservableListContentProvider) {
					ObservableListContentProvider listContentProvider = (ObservableListContentProvider) contentProvider;
					viewer.setLabelProvider(new ObservableMapLabelProvider(
							PojoObservables.observeMaps(listContentProvider
									.getKnownElements(), elementType,
									propertyNames)));
				}
			} else if (value instanceof Set<?>) {
				if (contentProvider == null) {
					contentProvider = new ObservableSetContentProvider();
					viewer.setContentProvider(contentProvider);
				}
				if (propertyNames != null
						&& contentProvider instanceof ObservableSetContentProvider) {
					ObservableSetContentProvider setContentProvider = (ObservableSetContentProvider) contentProvider;
					viewer.setLabelProvider(new ObservableMapLabelProvider(
							PojoObservables.observeMaps(setContentProvider
									.getKnownElements(), elementType,
									propertyNames)));
				}
			}
			if (labelProvider == null) {
				viewer.setLabelProvider(new DefaultColumnViewerLabelProvider(viewer));					
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
