/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *  
 * Contributors:                                                               *  
 *     Soyatec - initial API and implementation                                * 
 *******************************************************************************/
package org.eclipse.e4.xwt.emf;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class EMFUtility {
	public static EObject getEObject(EObject eObj, String featureName) {
		if (eObj == null) {
			return null;
		}
		if (featureName != null) {
			int index = featureName.indexOf(".");
			while (eObj != null && index != -1) {
				String prefix = featureName.substring(0, index);
				eObj = getEObject(eObj, prefix);
				featureName = featureName.substring(index + 1);
				index = featureName.indexOf(".");
			}
			index = featureName.indexOf(".");
			if (eObj != null && index == -1) {
				EStructuralFeature sf = eObj.eClass().getEStructuralFeature(featureName);
				if (sf != null) {
					Object newValue = eObj.eGet(sf);
					if (newValue == null && sf instanceof EReference) {
						EObject newEObj = EcoreUtil.create(((EReference) sf).getEReferenceType());
						eObj.eSet(sf, newEObj);
						eObj = newEObj;
					} else if (newValue instanceof EObject) {
						eObj = (EObject) newValue;
					}
				}
			}
		}
		return eObj;
	}

	public static EObject getEObject(String typeName) {
		if (typeName == null) {
			return null;
		}

		EObject eObject = null;
		try {
			ClassLoader classLoader = XWT.getLoadingContext().getClassLoader();
			Class<?> type = classLoader.loadClass(typeName);
			Package p = type.getPackage();
			Class<?>[] classes = getClasses(p.getName());
			for (Class<?> class1 : classes) {
				try {
					if (EPackage.class.isAssignableFrom(class1)) {
						Field instance = class1.getDeclaredField("eINSTANCE");
						EPackage ePackage = (EPackage) instance.get(null);
						EClassifier eClassifier = ePackage.getEClassifier(type.getSimpleName());
						if (eClassifier != null && eClassifier instanceof EClass) {
							eObject = ePackage.getEFactoryInstance().create((EClass) eClassifier);
							if (eObject != null) {
								break;
							}
						}
					}
				} catch (Exception e) {
				}
			}
		} catch (ClassNotFoundException e) {
		}
		return eObject;
	}

	public static Class<?>[] getClasses(String pckgname) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File directory = null;
		try {
			ClassLoader classLoader = XWT.getLoadingContext().getClassLoader();
			URL resource = classLoader.getResource(pckgname.replace('.', '/'));
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class")) {
					classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		return classes.toArray(new Class[classes.size()]);
	}

}
