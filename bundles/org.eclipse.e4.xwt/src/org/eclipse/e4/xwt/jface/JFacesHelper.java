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
package org.eclipse.e4.xwt.jface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.utils.ObjectUtil;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class JFacesHelper {

	public static Class<?>[] getSupportedElements() {
		return JFACES_SUPPORTED_ELEMENTS;
	}

	public static boolean isViewer(Object obj) {
		if (JFACES_VIEWER == null || obj == null)
			return false;
		return JFACES_VIEWER.isAssignableFrom(obj.getClass());
	}

	public static Control getControl(Object obj) {
		if (!isViewer(obj))
			throw new XWTException("Expecting a JFaces viewer:" + obj);
		try {
			Method method = JFACES_VIEWER.getMethod("getControl");
			return (Control) method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Class<?>[] JFACES_SUPPORTED_ELEMENTS;
	private static Class<?> JFACES_VIEWER;

	static {
		List<Class<?>> collector = new ArrayList<Class<?>>();
		try {

			JFACES_VIEWER = Class.forName("org.eclipse.jface.viewers.Viewer");
			collector.add(Class.forName("org.eclipse.jface.viewers.ComboViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.ListViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TableViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.TableTreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.CheckboxTableViewer"));
			collector.add(Class.forName("org.eclipse.jface.viewers.CheckboxTreeViewer"));
			collector.add(Class.forName("org.eclipse.jface.dialogs.TitleAreaDialog"));

			// Add CellEditors for JFave Viewers.
			collector.add(Class.forName("org.eclipse.jface.viewers.CellEditor"));
			collector.add(Class.forName("org.eclipse.jface.viewers.ComboBoxViewerCellEditor"));
			collector.add(Class.forName("org.eclipse.jface.viewers.DialogCellEditor"));
			collector.add(Class.forName("org.eclipse.jface.viewers.ColorCellEditor"));
			collector.add(TextCellEditor.class);
			collector.add(CheckboxCellEditor.class);
		} catch (ClassNotFoundException e) {
			System.out.println("No JFaces support");
		}
		JFACES_SUPPORTED_ELEMENTS = collector.toArray(new Class[collector.size()]);
	}
	
	public static String[] getViewerProperties(Viewer viewer) {
		if (viewer instanceof ColumnViewer) {
			ColumnViewer columnViewer = (ColumnViewer) viewer;
			Object[] properties = columnViewer.getColumnProperties();
			String[] propertyNames = Core.EMPTY_STRING_ARRAY;
			if (properties != null) {
				int size = 0;
				for (int i = 0; i < properties.length; i++) {
					if (properties[i] != null) {
						size ++;
					}
				}
	
				propertyNames = new String[size];
				for (int i = 0, j = 0; i < properties.length; i++) {
					if (properties[i] != null) {
						propertyNames[j++] = properties[i].toString();												
					}
				}
			}
			if (propertyNames.length != 0) {				
				return propertyNames;
			}
		}
		String path = (String)UserData.getLocalData(viewer, PropertiesConstants.PROPERTY_BINDING_PATH);
		if (path != null) {
			return new String [] {path};
		}
		return Core.EMPTY_STRING_ARRAY;
	}

	public static Object getColumnObject(Object element, int columnIndex, Object[] properties) {
		if (element == null) {
			return null;
		}
		Object dataContext = element;
		
		if (properties != null) {
			Object propertyElement = properties[columnIndex];
			if (propertyElement != null) {
				String propertyName = propertyElement.toString();
				if (propertyName != null) {
					try {
						IMetaclass metaclass = XWT.getMetaclass(dataContext);
						IProperty property = metaclass.findProperty(propertyName.toLowerCase());
						if (property != null) {
							dataContext = property.getValue(dataContext);
							if (dataContext != null) {		
								Class<?> type = dataContext.getClass();
								Class<?> propertyType = property.getType();
								if (propertyType != null && !propertyType.isAssignableFrom(type)) {
									dataContext = ObjectUtil.resolveValue(dataContext, type, propertyType, dataContext);
								}
							}
						}
					} catch (Exception e) {
						throw new XWTException(e);
					}			
				}
			}
		}
		return dataContext;
	}

	public static String getColumnText(Viewer viewer, Object element, int columnIndex) {
		String[] propertyNames = JFacesHelper.getViewerProperties(viewer);
		return getColumnText(viewer, element, columnIndex, propertyNames);
	}

	public static String getColumnText(Viewer viewer, Object element, int columnIndex, Object[] properties) {
		Object value = getColumnObject(element, columnIndex, properties);

		try {
			if (viewer instanceof TableViewer) {
				Table table = ((TableViewer)viewer).getTable();
				TableColumn[] columns = table.getColumns();
				TableColumn column = columns[columnIndex];
				if (UserData.hasLocalData(column,
						IUserDataConstants.XWT_PROPERTY_ITEM_TEXT_KEY)) {
					Object userDataValue = UserData.getLocalData(column,
							IUserDataConstants.XWT_PROPERTY_ITEM_TEXT_KEY);
					if (userDataValue instanceof IBinding) {
						IBinding binding = (IBinding) userDataValue;
						binding.reset();
						UserData.setDataContext(column, value);
						value = binding.getValue();
					} else {
						value = userDataValue;
					}
				}
				else if (UserData.hasLocalData(column,
						IUserDataConstants.XWT_PROPERTY_ITEM_IMAGE_KEY)) {
					return null;
				}
			}
		} catch (Exception e) {
			throw new XWTException(e);
		}
		if (value != null) {
			return value.toString();
		}
		return "";
	}
	
	public static Image getColumnImage(Viewer viewer, Object element, int columnIndex) {
		String[] propertyNames = JFacesHelper.getViewerProperties(viewer);
		return getColumnImage(viewer, element, columnIndex, propertyNames);
	}

	public static Image getColumnImage(Viewer viewer, Object element, int columnIndex, Object[] properties) {
		Object value = getColumnObject(element, columnIndex, properties);
		if (value == null) {
			return null;
		}
		try {
			if (viewer instanceof TableViewer) {
				Table table = ((TableViewer)viewer).getTable();
				TableColumn[] columns = table.getColumns();
				TableColumn column = columns[columnIndex];
				if (UserData.hasLocalData(column,
							IUserDataConstants.XWT_PROPERTY_ITEM_IMAGE_KEY)) {
					Object userDataValue = UserData.getLocalData(column,
							IUserDataConstants.XWT_PROPERTY_ITEM_IMAGE_KEY);
					if (userDataValue instanceof IBinding) {
						IBinding binding = (IBinding) userDataValue;
						binding.reset();
						UserData.setDataContext(column, value);
						value = binding.getValue();
					} else {
						value = userDataValue;
					}
				}
				else{
					return null;
				}
			}
		} catch (Exception e) {
			throw new XWTException(e);
		}
		if (value instanceof IObservableValue) {
			IObservableValue observableValue = (IObservableValue) value;
			value = observableValue.getValue();
		}
		if (value instanceof Image) {
			return (Image) value;
		} else if (value != null) {
			value = ObjectUtil.resolveValue(value, Image.class, value);
			if (value == null) {
				return null;
			}
			if (value instanceof Image) {
				return (Image) value;
			}
			throw new XWTException("Converter from " + value.getClass()
					+ " to Image is missing.");
		}
		return null;
	}
}
