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
import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.databinding.EventPropertyObservableValue;
import org.eclipse.e4.xwt.databinding.JFaceXWTDataBinding;
import org.eclipse.e4.xwt.databinding.ListToArrayObservableValue;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.metadata.ModelUtils;
import org.eclipse.swt.widgets.Widget;

public class ScopeManager {
	public static final int AUTO = 0;
	public static final int VALUE = 1;
	public static final int SET = 2;
	public static final int LIST = 3;
	
	public static IObservableValue observableValue(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger) {
		try {
			return observeValue(control, value, fullPath, updateSourceTrigger);
		} catch (Exception e) {
		}
		return null;
	}

	public static IObservableList observableList(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger) {
		try {
			return (IObservableList) observe(control, value, fullPath, updateSourceTrigger, ScopeManager.LIST);
		} catch (Exception e) {
		}
		return null;
	}

	public static IObservableSet observableSet(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger) {
		try {
			return (IObservableSet) observe(control, value, fullPath, updateSourceTrigger, ScopeManager.SET);
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
	public static IObservableValue findObservableValue(Object context, Object control, Object data, String propertyName) {
		ScopeKeeper scope = UserData.findScopeKeeper(context);
		if (control == null) {
			control = context;
		}
		return scope.getObservableValue(UserData.getWidget(control), data, propertyName);
	}

	/**
	 * Reserved only for the calling from XWTLoader
	 * 
	 * @param context
	 * @param data
	 * @param propertyName
	 * @return
	 */
	public static IObservableSet findObservableSet(Object context, Object control, Object data, String propertyName) {
		ScopeKeeper scope = UserData.findScopeKeeper(context);
		if (control == null) {
			control = context;
		}
		return scope.getObservableSet(UserData.getWidget(control), data, propertyName);
	}
	
	/**
	 * Reserved only for the calling from XWTLoader
	 * 
	 * @param context
	 * @param data
	 * @param propertyName
	 * @return
	 */
	public static IObservableList findObservableList(Object context, Object control, Object data, String propertyName) {
		ScopeKeeper scope = UserData.findScopeKeeper(context);
		if (control == null) {
			control = context;
		}
		return scope.getObservableList(UserData.getWidget(control), data, propertyName);
	}

	static class ObservableValueBuilder {
		private Widget widget;
		private Object value;
		private Class<?> elementType;
		private String fullPath;
		private UpdateSourceTrigger updateSourceTrigger;
		private IDataProvider dataProvider;
		private String currentPath;
		private List<String> segments;
		private int observeKind = VALUE;

		public ObservableValueBuilder(Object value, Class<?> elementType,
				String fullPath, List<String> segments, UpdateSourceTrigger updateSourceTrigger, int observeKind) {
			this.value = value;
			this.fullPath = fullPath;
			this.elementType = elementType;
			this.updateSourceTrigger = updateSourceTrigger;
			this.observeKind = observeKind;
			this.segments = segments;
		}

		public IObservable observe(Object control) {
			widget = UserData.getWidget(control);
			ScopeKeeper scopeManager = UserData.findScopeKeeper(widget);
			IObservable observable = scopeManager.getObservableValue(widget, 
					value, fullPath);
			if (observable != null) {
				return observable;
			}
			dataProvider = XWT.findDataProvider(value);
			Object dataValue = value;
			currentPath = null;
			Class<?> type = elementType;
			if (segments == null || segments.isEmpty()) {
				String segment = ModelUtils.normalizePropertyName(fullPath);
				observable = resolveObservablevalue(scopeManager,
						dataValue, type, segment);
			} else {
				if (observeKind == LIST) {
					// if the first is viewers' property
					if (!JFaceXWTDataBinding.isViewerPorperty(segments.get(0))) {
						observeKind = VALUE;
					}
				}
				int size = segments.size();
				int lastObserveKind = observeKind;
				observeKind = VALUE;
				for (int i = 0; i < size; i++) {
					String segment = segments.get(i);
					if (i == (size - 1)) {
						observeKind = lastObserveKind;
					}
					observable = resolveObservablevalue(scopeManager,
							dataValue, type, segment);						
					dataValue = observable;
					type = JFaceXWTDataBinding.toType(dataValue);
					if (type != null) {
						dataProvider = XWT.findDataProvider(type);
					}
				}
			}
			return observable;
		}

		private IObservable resolveObservablevalue(ScopeKeeper scopeManager,
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

			IObservable segmentValue = scopeManager.getObservableValue(widget, value,
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
		
		protected IObservable createValueProperty(Object object,
				String propertyName, Class<?> targetType) {
			IObservable observable = null;
			Class<?> type = null;
			if (targetType == null) {
				type = JFaceXWTDataBinding.toType(object);
			} else {
				type = targetType;
			}

			if (UserData.getWidget(object) != null) {
				observable = JFaceXWTDataBinding.observeWidget(
						object, propertyName, updateSourceTrigger, observeKind);				
			}			
		
			if (observable == null) {
				IMetaclass mateclass = XWT.getMetaclass(type);
				IProperty property = mateclass.findProperty(propertyName);
				if (property instanceof EventProperty) {
					observable = new EventPropertyObservableValue(object,
							(EventProperty) property);
				}
			}
			
			if (observable != null) {
				return observable;
			}
			observable = dataProvider.observableValueBridge()
					.observe(object, propertyName, type, observeKind);
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
		return observeValue(control, value, fullPath, BindingExpressionParser.splitRoots(fullPath), updateSourceTrigger);
	}
	
	public static IObservableValue observeValue(Object control, Object value,
			String fullPath, List<String> segments, UpdateSourceTrigger updateSourceTrigger) {
		if (value == null) {
			value = control;
		}
		ObservableValueBuilder builder = new ObservableValueBuilder(value,
				null, fullPath, segments, updateSourceTrigger, ScopeManager.VALUE);
		return (IObservableValue) builder.observe(control);
	}


	public static IObservable observe(Object control, Object value,
			String fullPath, List<String> segments, UpdateSourceTrigger updateSourceTrigger) {
		return observe(control, value, fullPath, segments, updateSourceTrigger, AUTO);
	}

	public static IObservable observe(Object control, Object value,
			String fullPath, UpdateSourceTrigger updateSourceTrigger, int observeKind) {
		return observe(control, value, fullPath, BindingExpressionParser.splitRoots(fullPath), updateSourceTrigger, observeKind);
	}
	
	public static IObservable observe(Object control, Object value,
			String fullPath, List<String> segments, UpdateSourceTrigger updateSourceTrigger, int observeKind) {
		if (value == null) {
			value = control;
		}
		ObservableValueBuilder builder = new ObservableValueBuilder(value,
				null, fullPath, segments, updateSourceTrigger, observeKind);
		return builder.observe(control);
	}

	public static IObservableValue observeValue(Object control, Object value,
			Class<?> type, String fullPath, List<String> segments, UpdateSourceTrigger updateSourceTrigger) {
		ObservableValueBuilder builder = new ObservableValueBuilder(value,
				type, fullPath, segments, updateSourceTrigger, ScopeManager.VALUE);
		return (IObservableValue) builder.observe(control);
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

	/**
	 * Reserved only for the calling from XWTLoader
	 * 
	 * @param context
	 * @param data
	 * @param propertyName
	 * @return
	 */
	public static boolean isProeprtyReadOnly(IDataProvider dataProvider, String fullPath, List<String> segments) {		
		if (segments == null || segments.isEmpty()) {
			String segment = fullPath;
			return dataProvider.isPropertyReadOnly(segment);
		} else {
			Class<?> type = null;
			
			int last = segments.size() - 1;
			for (int i = 0; i < last; i++) {
				String segment = segments.get(i);
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

				type = dataProvider.getDataType(segment);
				if (type != null) {
					dataProvider = XWT.findDataProvider(type);
					if (dataProvider == null) {
						throw new XWTException("Data probider is not found for the type " + type.getName());
					}
				}
				else {
					throw new XWTException("Type is not found for the property " + segment);
				}
			}
			String segment = segments.get(last);
			
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
			return dataProvider.isPropertyReadOnly(segment);
		}
	}
}
