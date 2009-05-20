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
package org.eclipse.e4.xwt.internal;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.utils.LoggerManager;

/**
 * The class defines the in-line XAML style
 * 
 * @author yyang
 */
public class Style {
	protected Class<?> targetType;
	protected Collection<Setter> setters;

	public Class<?> getTargetType() {
		return targetType;
	}

	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}

	public Collection<Setter> getSetters() {
		if (setters == null) {
			return Collections.EMPTY_LIST;
		}
		return setters;
	}

	public void setSetters(Collection<Setter> setters) {
		this.setters = setters;
	}

	public void apply(Object target) {
		IMetaclass metaclass = XWT.getMetaclass(target);
		for (Setter setter : getSetters()) {
			String propName = setter.getProperty();
			String propValue = setter.getValue();
			IProperty prop = metaclass.findProperty(propName);
			if (prop != null && propValue != null) {
				Object value = XWT.convertFrom(prop.getType(), propValue);
				try {
					prop.setValue(target, value);
				} catch (Exception e) {
					LoggerManager.log(e);
				}
			}
		}
	}
}
