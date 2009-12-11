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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
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

	private VisualEditPart editPart;
	private EObject model;
	private IPropertyDescriptor[] descriptors;

	public E4PropertySource(VisualEditPart editPart) {
		this.editPart = editPart;
		model = editPart.getCastModel();
	}

	/*
	 * (non-Javadoc)
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
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (descriptors == null) {
			List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
			EList<EStructuralFeature> features = model.eClass().getEAllStructuralFeatures();
			for (EStructuralFeature sf : features) {
				// Only enable attributes now.
				if (!(sf instanceof EAttribute)) {
					continue;
				}
				descs.add(createPropertyDescriptor(sf));
			}
			descriptors = descs.toArray(new IPropertyDescriptor[descs.size()]);
		}
		return descriptors;
	}

	private IPropertyDescriptor createPropertyDescriptor(final EStructuralFeature feature) {
		return new PropertyDescriptor(feature, feature.getName()) {
			public CellEditor createPropertyEditor(Composite parent) {
				return createCellEditor(feature, parent);
			}
		};
	}

	protected CellEditor createCellEditor(EStructuralFeature feature, Composite parent) {
		EDataType eAttributeType = ((EAttribute) feature).getEAttributeType();
		if (Object.class == eAttributeType.getInstanceClass()) {
			return null;
		}
		EDataTypeCellEditor cellEditor = new EDataTypeCellEditor(eAttributeType, parent);
		return cellEditor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id instanceof EStructuralFeature) {
			return model.eGet((EStructuralFeature) id);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		if (id instanceof EStructuralFeature) {
			return model.eIsSet((EStructuralFeature) id);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (id instanceof EStructuralFeature) {
			model.eSet((EStructuralFeature) id, value);
		}
	}

}
