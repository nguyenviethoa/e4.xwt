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

import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.databinding.ControlDataBinding;
import org.eclipse.e4.xwt.databinding.DataBinding;
import org.eclipse.e4.xwt.dataproviders.IDataProvider;
import org.eclipse.e4.xwt.dataproviders.ObjectDataProvider;
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

		private String mode;

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

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		protected Object getSourceObject() {
			if (source instanceof IBinding) {
				return ((IBinding) source).getValue();
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

		private ResourceDictionary getResourceDictionary(Widget widget) {
			ResourceDictionary data = (ResourceDictionary) widget.getData(IUserDataConstants.XWT_RESOURCES_KEY);
			Widget parent = widget;
			while ((data == null || data.isEmpty()) && (parent = (Widget) parent.getData(IUserDataConstants.XWT_PARENT_KEY)) != null)
				data = (ResourceDictionary) ((Widget) parent).getData(IUserDataConstants.XWT_RESOURCES_KEY);
			return data;
		}

		public Object getValue() {
			Object dataContext = getSourceObject();
			IDataProvider dataProvider = null;
			DataBinding dataBinding = null;
			if (dataContext != null && dataContext instanceof Control) {
				ControlDataBinding controlDataBinding = new ControlDataBinding((Control) dataContext, (Control) control, path, type, mode);
				if (controlDataBinding != null) {
					return controlDataBinding.getValue();
				}
			}
			if (dataContext != null) {
				dataProvider = new ObjectDataProvider();
				((ObjectDataProvider) dataProvider).setObjectInstance(dataContext);
			} else {
				dataProvider = getDataProvider();
			}
			if (dataProvider != null && (path != null || xPath != null)) {
				dataBinding = new DataBinding(dataProvider, control, xPath != null ? xPath : path, type, mode);
			}
			if (dataBinding != null) {
				return dataBinding.getValue();
			}
			return dataContext;
		}

		private IDataProvider getDataProvider() {
			if (control != null) {
				ResourceDictionary rd = getResourceDictionary(control);
				if (rd == null || rd.isEmpty()) {
					return null;
				}
				for (String key : rd.keySet()) {
					Object object = rd.get(key);
					if (object instanceof IDataProvider) {
						return (IDataProvider) object;
					}
				}
			}
			return null;
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
