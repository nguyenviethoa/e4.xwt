/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards.part;

import java.beans.PropertyDescriptor;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PDCTypeLabelProvider extends LabelProvider {

	private EcoreAdapterFactory factory;
	private AdapterFactoryItemDelegator adapter;

	public PDCTypeLabelProvider() {
		factory = new EcoreItemProviderAdapterFactory();
		adapter = new AdapterFactoryItemDelegator(factory);
	}

	public String getText(Object element) {
		if (element instanceof IJavaElement) {
			return ((IJavaElement) element).getElementName();
		} else if (element instanceof PropertyDescriptor) {
			String displayName = ((PropertyDescriptor) element).getName();
			Class<?> propertyType = ((PropertyDescriptor) element)
					.getPropertyType();
			if (propertyType != null) {
				String typeName = propertyType.getSimpleName();
				return displayName + " - " + typeName;
			}
			return displayName;
		} else if (element instanceof ETypedElement) {
			String typeName = ((ETypedElement) element).getEType().getName();
			String name = ((ETypedElement) element).getName();
			return name + " - " + typeName;
		} else if (element instanceof ENamedElement) {
			return ((ENamedElement) element).getName();
		} else if (element instanceof Class<?>) {
			return ((Class<?>) element).getSimpleName();
		}
		return super.getText(element);
	}
	public Image getImage(Object element) {
		try {
			if (element instanceof PropertyDescriptor) {
				return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PUBLIC);
			} else if (element instanceof Class<?>) {
				return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
			} else if (element instanceof EObject) {
				Object image = adapter.getImage(element);
				return ExtendedImageRegistry.getInstance().getImage(image);
			}
		} catch (Exception e) {
		}
		return null;
	}

}
