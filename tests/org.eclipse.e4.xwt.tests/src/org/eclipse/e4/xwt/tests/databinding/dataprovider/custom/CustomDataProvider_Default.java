/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.databinding.dataprovider.custom;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IDataProviderFactory;
import org.eclipse.e4.xwt.XWT;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class CustomDataProvider_Default {
	public static void main(String[] args) {
		URL url = CustomDataProvider_Default.class.getResource(CustomDataProvider_Default.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.addDataProvider(new IDataProviderFactory(){
				public Class<?> getType() {
					return CustomDataProvider.class;
				}
			
				public IDataProvider create(Object dataContext) {
					if (dataContext instanceof DynamicObject) {
						CustomDataProvider provider = new CustomDataProvider();
						provider.setObjectInstance((DynamicObject)dataContext);
						return provider;
					}
					return null;
				}
			});
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
