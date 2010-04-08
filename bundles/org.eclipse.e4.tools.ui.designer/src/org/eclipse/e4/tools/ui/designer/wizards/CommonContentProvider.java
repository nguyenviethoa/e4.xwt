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
package org.eclipse.e4.tools.ui.designer.wizards;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CommonContentProvider implements ITreeContentProvider {

	private boolean displayComplexTypes = true;
	private boolean displaySuperTypes = false;

	public CommonContentProvider() {
		this(true, false);
	}

	public CommonContentProvider(boolean displayComplexTypes,
			boolean displaySuperTypes) {
		this.displayComplexTypes = displayComplexTypes;
		this.displaySuperTypes = displaySuperTypes;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	private Class<?> getBeanType(IType type) {
		try {
			IJavaProject javaProject = type.getJavaProject();
			return ProjectContext.getContext(javaProject).loadClass(
					type.getFullyQualifiedName());
		} catch (Exception e) {
		}
		return null;
	}
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ICompilationUnit) {
			IType type = ((ICompilationUnit) parentElement).findPrimaryType();
			if (type != null) {
				return new Object[]{type};
			}
		} else if (parentElement instanceof Resource) {
			EList<EObject> contents = ((Resource) parentElement).getContents();
			EPackage ePackage = null;
			for (EObject eObject : contents) {
				if (eObject instanceof EPackage) {
					ePackage = (EPackage) eObject;
				} else if (eObject instanceof EClass) {
					ePackage = ((EClass) eObject).getEPackage();
				} else {
					EClass eClass = eObject.eClass();
					ePackage = eClass.getEPackage();
				}
				if (ePackage != null) {
					break;
				}
			}
			return getChildren(ePackage);
		} else if (parentElement instanceof IType) {
			IType type = (IType) parentElement;
			Class<?> clazz = getBeanType(type);
			if (clazz != null) {
				return getChildren(clazz);
			}
		} else if (parentElement instanceof Class<?>) {
			try {
				Class<?> clazz = (Class<?>) parentElement;
				Class<?> superclass = clazz.getSuperclass();
				BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(clazz,
						superclass);
				PropertyDescriptor[] properties = beanInfo
						.getPropertyDescriptors();
				List<Object> children = new ArrayList<Object>();
				for (PropertyDescriptor pd : properties) {
					Class<?> propertyType = pd.getPropertyType();
					if (propertyType == null) {
						continue;
					}
					if (!isDisplayComplexTypes()
							&& !(propertyType.isPrimitive()
									|| (propertyType == String.class) || propertyType
									.isEnum())) {
						continue;
					}
					children.add(pd);
				}
				if (isDisplaySuperTypes() && Object.class != superclass) {
					children.add(superclass);
				}
				return children.toArray(new Object[0]);
			} catch (IntrospectionException e) {
			}
		} else if (parentElement instanceof EPackage) {
			return ((EPackage) parentElement).getEClassifiers().toArray(
					new Object[0]);
		} else if (parentElement instanceof EClass) {
			EClass eClass = (EClass) parentElement;
			List<Object> objects = new ArrayList<Object>();
			EList<EStructuralFeature> allFeatures = eClass
					.getEStructuralFeatures();
			for (EStructuralFeature sf : allFeatures) {
				if (!isDisplayComplexTypes()
						&& !(sf.getEType() instanceof EDataType)) {
					continue;
				}
				objects.add(sf);
			}
			if (isDisplaySuperTypes()) {
				objects.addAll(eClass.getESuperTypes());
			}
			return objects.toArray(new Object[0]);
		} else if (parentElement instanceof EObject) {
			return new Object[]{((EObject) parentElement).eClass()};
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof EObject) {
			return ((EObject) element).eContainer();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof PropertyDescriptor
				|| element instanceof EStructuralFeature) {
			return false;
		}
		return getChildren(element).length > 0;
	}

	/**
	 * @param displayComplexTypes
	 *            the displayComplexTypes to set
	 */
	public void setDisplayComplexTypes(boolean displayComplexTypes) {
		this.displayComplexTypes = displayComplexTypes;
	}

	/**
	 * @return the displayComplexTypes
	 */
	public boolean isDisplayComplexTypes() {
		return displayComplexTypes;
	}

	/**
	 * @param displaySuperTypes
	 *            the displaySuperTypes to set
	 */
	public void setDisplaySuperTypes(boolean displaySuperTypes) {
		this.displaySuperTypes = displaySuperTypes;
	}

	/**
	 * @return the displaySuperTypes
	 */
	public boolean isDisplaySuperTypes() {
		return displaySuperTypes;
	}

}
