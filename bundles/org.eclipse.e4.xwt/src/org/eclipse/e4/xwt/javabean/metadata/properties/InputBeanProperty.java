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
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.collection.CollectionViewSource;
import org.eclipse.e4.xwt.metadata.DelegateProperty;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IContentProvider;

/**
 * Handle manually the type conversion. Maybe it can be done using the IConverter. Only the type of IProperty should be IObservableCollection
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
		if (value instanceof CollectionViewSource) {
			value = ((CollectionViewSource) value).getView();
		}
		else if (!(value instanceof IObservableCollection)) {
			if (target instanceof AbstractListViewer){				
				AbstractListViewer viewer = (AbstractListViewer) target;
				
				if (!isArrayProperty()) {	
					if (value instanceof List<?>) {
						IContentProvider contentProvider = viewer.getContentProvider();
						if (contentProvider == null) {
							contentProvider = new ObservableListContentProvider();
							viewer.setContentProvider(contentProvider);
						}
						value = new WritableList(XWT.getRealm(), (List<?>)value, getElementType());						
					}
					else if (value instanceof Set<?>) {
						IContentProvider contentProvider = viewer.getContentProvider();
						if (contentProvider == null) {
							contentProvider = new ObservableSetContentProvider();
							viewer.setContentProvider(contentProvider);
						}
						value = new WritableSet(XWT.getRealm(), (List<?>)value, getElementType());						
					}
				}
			}
			else if (target instanceof ColumnViewer){
				ColumnViewer viewer = (ColumnViewer) target;
				Object[] properties = viewer.getColumnProperties();
				String[] propertyNames = new String[properties.length];
				for (int i = 0; i < properties.length; i++) {
					propertyNames[i] = properties[i].toString();					
				}
				
				if (!isArrayProperty()) {
					if (value instanceof List<?>) {
						IContentProvider contentProvider = viewer.getContentProvider();
						if (contentProvider == null) {
							contentProvider = new ObservableListContentProvider();
							viewer.setContentProvider(contentProvider);
						}
						if (contentProvider instanceof ObservableListContentProvider) {
							ObservableListContentProvider listContentProvider = (ObservableListContentProvider) contentProvider;
							viewer.setLabelProvider(new ObservableMapLabelProvider(PojoObservables
									.observeMaps(listContentProvider.getKnownElements(), Object.class,
											propertyNames)));					
						}
						value = new WritableList(XWT.getRealm(), (List<?>)value, getElementType());						
					}
					else if (value instanceof Set<?>) {
						IContentProvider contentProvider = viewer.getContentProvider();
						if (contentProvider == null) {
							contentProvider = new ObservableSetContentProvider();
							viewer.setContentProvider(contentProvider);
						}
						if (contentProvider instanceof ObservableSetContentProvider) {
							ObservableSetContentProvider setContentProvider = (ObservableSetContentProvider) contentProvider;
							viewer.setLabelProvider(new ObservableMapLabelProvider(PojoObservables
									.observeMaps(setContentProvider.getKnownElements(), Object.class,
											propertyNames)));					
						}
						value = new WritableSet(XWT.getRealm(), (List<?>)value, getElementType());						
					}
				}
			}
		}
		super.setValue(target, value);
	}

	protected boolean isArrayProperty() {
		IProperty property = getDelegate();
		Class<?> type = property.getType();
		if (type == null) {
			return false;
		}
		return type.isArray();
	}

	protected Object getElementType() {
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
