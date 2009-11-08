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
package org.eclipse.e4.xwt.javabean.metadata;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IDynamicBinding;
import org.eclipse.e4.xwt.databinding.BindingMode;
import org.eclipse.e4.xwt.databinding.ControlDataBinding;
import org.eclipse.e4.xwt.databinding.DataBinding;
import org.eclipse.e4.xwt.databinding.ObservableValueFactory;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Generic Binding definition
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class Binding implements IDynamicBinding {
	/**
	 * which used to decide binding type, not only text.
	 */
	private String type;
	private String path;
	private Object source;

	private String elementName;

	private Object control;

	private IXWTLoader xwtLoader;

	private BindingMode mode = BindingMode.TwoWay;

	private IValueConverter converter;

	public IValueConverter getConverter() {
		return converter;
	}

	public void setConverter(IValueConverter converter) {
		this.converter = converter;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public String getPath() {
		return path;
	}

	public void setControl(Object control) {
		this.control = control;
	}

	public Object getControl() {
		return this.control;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public BindingMode getMode() {
		return mode;
	}

	public void setMode(BindingMode mode) {
		this.mode = mode;
	}

	protected Object getSourceObject() {
		if (source != null) {
			return source;
		} else if (elementName != null) {
			return XWT.findElementByName(control, elementName);
		}
		return null;
	}

	protected Object getDataContextHost() {
		if (control == null) {
			return null;
		}
		Object data = UserData.getLocalDataContext(control);
		if (data == null || data == this) {
			Widget parent = UserData.getParent(control);
			if (parent != null) {
				return UserData.getDataContextHost(parent);
			}
			return null;
		}
		if (data != null) {
			return control;
		}
		return UserData.getDataContextHost(control);
	}

	public Object createBoundSource() {
		Object source = getSourceObject();
		if (source == null) {
			source = XWT.getDataContext(control);
		}
		if (source instanceof IDynamicBinding) {
			Object value = ((IDynamicBinding) source).createBoundSource();
			if (value != null && path != null) {
				Widget widget = UserData.getWidget(value);
				if (widget != null) {
					return ObservableValueFactory.createWidgetValue(value, path);
				}
				else {
					IDataProvider dataProvider = getDataProvider(source);
					Class<?> dataType = dataProvider.getDataType(path);
					return dataProvider.createObservableValue(dataType, path);
				}
			}
		}
		if (source != null && path != null) {
			Widget widget = UserData.getWidget(source);
			if (widget != null) {
				return ObservableValueFactory.createWidgetValue(source, path);
			}
			else {
				IDataProvider dataProvider = getDataProvider(source);
				Class<?> dataType = dataProvider.getDataType(path);
				return dataProvider.createObservableValue(dataType, path);
			}
		}
		return source;
	}

	public boolean isSourceControl() {
		Object source = getSourceObject();
		if (source == null) {
			Object dataContextHost = getDataContextHost();
			if (dataContextHost != null) {
				source = UserData.getLocalDataContext(dataContextHost);
			}
		}

		if (source instanceof IDynamicBinding) {
			return ((IDynamicBinding) source).isSourceControl();
		}
		if (source instanceof IBinding) {
			source = ((IBinding) source).getValue();
		}
		
		if (path == null) {
			return false;
		}
		int index = path.lastIndexOf('.');
		if (index == -1) {
			return (source instanceof Control || source instanceof Viewer);
		}
		String parentPath = path.substring(0, index);
		IObservableValue observableValue = ObservableValueFactory.createWidgetValue(source, parentPath);
		if (observableValue != null) {
			Object type = observableValue.getValueType();
			if (type != null) {
				return UserData.isUIElementType(type);
			}
		}
		return false;
	}

	public Object getValue() {
		Object dataContext = getSourceObject();
		if (dataContext == null) {
			Object dataContextHost = getDataContextHost();
			if (dataContextHost != null) {
				dataContext = UserData.getLocalDataContext(dataContextHost);
				if (dataContext instanceof IDynamicBinding) {
					IDynamicBinding dynamicBinding = (IDynamicBinding) dataContext;
					dataContext = dynamicBinding.createBoundSource();
				}
			}
		}
		// direct binding
		if (dataContext instanceof IBinding) {
			dataContext = ((IBinding) dataContext).getValue();
		}

		IDataProvider dataProvider = getDataProvider(dataContext);

		if (isSourceControl()) {
			try {
				ControlDataBinding controlDataBinding = new ControlDataBinding(dataContext, control, path, type, mode, converter, dataProvider);
				return controlDataBinding.getValue();
			} catch (XWTException e) {
				// in case the property cannot be bound. return value
			}
		}

		DataBinding dataBinding = null;
		if (dataProvider != null && (path != null)) {
			dataBinding = new DataBinding(control, path, type, mode, converter, dataProvider);
		}
		if (dataBinding != null) {
			return dataBinding.getValue();
		}
		return dataContext;
	}

	protected IDataProvider getDataProvider(Object dataContext) {
		if (dataContext != null) {
			if (dataContext instanceof IDataProvider) {
				return (IDataProvider) dataContext;
			} else {
				return xwtLoader.findDataProvider(dataContext);
			}
		}
		return null;
	}

	public void setXWTLoader(IXWTLoader xwtLoader) {
		this.xwtLoader = xwtLoader;
	}
}