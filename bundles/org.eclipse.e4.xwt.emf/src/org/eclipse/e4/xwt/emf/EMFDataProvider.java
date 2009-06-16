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

package org.eclipse.e4.xwt.emf;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.dataproviders.AbstractDataProvider;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author jliu
 * 
 */
public class EMFDataProvider extends AbstractDataProvider {

	private String featureName;
	private EObject eObject;
	private Class<?> eObjectType;

	public IObservableValue createObservableValue(Object valueType, String path) {
		EObject eObj = getTarget();
		if (eObj != null && path != null) {
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = EMFUtility.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return EMFObservables.observeValue(XWT.getRealm(), eObj, feature);
			}
		}
		return null;
	}

	public EObject getEObject() {
		if (eObject == null && eObjectType != null) {
			eObject = EMFUtility.getEObject(eObjectType);
		}
		return eObject;
	}

	public void setEObject(EObject eObject) {
		this.eObject = eObject;
	}

	public EObject getTarget() {
		EObject eObj = getEObject();
		if (eObj != null) {
			return EMFUtility.getEObject(eObj, featureName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#getData(java.lang.String)
	 */
	public Object getData(String path) {
		EObject eObj = getTarget();
		if (eObj != null && path != null) {
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = (EObject) getData(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return eObj.eGet(feature);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#getDataType(java.lang.String)
	 */
	public Class<?> getDataType(String path) {
		EObject eObj = getTarget();
		if (eObj != null && path != null) {
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = EMFUtility.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return feature.getEType().getInstanceClass();
			}
		}
		return null;
	}

	public void setEObjectType(Class<?> eObjectType) {
		this.eObjectType = eObjectType;
	}

	public Class<?> getEObjectType() {
		if (eObjectType == null && eObject != null) {
			eObjectType = eObject.getClass();
		}
		return eObjectType;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureName() {
		return featureName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#getData(java.lang.Object, java.lang.String)
	 */
	public Object getData(Object target, String path) {
		if (target instanceof EObject) {
			return EMFUtility.getEObject((EObject) target, path);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#setData(java.lang.String, java.lang.Object)
	 */
	public void setData(String path, Object value) {
		setData(getTarget(), path, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IDataProvider#setData(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void setData(Object target, String path, Object value) {
		if (target instanceof EObject) {
			EObject eObj = (EObject) target;
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = EMFUtility.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				eObj.eSet(feature, value);
			}
		}
	}

}
