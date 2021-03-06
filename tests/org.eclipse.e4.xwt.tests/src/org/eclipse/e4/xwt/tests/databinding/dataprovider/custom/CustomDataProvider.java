/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.       *
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
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.databinding.XWTObservableValue;
import org.eclipse.e4.xwt.dataproviders.AbstractDataProvider;
import org.eclipse.e4.xwt.internal.core.UpdateSourceTrigger;

/**
 * An sample a custom data provider
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class CustomDataProvider extends AbstractDataProvider {
	static DataModelService dataModelService = new DataModelService() {
		public Object toModelType(Object data) {
			throw new UnsupportedOperationException();
		}

		public Object loadModelType(String className) {
			throw new UnsupportedOperationException();
		}

		public Object toModelPropertyType(Object object, String propertyName) {
			throw new UnsupportedOperationException();
		}
	};

	protected Class<?> objectType;
	protected DynamicObject object;

	public Object getData(String path) {
		return getData(getObjectInstance(), path);
	}

	public Object getData(Object object, String path) {
		assert object instanceof DynamicObject;
		return ((DynamicObject) object).getProperty(path);
	}

	public void setData(String path, Object value) {
		setData(getObjectInstance(), path, value);
	}

	public void setData(Object object, String path, Object value) {
		assert object instanceof DynamicObject;
		((DynamicObject) object).setProperty(path, value);
	}

	public IValueProperty observeValueProperty(Object valueType, String path,
			UpdateSourceTrigger updateSourceTrigger) {
		return new MyValueProperty();
	}

	@Override
	public IObservableValue observeValue(Object bean,
			final String propertyName) {
		Object target = getObjectInstance();
		if (target != null) {
			return new XWTObservableValue(target.getClass(), target,
					propertyName) {
				@Override
				protected void doSetApprovedValue(Object value) {
					CustomDataProvider.this.getObjectInstance().setProperty(
							propertyName, value);
				}

				@Override
				protected Object doGetValue() {
					return CustomDataProvider.this.getData(propertyName);
				}
			};
		}
		return null;
	}

	@Override
	public IObservableValue observeDetailValue(IObservableValue bean,
			Object ownerType, String propertyName, Object propertyType) {
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

	public DataModelService getModelService() {
		return dataModelService;
	}
}
