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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBinding;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.utils.JFacesHelper;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class BindingMetaclass extends Metaclass {

	public static class Binding implements IBinding {
		private String path;
		private Object source;

		private String elementName;

		private Widget control;

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

		protected Object getSourceObject() {
			if (source instanceof IBinding) {
				return ((IBinding) source).getValue();
			} else if (elementName != null) {
				return XWT.findElementByName(control, elementName);
			}
			Object data = control.getData(IConstants.XWT_DATACONTEXT_KEY);
			if (data == null || data == this) {
				Widget parent = (Widget) control.getData(IConstants.XWT_PARENT_KEY);
				if (parent != null) {
					return getDataContext(parent);
				}
				return null;
			}

			return getDataContext(control);
		}

		public Object getValue() {
			Object dataContext = getSourceObject();
			if (dataContext == null) {
				return null;
			}
			if (path != null) {
				IObservableValue observeData = BeansObservables.observeValue(dataContext, path);
				IObservableValue observeWidget = createObservable(control);
				if (observeWidget != null) {
					DataBindingContext bindingContext = new DataBindingContext();
					bindingContext.bindValue(observeWidget, observeData, null, null);
				}
				return observeData.getValue();
			}
			return dataContext;
		}

		private ISWTObservableValue createObservable(Widget widget) {
			if (widget instanceof Text)
				return SWTObservables.observeText((Text) widget, SWT.Modify);
			if (widget instanceof Label)
				return SWTObservables.observeText((Label) widget);
			if (widget instanceof Combo)
				return SWTObservables.observeText((Combo) widget);
			return null;
		}

		private Object getDataContext(Widget widget) {
			Object data = widget.getData(IConstants.XWT_DATACONTEXT_KEY);
			Widget parent = widget;
			while (data == null && (parent = (Widget) parent.getData(IConstants.XWT_PARENT_KEY)) != null) {
				data = ((Widget) parent).getData(IConstants.XWT_DATACONTEXT_KEY);
			}
			if (data instanceof IBinding) {
				return ((IBinding) data).getValue();
			}
			return data;
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
		return newInstance;
	}
}
