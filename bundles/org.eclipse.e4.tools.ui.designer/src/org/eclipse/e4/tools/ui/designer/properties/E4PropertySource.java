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
package org.eclipse.e4.tools.ui.designer.properties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.workbench.ui.UIEvents;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor.EDataTypeCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4PropertySource implements IPropertySource {

	private EObject model;
	private IPropertyDescriptor[] descriptors;

	public E4PropertySource(EObject model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		if (model instanceof MUIElement) {
			return ((MUIElement) model).getWidget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (descriptors == null) {
			List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
			EClass type = model.eClass();
			EList<EStructuralFeature> features = type
					.getEAllStructuralFeatures();
			for (EStructuralFeature sf : features) {
				if (!buildTopic(type, sf)) {
					continue;
				}
				descs.add(createPropertyDescriptor(sf));
			}
			descriptors = descs.toArray(new IPropertyDescriptor[descs.size()]);
		}
		return descriptors;
	}

	private boolean buildTopic(EClass type, EStructuralFeature feature) {
		if (!(feature instanceof EAttribute) || feature.getUpperBound() != 1) {
			return false;
		}
		EDataType eAttributeType = ((EAttribute) feature).getEAttributeType();
		Class<?> instanceClass = eAttributeType.getInstanceClass();
		if (instanceClass == null
				|| (instanceClass.isArray() || !(instanceClass.isPrimitive() || String.class == instanceClass))) {
			return false;
		}
		EList<EClass> eSuperTypes = type.getESuperTypes();
		for (EClass eClass : eSuperTypes) {
			if (buildTopic(eClass, feature)) {
				return true;
			}
		}
		Class<?>[] interfaces = UIEvents.class.getDeclaredClasses();
		Class<?> classType = null;
		for (Class<?> clazz : interfaces) {
			String simpleName = clazz.getSimpleName();
			if (type.getName().equals(simpleName)) {
				classType = clazz;
				break;
			}
		}
		if (classType == null) {
			return false;
		}
		Field[] declaredFields = classType.getDeclaredFields();
		for (Field field : declaredFields) {
			String name = field.getName();
			if (name.equalsIgnoreCase(feature.getName())) {
				return true;
			}
		}
		return false;
	}

	private IPropertyDescriptor createPropertyDescriptor(
			final EStructuralFeature feature) {
		return new PropertyDescriptor(feature, feature.getName()) {
			public CellEditor createPropertyEditor(Composite parent) {
				return createCellEditor(feature, parent);
			}
		};
	}

	protected CellEditor createCellEditor(EStructuralFeature feature,
			Composite parent) {
		if (!(feature instanceof EAttribute)) {
			return null;
		}
		EDataType eAttributeType = ((EAttribute) feature).getEAttributeType();
		if (Object.class == eAttributeType.getInstanceClass()) {
			return null;
		}
		EDataTypeCellEditor cellEditor = new EDataTypeCellEditor(
				eAttributeType, parent);
		return cellEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id instanceof EStructuralFeature) {
			return model.eGet((EStructuralFeature) id);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang
	 * .Object)
	 */
	public boolean isPropertySet(Object id) {
		if (id instanceof EStructuralFeature) {
			return model.eIsSet((EStructuralFeature) id);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
	 * .lang.Object)
	 */
	public void resetPropertyValue(Object id) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (id instanceof EStructuralFeature) {
			model.eSet((EStructuralFeature) id, value);
		}
	}

}
