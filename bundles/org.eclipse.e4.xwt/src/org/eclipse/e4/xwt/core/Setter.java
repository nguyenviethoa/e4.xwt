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
package org.eclipse.e4.xwt.core;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;


/**
 * Setter of the class Style, which is used to define the in-line XAML style
 * 
 * @see Style
 * @author yyang
 */
public class Setter extends SetterBase {
	protected String property;
	protected String value;
	protected String targetName;

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void applyTo(Object element) {
		String propName = getProperty();
		String propValue = getValue();
		String targetName = getTargetName();
		Object setterTarget = element;
		if (targetName != null) {
			setterTarget = TriggerBase.getElementByName(element, targetName);
		}
		IMetaclass metaclass = XWT.getMetaclass(setterTarget);
		IProperty prop = metaclass.findProperty(propName);
		if (prop != null && propValue != null) {
			Object toValue = XWT.convertFrom(prop.getType(), propValue);
			try {
				prop.setValue(setterTarget, toValue);
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}
}
