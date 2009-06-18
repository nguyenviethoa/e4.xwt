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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class EMFDataProvider extends AbstractDataProvider {
	private URI typeURI;
	private URI objectURI;

	private ResourceSet resourceSet;

	private String featureName;
	private EObject objectInstance;

	public IObservableValue createObservableValue(Object valueType, String path) {
		EObject eObj = getTarget();
		if (eObj != null && path != null) {
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = EMFBinding.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return EMFObservables.observeValue(XWT.getRealm(), eObj, feature);
			}
		}
		return null;
	}

	public URI getObjectURI() {
		return objectURI;
	}

	public void setObjectURI(URI objectURI) {
		this.objectURI = objectURI;
	}

	public URI getTypeURI() {
		return typeURI;
	}

	public void setTypeURI(URI typeURI) {
		this.typeURI = typeURI;
	}

	public EObject getObjectInstance() {
		if (objectInstance == null) {
			if (objectURI != null) {
				objectInstance = getResourceSet().getEObject(objectURI, true);
			} else if (typeURI != null) {
				EClass eClass = (EClass) getResourceSet().getEObject(typeURI, true);
				objectInstance = eClass.getEPackage().getEFactoryInstance().create(eClass);
			}
		}
		return objectInstance;
	}

	protected ResourceSet getResourceSet() {
		if (resourceSet == null) {
			resourceSet = new ResourceSetImpl();
		}
		return resourceSet;
	}

	protected void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public void setObjectInstance(EObject eObject) {
		this.objectInstance = eObject;
	}

	public EObject getTarget() {
		EObject eObj = getObjectInstance();
		if (eObj != null && featureName != null) {
			return EMFBinding.getEObject(eObj, featureName);
		}
		return eObj;
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
				eObj = EMFBinding.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return feature.getEType().getInstanceClass();
			}
		}
		return null;
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
			return EMFBinding.getEObject((EObject) target, path);
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
				eObj = EMFBinding.getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				eObj.eSet(feature, value);
			}
		}
	}
}
