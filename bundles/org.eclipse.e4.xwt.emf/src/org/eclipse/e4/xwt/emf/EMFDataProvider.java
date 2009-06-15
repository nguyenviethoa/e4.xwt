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
import org.eclipse.e4.xwt.dataproviders.ObjectDataProvider;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author jliu (jin.liu@soyatec.com)
 * 
 */
public class EMFDataProvider extends ObjectDataProvider {

	private String path;
	private EObject eObject;

	public IObservableValue createObservableValue(Object valueType, String path) {
		EObject eObj = getEObject();
		if (eObj != null && path != null) {
			String featureName = path;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String parent = path.substring(0, index);
				eObj = getEObject(eObj, parent);
				featureName = path.substring(index + 1);
			}
			EStructuralFeature feature = eObj.eClass().getEStructuralFeature(featureName);
			if (feature != null) {
				return EMFObservables.observeValue(XWT.getRealm(), eObj, feature);
			}
		}
		return super.createObservableValue(valueType, path);
	}

	public EObject getEObject() {
		if (eObject == null) {
			Object objectInstance = getTarget();
			if (objectInstance instanceof EObject) {
				eObject = (EObject) objectInstance;
			}
			eObject = getEObject(eObject, getPath());
		}
		return eObject;
	}

	public EObject getEObject(EObject eObj, String path) {
		if (eObj == null) {
			return null;
		}
		if (path != null) {
			int index = path.indexOf(".");
			while (eObj != null && index != -1) {
				String prefix = path.substring(0, index);
				eObj = getEObject(eObj, prefix);
				path = path.substring(index + 1);
				index = path.indexOf(".");
			}
			index = path.indexOf(".");
			if (eObj != null && index == -1) {
				EStructuralFeature sf = eObj.eClass().getEStructuralFeature(path);
				if (sf != null) {
					Object newValue = eObj.eGet(sf);
					if (newValue instanceof EObject) {
						eObj = (EObject) newValue;
					}
				}
			}
		}
		return eObj;
	}

	public void setEObject(EObject eObject) {
		this.eObject = eObject;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
