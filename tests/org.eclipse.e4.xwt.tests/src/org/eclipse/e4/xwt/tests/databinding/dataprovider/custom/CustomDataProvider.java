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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.dataproviders.AbstractDataProvider;
import org.eclipse.e4.xwt.dataproviders.observable.XWTObservableValue;

/**
 * An sample a custom data provider
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class CustomDataProvider extends AbstractDataProvider {
	protected Class<?> objectType; 
	protected DynamicObject object; 
	
	public Object getData(String path) {
		return getObjectInstance().getProperty(path);
	}

	public IObservableValue createObservableValue(Object valueType, final String path) {
		Object target = getObjectInstance();
		if (target != null) {
			return new XWTObservableValue(valueType, target) {
				@Override
				protected void doSetApprovedValue(Object value) {
					CustomDataProvider.this.getObjectInstance().setProperty(path, value);
				}
				
				@Override
				protected Object doGetValue() {
					return CustomDataProvider.this.getData(path);
				}
			};
		}
		return null;
	}
	
	public Class<?> getDataType(String path) {
		return String.class;
	}
	
	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public void setObjectInstance(DynamicObject objectInstance) {
		object = objectInstance;
	}

	public DynamicObject getObjectInstance() {
		if (object == null) {
			try {
				object = (DynamicObject) objectType.newInstance();
			} catch (Exception e) {
				throw new XWTException(e);
			}			
		}
		return object;
	}
}