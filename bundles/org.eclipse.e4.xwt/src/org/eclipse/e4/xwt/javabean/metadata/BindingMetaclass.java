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

import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.databinding.BindingMode;
import org.eclipse.e4.xwt.databinding.ControlDataBinding;
import org.eclipse.e4.xwt.databinding.DataBinding;
import org.eclipse.e4.xwt.impl.IBinding;
import org.eclipse.e4.xwt.impl.IUserDataConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class BindingMetaclass extends Metaclass {

	public static class Binding implements IBinding {
		/**
		 * which used to decide binding type, not only text.
		 */
		private String type;
		private String xPath;
		private String path;
		private Object source;

		private String elementName;

		private Widget control;

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

		public void setControl(Widget control) {
			this.control = control;
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

		public void setXPath(String xPath) {
			this.xPath = xPath;
		}

		public String getXPath() {
			return xPath;
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
				if (source instanceof IBinding) {
					return ((IBinding) source).getValue();
				}
				return source;
			} else if (elementName != null) {
				return XWT.findElementByName(control, elementName);
			}
			if (control == null) {
				return null;
			}
			Object data = control.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
			if (data == null || data == this) {
				Widget parent = (Widget) control.getData(IUserDataConstants.XWT_PARENT_KEY);
				if (parent != null) {
					return XWT.getDataContext(parent);
				}
				return null;
			}
			if (data != null) {
				return data;
			}
			return XWT.getDataContext(control);
		}

		public Object getValue() {
			Object dataContext = getSourceObject();
			IDataProvider dataProvider = null;
			DataBinding dataBinding = null;
			if (dataContext != null && dataContext instanceof Control) {
				try {
					ControlDataBinding controlDataBinding = new ControlDataBinding((Control) dataContext, (Control) control, path, type, mode, converter);
					return controlDataBinding.getValue();
				} catch (XWTException e) {
					// in case the property cannot be bound. return value
				}
			}
			if (dataContext != null) {
				if (dataContext instanceof IDataProvider) {
					dataProvider = (IDataProvider) dataContext;
				} else {
					dataProvider = XWT.findDataProvider(dataContext);
				}
			}

			if (dataProvider != null && (path != null || xPath != null)) {
				dataBinding = new DataBinding(dataProvider, control, xPath != null ? xPath : path, type, mode, converter);
			}
			if (dataBinding != null) {
				return dataBinding.getValue();
			}
			return dataContext;
		}
	}

	public BindingMetaclass() {
		super(BindingMetaclass.Binding.class, null);
	}

	@Override
	public Object newInstance(Object[] parameters) {
		Binding newInstance = (Binding) super.newInstance(parameters);
		if (JFacesHelper.isViewer(parameters[0]))
			newInstance.setControl(JFacesHelper.getControl(parameters[0]));
		else if (parameters[0] instanceof Control)
			newInstance.setControl((Control) parameters[0]);
		else if (parameters[0] instanceof TableItemProperty.Cell)
			newInstance.setControl(((TableItemProperty.Cell) parameters[0]).getParent());
		else if (parameters[0] instanceof Item)
			newInstance.setControl((Item) parameters[0]);
		else if (parameters[0] instanceof ViewerColumn) {
			newInstance.setControl(((ViewerColumn) parameters[0]).getViewer().getControl());
		}
		return newInstance;
	}

}
