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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PDC {

	/**
	 * Get type from given object, the type should be a Java Class or EClass
	 * object.
	 */
	public static Object getType(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Class<?>) {
			return (Class<?>) object;
		} else if (object instanceof EClass) {
			return (EClass) object;
		} else if (object instanceof EObject) {
			return ((EObject) object).eClass();
		} else {
			return object.getClass();
		}
	}

	public static List<Object> collectBasicProperties(Object object) {
		return collectProperties(object, false, false, true);
	}

	/**
	 * Collecting properties from given object, both Java object and EMF object
	 * can be recognized.
	 */
	public static List<Object> collectProperties(Object object,
			boolean containsSupers, boolean containsComplexies,
			boolean containsUnsettables) {
		Object type = getType(object);
		if (type == null) {
			return Collections.emptyList();
		}
		if (type instanceof Class<?>) {
			return new ArrayList<Object>(collectProperties((Class<?>) type,
					containsSupers, containsComplexies));
		} else if (type instanceof EClass) {
			List<EStructuralFeature> properties = collectProperties(
					(EClass) type, containsSupers, containsComplexies,
					containsUnsettables);
			return new ArrayList<Object>(properties);
		}
		return Collections.emptyList();
	}

	/**
	 * Collecting properties from a bean, if containsSupers is FALSE, all
	 * properties of super types will be ignored.
	 */
	public static List<PropertyDescriptor> collectProperties(
			Class<?> beanClass, boolean containsSupers, boolean containsComplex) {
		if (beanClass == null) {
			return Collections.emptyList();
		}
		try {
			Class<?> superclass = beanClass.getSuperclass();
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, superclass);
			PropertyDescriptor[] descriptors = beanInfo
					.getPropertyDescriptors();
			ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
			if (descriptors != null) {
				for (PropertyDescriptor pd : descriptors) {
					Class<?> propertyType = pd.getPropertyType();
					if (propertyType == null) {
						continue;
					}
					if (containsComplex) {
						properties.add(pd);
					} else if (propertyType.isPrimitive()
							|| propertyType.isEnum()
							|| propertyType == String.class) {
						properties.add(pd);
					} else if (propertyType.getComponentType() != null
							&& (propertyType.getComponentType().isPrimitive() || propertyType
									.getComponentType() == String.class)) {
						properties.add(pd);
					}
				}
			}
			if (containsSupers) {
				while (superclass != null && Object.class != superclass) {
					properties.addAll(collectProperties(superclass,
							containsSupers, containsComplex));
					superclass = superclass.getSuperclass();
				}
			}
			return properties;
		} catch (IntrospectionException e) {
		}
		return Collections.emptyList();
	}
	/**
	 * Collecting all properties from a EMF object, if contansReferences is
	 * TRUE, all features should be contained, otherwise, only EAttrubutes will
	 * be returned.
	 */
	public static List<EStructuralFeature> collectProperties(EClass eClass,
			boolean containsSupers, boolean containsReferences,
			boolean containsUnsettables) {
		if (eClass == null) {
			return Collections.emptyList();
		}
		List<EStructuralFeature> properties = new ArrayList<EStructuralFeature>();
		List<EStructuralFeature> features = null;
		if (containsReferences) {
			features = eClass.getEStructuralFeatures();
		} else {
			features = new ArrayList<EStructuralFeature>(eClass
					.getEAttributes());
		}
		for (EStructuralFeature sf : features) {
			try {
				if (containsUnsettables || eClass.eIsSet(sf)) {
					properties.add(sf);
				}
			} catch (Exception e) {
			}
		}
		if (containsSupers) {
			EList<EClass> eSuperTypes = eClass.getESuperTypes();
			for (EClass eSuperClass : eSuperTypes) {
				properties
						.addAll(collectProperties(eSuperClass, containsSupers,
								containsReferences, containsUnsettables));
			}
		}
		return properties;
	}

	/**
	 * Return TRUE if the property type of given object is Collection or Array,
	 * if TRUE, the property can be used as a Master.
	 */
	public static boolean isMany(Object object, String property) {
		if (object == null || property == null) {
			return false;
		}
		Object type = getType(object);
		if (type instanceof Class<?>) {
			return isMany((Class<?>) type, property);
		} else if (type instanceof EClass) {
			return isMany((EClass) type, property);
		}
		return false;
	}

	public static boolean isMany(EClass eClass, String featureName) {
		if (eClass == null || featureName == null) {
			return false;
		}
		EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
		return feature != null && feature.isMany();
	}

	public static boolean isMany(Class<?> beanClass, String propertyName) {
		if (beanClass == null || propertyName == null) {
			return false;
		}
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			PropertyDescriptor[] descriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor pd : descriptors) {
				if (!propertyName.equals(pd.getName())) {
					continue;
				}
				Class<?> propertyType = pd.getPropertyType();
				return propertyType != null
						&& (propertyType.isArray() || Collection.class
								.isAssignableFrom(propertyType));
			}
		} catch (IntrospectionException e) {
		}
		return false;
	}

	public static String getPropertyName(Object property) {
		if (property == null) {
			return null;
		}
		String propertyName = property.toString();
		if (property instanceof PropertyDescriptor) {
			propertyName = ((PropertyDescriptor) property).getName();
		} else if (property instanceof EStructuralFeature) {
			propertyName = ((EStructuralFeature) property).getName();
		}
		return propertyName;
	}

	public static String getPropertyDisplayName(Object property) {
		if (property instanceof PropertyDescriptor) {
			String displayName = ((PropertyDescriptor) property).getName();
			Class<?> propertyType = ((PropertyDescriptor) property)
					.getPropertyType();
			if (propertyType != null) {
				String typeName = propertyType.getSimpleName();
				return displayName + " - " + typeName;
			}
			return displayName;
		} else if (property instanceof EStructuralFeature) {
			String typeName = ((EStructuralFeature) property).getEType()
					.getName();
			String name = ((EStructuralFeature) property).getName();
			return name + " - " + typeName;
		}
		return getPropertyName(property);
	}

	private PDC() {
	}
}
