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
package org.eclipse.e4.xwt.internal.core;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.databinding.EventPropertyObservableValue;
import org.eclipse.e4.xwt.databinding.ListToArrayObservableValue;
import org.eclipse.e4.xwt.databinding.ObservableValueFactory;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.swt.widgets.Widget;

public class ScopeManager {
	public static IObservableValue observableValue(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger) {
		try {
			return observeValue(control, value, fullPath, updateSourceTrigger);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * Reserved only for the calling from XWTLoader
	 * 
	 * @param context
	 * @param data
	 * @param propertyName
	 * @return
	 */
	public static IObservableValue findObservableValue(Object control, Object data, String propertyName) {
		ScopeKeeper scope = UserData.findScopeKeeper(control);
		return scope.getObservableValue(UserData.getWidget(control), data, propertyName);
	}

	static class ObservableValueBuilder {
		private Widget widget;
		private Object value;
		private Class<?> elementType;
		private String fullPath;
		private UpdateSourceTrigger updateSourceTrigger;
		private IDataProvider dataProvider;
		private String currentPath;

		public ObservableValueBuilder(Object value, Class<?> elementType,
				String fullPath, UpdateSourceTrigger updateSourceTrigger) {
			this.value = value;
			this.fullPath = fullPath;
			this.elementType = elementType;
			this.updateSourceTrigger = updateSourceTrigger;
		}

		public IObservableValue observeValue(Object control) {
			widget = UserData.getWidget(control);
			ScopeKeeper scopeManager = UserData.findScopeKeeper(widget);
			IObservableValue observableValue = scopeManager.getObservableValue(widget, 
					value, fullPath);
			if (observableValue != null) {
				return observableValue;
			}
			dataProvider = XWT.findDataProvider(value);
			Object dataValue = value;
			currentPath = null;
			Class<?> type = elementType;
			if (fullPath.indexOf('.') == -1) {
				String segment = fullPath;
				observableValue = resolveObservablevalue(scopeManager,
						dataValue, type, segment);
			} else {
				ArrayList<String> segments = BindingExpressionParser
						.splitRoots(fullPath);
				for (String segment : segments) {
					observableValue = resolveObservablevalue(scopeManager,
							dataValue, type, segment);
					dataValue = observableValue;
					type = ObservableValueFactory.toType(dataValue);
					if (type != null) {
						dataProvider = XWT.findDataProvider(type);
					}
				}
			}
			return observableValue;
		}

		private IObservableValue resolveObservablevalue(ScopeKeeper scopeManager,
				Object dataValue, Class<?> type, String segment) {
			int length = segment.length();
			if (length > 1 && segment.charAt(0) == '('
					&& segment.charAt(length - 1) == ')') {
				// It is class
				String path = segment.substring(1, segment.length() - 1);
				int index = path.lastIndexOf('.');
				if (index != -1) {
					String className = path.substring(0, index);
					segment = path.substring(index + 1);
					type = XWT.getLoadingContext().loadClass(className);
					if (type == null) {
						throw new XWTException("Class " + className
								+ " not found");
					}
					dataProvider = XWT.findDataProvider(type);
				}
			}
			if (currentPath == null) {
				currentPath = segment;
			} else {
				currentPath = currentPath + '.' + segment;
			}

			IObservableValue segmentValue = scopeManager.getObservableValue(widget, value,
					currentPath);
			try {
				if (segmentValue == null) {
					segmentValue = createValueProperty(dataValue, segment, type);
					if (segmentValue == null) {
						throw new XWTException(" Property " + segment
								+ " is not found in " + fullPath); // maybe to
																	// raise an
																	// exception
					}
					scopeManager.addObservableValue(widget, value, currentPath,
							segmentValue);
				}
			} catch (IllegalArgumentException e) {
				// Property is not found
				String message = e.getMessage();
				if (!message.startsWith("Could not find property with name")) {
					throw e;
				}
				throw new XWTException(" Property " + segment
						+ " is not found in " + fullPath); // maybe to raise an
															// exception
			}
			return segmentValue;
		}

		protected IObservableValue createValueProperty(Object object,
				String propertyName, Class<?> targetType) {
			IObservableValue observableValue = null;
			if (UserData.getWidget(object) != null) {
				observableValue = ObservableValueFactory.createWidgetValue(
						object, propertyName, updateSourceTrigger);
			}
			Class<?> type = null;
			if (targetType == null) {
				type = ObservableValueFactory.toType(object);
			} else {
				type = targetType;
			}
			if (observableValue == null) {
				IMetaclass mateclass = XWT.getMetaclass(type);
				IProperty property = mateclass.findProperty(propertyName);
				if (property instanceof EventProperty) {
					observableValue = new EventPropertyObservableValue(object,
							(EventProperty) property);
				}
			}
			if (observableValue != null) {
				return observableValue;
			}
			IObservableValue observable = dataProvider.observableValueBridge()
					.observe(object, propertyName, type);
			if (observable instanceof IObservableValue) {
				IObservableValue activeValue = (IObservableValue) observable;
				
				Class<?> valueType = (Class<?>) activeValue.getValueType();
				if (valueType != null && valueType.isArray()) {
					// Create a IObserableValue to handle the connection between
					// Array and List
					
					Object values = dataProvider.getData(propertyName);
					ArrayList<Object> array = new ArrayList<Object>();
					if (values != null) {
						for (Object value : (Object[]) values) {
							array.add(value);
						}
					}
					WritableList writableList = new WritableList(
							XWT.getRealm(), array, valueType.getComponentType());
					
					return new ListToArrayObservableValue(writableList,
							activeValue);
				}
			}
			return observable;
		}
	}

	public static IObservableValue observeValue(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger) {
		if (value == null) {
			value = control;
		}
		ObservableValueBuilder builder = new ObservableValueBuilder(value,
				null, fullPath, updateSourceTrigger);
		return builder.observeValue(control);
	}

	public static IObservable observeValue(Object control, Object value,
			Class<?> type, String fullPath,
			UpdateSourceTrigger updateSourceTrigger) {
		ObservableValueBuilder builder = new ObservableValueBuilder(value,
				type, fullPath, updateSourceTrigger);
		return builder.observeValue(control);
	}
	
	public static IValueProperty createValueProperty(Object control, Object type, String fullPath) {	
		IValueProperty valueProperty = null; 
		
		if (fullPath.indexOf('.') == -1) {
			String segment = fullPath;
			valueProperty = doCreateValueProperty(type, segment);
		} else {
			ArrayList<String> segments = BindingExpressionParser
					.splitRoots(fullPath);
			for (String segment : segments) {
				IValueProperty segmentValueProperty = doCreateValueProperty(type, segment);
				if (valueProperty == null) { 
					valueProperty = segmentValueProperty;
				}
				else {
					valueProperty = valueProperty.value(segmentValueProperty);
				}
				type = valueProperty.getValueType();
			}
		}
		
		return valueProperty;
	}
	
	protected static IValueProperty doCreateValueProperty(Object type, String fullPath) {
		IDataProvider dataProvider = XWT.findDataProvider(type);
		return dataProvider.observableValueBridge().createValueProperty(type, fullPath);
	}
}
