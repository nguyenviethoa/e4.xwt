/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *  
 * Contributors:                                                               *  
 *     Soyatec - initial API and implementation                                * 
 *******************************************************************************/
package org.eclipse.e4.xwt.databinding;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;

/**
 * A utility class for creating Java Bean ObservableValue for a Java Bean object, if the Java Bean object support default JavaBean bindings of eclipse.core, use it directly, otherwise use the custom one of XWT.
 * 
 * @author jliu jin.liu@soyatec.com
 */
public class BeanObservableValueUtil {

	public static boolean isBeanSupport(Object target) {
		Method method = null;
		try {
			try {
				method = target.getClass().getMethod("addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class });
			} catch (NoSuchMethodException e) {
				method = target.getClass().getMethod("addPropertyChangeListener", new Class[] { PropertyChangeListener.class });
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return method != null;
	}

	public static IObservableValue observeValue(Object observed, String propertyName) {
		if (observed == null || propertyName == null) {
			return null;
		}
		if (isBeanSupport(observed)) {
			return BeansObservables.observeValue(XWT.getRealm(), observed, propertyName);
		}
		Class<?> valueType = BeanObservableValue.getValueType(observed.getClass(), propertyName);
		return new BeanObservableValue(valueType, observed, propertyName);
	}
}
