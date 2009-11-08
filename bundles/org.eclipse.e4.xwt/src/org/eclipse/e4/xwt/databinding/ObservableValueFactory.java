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
package org.eclipse.e4.xwt.databinding;

import java.lang.reflect.Method;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.internal.databinding.menuitem.MenuItemEnabledObservableValue;
import org.eclipse.e4.xwt.internal.databinding.menuitem.MenuItemSelectionObservableValue;
import org.eclipse.e4.xwt.internal.utils.ObservableValueManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ObservableValueFactory {
	static final String ENABLED = "enabled";
	static final String SELECTION = "selection";

	static final String TEXT = "text";
	public static final Class<?>[] CONTROL_ARGUMENT_TYPES = new Class[] { Control.class };
	public static final Class<?>[] VIEWER_ARGUMENT_TYPES = new Class[] { Viewer.class };

	public static IObservableValue createWidgetValue(Object object, String propertyName) {
		try {
			IObservableValue observableValue = observePropertyValue(object, propertyName);
			if (observableValue != null) {
				return observableValue;
			}
		} catch (XWTException e) {
		}

		IMetaclass mateclass = XWT.getMetaclass(object);
		IProperty property = mateclass.findProperty(propertyName);
		if (property instanceof EventProperty) {
			IObservableValueManager eventManager = UserData.getObservableValueManager(object);
			if (eventManager == null) {
				eventManager = new ObservableValueManager(object);
				UserData.setObservableValueManager(object, eventManager);
			}
			IObservableValue observableValue = eventManager.getValue(property);
			if (observableValue == null) {
				observableValue = new EventPropertyObservableValue(object, (EventProperty)property);
			}
			return observableValue;
		}
		return null;
	}

	protected static IObservableValue observePropertyValue(Object object, String propertyName) {
		if (object instanceof Control) {
			return observePropertyValue((Control)object, propertyName);
		}
		else if (object instanceof Viewer) {
			return observePropertyValue((Viewer)object, propertyName);			
		}
		else if (object instanceof MenuItem) {
			//
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=280157
			// testcase: org.eclipse.e4.xwt.tests.databinding.bindcontrol.BindMenuItem
			//
			if (ENABLED.equalsIgnoreCase(propertyName)) {
				return new MenuItemEnabledObservableValue((MenuItem) object);
			} else if (SELECTION.equalsIgnoreCase(propertyName)) {
				return new MenuItemSelectionObservableValue((MenuItem) object);
			}
		}
		return null;
	}
	
	protected static IObservableValue observePropertyValue(Control control, String propertyName) {
		if (TEXT.equalsIgnoreCase(propertyName)) {
			if (control instanceof Text) {
				IObservableValue observableValue = SWTObservables.observeText(control, SWT.Modify);
				if (observableValue != null) {
					return observableValue;
				}
			}
			// widget button is not supported at 3.4 version.
			if (SWT.getVersion() == 3449 && control instanceof Button) {
				return null;
			}
			try {
				IObservableValue observableValue = SWTObservables.observeText(control);
				if (observableValue != null) {
					return observableValue;
				}
			} catch (IllegalArgumentException e) {
				throw new XWTException(e);
			}
		} else {
			if (propertyName == null) {
				return null;
			}
			String getterName = "observe" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			Method method;
			try {
				method = SWTObservables.class.getMethod(getterName, CONTROL_ARGUMENT_TYPES);
				if (method == null) {
					for (Method element : SWTObservables.class.getMethods()) {
						if (element.getParameterTypes().length != 0) {
							continue;
						}
						if (element.getName().equalsIgnoreCase(getterName)) {
							method = element;
							break;
						}
					}
				}
				if (method != null) {
					IObservableValue observableValue = (IObservableValue) method.invoke(null, control);
					if (observableValue != null) {
						return observableValue;
					}
				}
			} catch (Exception e) {
				throw new XWTException(e);
			}
		}
		IMetaclass mateclass = XWT.getMetaclass(control);
		IProperty property = mateclass.findProperty(propertyName);
		if (property instanceof EventProperty) {
			return new EventPropertyObservableValue(control, (EventProperty)property);
		}
		return null;
	}

	protected static IObservableValue observePropertyValue(Viewer viewer, String property) {
		if (property == null) {
			return null;
		}
		String getterName = "observe" + property.substring(0, 1).toUpperCase() + property.substring(1);
		Method method;
		try {
			method = ViewersObservables.class.getMethod(getterName, VIEWER_ARGUMENT_TYPES);
			if (method == null) {
				for (Method element : ViewersObservables.class.getMethods()) {
					if (element.getParameterTypes().length != 0) {
						continue;
					}
					if (element.getName().equalsIgnoreCase(getterName)) {
						method = element;
						break;
					}
				}
			}
			if (method != null) {
				IObservableValue observableValue = (IObservableValue) method.invoke(null, viewer);
				if (observableValue != null) {
					return observableValue;
				}
			}
		} catch (Exception e) {
			throw new XWTException(e);
		}
		return null;
	}
}
