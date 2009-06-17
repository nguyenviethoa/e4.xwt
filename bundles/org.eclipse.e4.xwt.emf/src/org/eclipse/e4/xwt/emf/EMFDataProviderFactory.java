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
import org.eclipse.emf.ecore.EObject;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class EMFDataProviderFactory implements IDataProviderFactory {

	public IDataProvider create(Object dataContext) {
		EMFDataProvider dataProvider = new EMFDataProvider();
		dataProvider.setObjectInstance((EObject) dataContext);
		return dataProvider;
	}

	public Class<?> getType() {
		return EObject.class;
	}
}
