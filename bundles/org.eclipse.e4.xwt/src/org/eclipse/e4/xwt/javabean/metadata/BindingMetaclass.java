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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.impl.IBinding;
import org.eclipse.e4.xwt.impl.IUserDataConstants;
import org.eclipse.e4.xwt.utils.JFacesHelper;
import org.eclipse.e4.xwt.utils.LoggerManager;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class BindingMetaclass extends Metaclass {

	private static final Map<Object, BindingContext> bindingContext = new HashMap<Object, BindingContext>();

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
			Object data = control.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
			if (data == null || data == this) {
				Widget parent = (Widget) control.getData(IUserDataConstants.XWT_PARENT_KEY);
				if (parent != null) {
					return XWT.getDataContext(parent);
				}
				return null;
			}

			return XWT.getDataContext(control);
		}

		public Object getValue() {
			Object dataContext = getSourceObject();
			if (dataContext == null) {
				return null;
			}
			java.util.List<Object> dataContexts = new ArrayList<Object>();
			dataContexts.add(dataContext);
			if (path != null) {
				String[] paths = path.trim().split("\\.");
				IObservableValue observeData = null;
				String propertyName = null;
				if (paths.length > 1) {
					for (int i = 0; i < paths.length - 1; i++) {
						String path = paths[i];
						if (dataContext != null) {
							bindingContext.put(dataContext, new BindingContext(dataContext));
							dataContext = getObserveData(dataContext, path);
							dataContexts.add(dataContext);
						}
					}
					propertyName = paths[paths.length - 1];
				} else if (paths.length == 1) {
					propertyName = path;
				}
				observeData = BeansObservables.observeValue(dataContext, propertyName);

				final IObservableValue observeWidget = createObservable(control);
				if (observeWidget != null) {
					DataBindingContext bindingContext = new DataBindingContext();
					bindingContext.bindValue(observeWidget, observeData, null, null);
				}
				BindingContext bc = new BindingContext(dataContext);
				bc.observeValue = observeData;
				bc.observeWidget = observeWidget;
				bc.propertyName = propertyName;
				bindingContext.put(dataContext, bc);
				// addDataContextListener(observeWidget, path, dataContexts, observeData);
				return observeData.getValue();
			}
			return dataContext;
		}

		// private void addDataContextListener(IObservableValue observeWidget, String path, java.util.List<Object> dataContexts, IObservableValue observeData) {
		// DataContextChangeListener propertyChangeListener = new DataContextChangeListener(observeWidget, path, dataContexts, observeData);
		// for (Object dataContext : dataContexts) {
		// Class<?> dataContextClass = dataContext.getClass();
		// Field[] fields = dataContextClass.getDeclaredFields();
		// try {
		// Method addListenerMethod = dataContextClass.getDeclaredMethod("addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class });
		// for (Field field : fields) {
		// if (!PropertyChangeSupport.class.equals(field.getType())) {
		// addListenerMethod.invoke(dataContext, new Object[] { field.getName(), propertyChangeListener });
		// }
		// }
		// } catch (Exception e) {
		// LoggerManager.log(e);
		// }
		// }
		// }

		private Object getObserveData(Object dataContext, String path) {
			try {
				Class<?> dataContextClass = dataContext.getClass();
				String getMethiodName = "get" + path.substring(0, 1).toUpperCase() + path.substring(1);
				Method getMethod = dataContextClass.getDeclaredMethod(getMethiodName, new Class[] {});
				if (getMethod != null) {
					return getMethod.invoke(dataContext, new Object[] {});
				}
			} catch (SecurityException e) {
				LoggerManager.log(e);
			} catch (NoSuchMethodException e) {
				LoggerManager.log(e);
			} catch (IllegalArgumentException e) {
				LoggerManager.log(e);
			} catch (IllegalAccessException e) {
				LoggerManager.log(e);
			} catch (InvocationTargetException e) {
				LoggerManager.log(e);
			}
			return null;
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
		return newInstance;
	}

	static class BindingContext {
		String propertyName;
		Object source;
		IObservableValue observeValue;
		IObservableValue observeWidget;

		public BindingContext(Object source) {
			this.source = source;
			addListener(source);
		}

		public void setNewValue(Object newValue) {
			if (newValue != null && newValue.getClass() == source.getClass() && observeValue != null && propertyName != null && observeWidget != null) {

				Field[] fields = source.getClass().getDeclaredFields();
				for (Field field : fields) {
					Object oldPropertyValue = getPropertyValue(source, field.getName());
					Object newPropertyValue = getPropertyValue(newValue, field.getName());
					if (oldPropertyValue != null && oldPropertyValue != newPropertyValue) {
						BindingContext bc = bindingContext.get(oldPropertyValue);
						if (bc != null) {
							bc.setNewValue(newPropertyValue);
						}
					}
				}

				observeValue = BeansObservables.observeValue(newValue, propertyName);
				addListener(newValue);
				source = newValue;
				bindingContext.put(source, this);
				if (observeWidget != null) {
					DataBindingContext bindingContext = new DataBindingContext();
					bindingContext.bindValue(observeWidget, observeValue, null, null);
				}
			}
		}

		private Object getPropertyValue(Object object, String propertyName) {
			String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				Method getMethod1 = object.getClass().getDeclaredMethod(getMethodName, new Class[] {});
				if (getMethod1 != null) {
					return getMethod1.invoke(object, new Object[] {});
				}
			} catch (Exception e) {
			}
			return null;
		}

		private void applyNewValue(Object oldValue, Object newValue) {
			BindingContext bc = bindingContext.get(oldValue);
			if (bc == null) {
				if (oldValue.getClass() == newValue.getClass() && oldValue.getClass() != String.class) {
					// children, ...
					Field[] fields = oldValue.getClass().getDeclaredFields();
					for (Field field : fields) {
						Object oldPropertyValue = getPropertyValue(oldValue, field.getName());
						Object newPropertyValue = getPropertyValue(newValue, field.getName());
						if (oldPropertyValue != null && oldPropertyValue != newPropertyValue) {
							applyNewValue(oldPropertyValue, newPropertyValue);
						}
					}
				}
				return;
			}
			bc.setNewValue(newValue);
		}

		public void addListener(Object dataContext) {
			if (dataContext == null) {
				return;
			}
			PropertyChangeListener p = new PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent evt) {
					Object oldValue = evt.getOldValue();
					Object newValue = evt.getNewValue();
					if (oldValue == newValue) {
						return;
					}
					applyNewValue(oldValue, newValue);
				}

			};

			Class<?> dataContextClass = dataContext.getClass();
			Field[] fields = dataContextClass.getDeclaredFields();
			try {
				Method addListenerMethod = dataContextClass.getDeclaredMethod("addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class });
				for (Field field : fields) {
					if (!PropertyChangeSupport.class.equals(field.getType())) {
						addListenerMethod.invoke(dataContext, new Object[] { field.getName(), p });
					}
				}
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}
}
