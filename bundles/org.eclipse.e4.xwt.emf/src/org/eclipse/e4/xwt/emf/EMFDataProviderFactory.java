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

import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IDataProviderFactory;
import org.eclipse.emf.databinding.EObjectObservableList;
import org.eclipse.emf.databinding.EObjectObservableMap;
import org.eclipse.emf.databinding.EObjectObservableValue;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class EMFDataProviderFactory implements IDataProviderFactory {

	public IDataProvider create(Object dataContext) {
		if (dataContext instanceof EObject) {
			EMFDataProvider dataProvider = new EMFDataProvider();
			dataProvider.setObjectInstance(dataContext);
			return dataProvider;
		}
		else if (dataContext instanceof EClassifier) {
			EClassifier classifier = (EClassifier) dataContext;
			EMFDataProvider dataProvider = new EMFDataProvider();
			dataProvider.setTypeURI(EcoreUtil.getURI(classifier));
			return dataProvider;
		}
		else if (dataContext instanceof EObjectObservableValue 
				|| dataContext instanceof EObjectObservableList || dataContext instanceof EObjectObservableMap) {
			EMFDataProvider dataProvider = new EMFDataProvider();
			dataProvider.setObjectInstance(dataContext);
			return dataProvider;
		}
		return null;
	}

	public Class<?> getType() {
		return EObject.class;
	}
}
