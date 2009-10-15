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
package org.eclipse.e4.xwt.internal.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.core.TriggerBase;
import org.eclipse.e4.xwt.internal.core.NameScope;
import org.eclipse.e4.xwt.javabean.Controller;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class UserData {
	private HashMap<String, Object> dictionary = null;
	private IObservableValueManager observableValueManager;
	
	protected IObservableValueManager getObservableValueManager() {
		return observableValueManager;
	}

	protected void setObservableValueManager(IObservableValueManager observableValueManager) {
		this.observableValueManager = observableValueManager;
	}

	public void setData(String key, Object value) {
		if (dictionary == null) {
			dictionary = new HashMap<String, Object>();
		}
		dictionary.put(key, value);
	}
	
	public Object getData(String key) {
		if (dictionary == null) {
			return null;
		}
		return dictionary.get(key);
	}

	public Object removeData(String key) {
		if (dictionary == null) {
			return null;
		}
		return dictionary.remove(key);
	}

	public boolean containsKey(String key) {
		if (dictionary == null) {
			return false;
		}
		return dictionary.containsKey(key);
	}

	public Collection<String> keySet() {
		if (dictionary == null) {
			return Collections.EMPTY_LIST;
		}
		return dictionary.keySet();
	}
	
	public static void bindNameContext(Widget widget, NameScope nameContext) {
		UserData dataDictionary = updateDataDictionary(widget);
		if (dataDictionary.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY) != null) {
			throw new IllegalStateException("Name context is already set");
		}
		dataDictionary.setData(IUserDataConstants.XWT_NAMECONTEXT_KEY, nameContext);
	}
	
	protected static UserData updateDataDictionary(Object target) {
		Widget widget = getWidget(target);
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary == null) {
			dataDictionary = new UserData();
			widget.setData(IUserDataConstants.XWT_USER_DATA_KEY, dataDictionary);
		}
		return dataDictionary;
	}

	public static Shell findShell(Widget widget) {
		Control control = getParent(widget);
		if (control != null) {
			return control.getShell();
		}
		return null;
	}

	public static Composite findCompositeParent(Widget widget) {
		Control control = getParent(widget);
		while (control != null && !(control instanceof Composite)) {
			control = getParent(control);
		}
		return (Composite) control;
	}

	public static Object findParent(Widget widget, Class<?> type) {
		Control control = getParent(widget);
		while (control != null && !(type.isInstance(control))) {
			control = getParent(control);
		}
		return control;
	}

	public static NameScope findNameContext(Widget widget) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			Object data = dataDictionary.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY);
			if (data != null) {
				return (NameScope) data;
			}
		}
		Widget parent = getTreeParent(widget);
		if (parent != null) {
			return findNameContext(parent);
		}
		return null;
	}

	public static Object findElementByName(Widget widget, String name) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			NameScope nameContext = (NameScope) dataDictionary.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY);
			if (nameContext != null) {
				Object element = nameContext.get(name);
				if (element != null) {
					return element;
				}
			}
		}
		Widget parent = getTreeParent(widget);
		if (parent != null) {
			return findElementByName(parent, name);
		}
		return null;
	}

	public static String getElementName(Object object) {
		if (object instanceof Widget) {
			Widget widget = (Widget) object;
			UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
			if (dataDictionary != null) {
				return (String) widget.getData(IUserDataConstants.XWT_NAME_KEY);
			}
		} else if (object instanceof Viewer) {
			Viewer viewer = (Viewer) object;
			UserData dataDictionary = (UserData)viewer.getControl().getData(IUserDataConstants.XWT_USER_DATA_KEY);
			return (String) dataDictionary.getData(IUserDataConstants.XWT_NAME_KEY);
		}
		return null;
	}

	public static void setCLR(Widget widget, Object type) {
		setData(widget, IUserDataConstants.XWT_CLR_KEY, type);
	}

	public static Object getCLR(Widget widget) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			Object data = dataDictionary.getData(IUserDataConstants.XWT_CLR_KEY);
			if (data != null) {
				return data;
			}
		}
		Widget parent = getParent(widget);
		if (parent != null) {
			return getCLR(parent);
		}
		return null;
	}

	public static Widget getTreeParent(Widget widget) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			return (Widget) dataDictionary.getData(IUserDataConstants.XWT_PARENT_KEY);
		}
		return null;
	}

	public static Control getParent(Widget widget) {
		if (widget instanceof Control) {
			Control control = (Control) widget;
			return control.getParent();
		} else if (widget instanceof Menu) {
			Menu item = (Menu) widget;
			return item.getParent();
		} else if (widget instanceof MenuItem) {
			MenuItem item = (MenuItem) widget;
			Menu menu = item.getParent();
			if (menu == null) {
				return null;
			}
			return menu.getParent();
		} else if (widget instanceof ScrollBar) {
			ScrollBar item = (ScrollBar) widget;
			return item.getParent();
		} else if (widget instanceof ToolTip) {
			ToolTip item = (ToolTip) widget;
			return item.getParent();
		} else if (widget instanceof CoolItem) {
			CoolItem item = (CoolItem) widget;
			return item.getParent();
		} else if (widget instanceof CTabItem) {
			CTabItem item = (CTabItem) widget;
			return item.getParent();
		} else if (widget instanceof ExpandItem) {
			ExpandItem item = (ExpandItem) widget;
			return item.getParent();
		} else if (widget instanceof TabItem) {
			TabItem item = (TabItem) widget;
			return item.getParent();
		} else if (widget instanceof TableColumn) {
			TableColumn item = (TableColumn) widget;
			return item.getParent();
		} else if (widget instanceof TableItem) {
			TableItem item = (TableItem) widget;
			return item.getParent();
		} else if (widget instanceof TableTreeItem) {
			TableTreeItem item = (TableTreeItem) widget;
			return item.getParent();
		} else if (widget instanceof ToolItem) {
			ToolItem item = (ToolItem) widget;
			return item.getParent();
		} else if (widget instanceof TreeColumn) {
			TreeColumn item = (TreeColumn) widget;
			return item.getParent();
		} else if (widget instanceof TreeItem) {
			TreeItem item = (TreeItem) widget;
			return item.getParent();
		} else if (widget instanceof Caret) {
			Caret item = (Caret) widget;
			return item.getParent();
		}
		return null;
	}

	public static Controller findEventController(Object widget) {
		return (Controller)findData(widget, IUserDataConstants.XWT_CONTROLLER_KEY);
	}

	public static Object getDataContext(Object widget) {
		return findData(widget, IUserDataConstants.XWT_DATACONTEXT_KEY);
	}

	public static TriggerBase[] getTriggers(Widget widget) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			TriggerBase[] triggers =  (TriggerBase[]) dataDictionary.getData(IUserDataConstants.XWT_TRIGGERS_KEY);
			if (triggers != null) {
				return triggers;
			}
		}
		return TriggerBase.EMPTY_ARRAY;		
	}

	public static void setTriggers(Object widget, TriggerBase[] triggers) {
		setData(widget, IUserDataConstants.XWT_TRIGGERS_KEY, triggers);
	}

	public static Widget getDataContextHost(Widget widget) {
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		Object host = null;
		if (dataDictionary != null) {
			host = dataDictionary.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
			if (host != null) {
				return widget;
			}
		}
		Widget parent = widget;
		while (parent != null) {
			dataDictionary = (UserData)parent.getData(IUserDataConstants.XWT_USER_DATA_KEY);
			if (dataDictionary != null) {
				host = dataDictionary.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
				if (host != null) {
					return parent;
				}
				parent = (Widget) dataDictionary.getData(IUserDataConstants.XWT_PARENT_KEY);
			}
			else {
				break;
			}
		}
		return null;
	}

	public static void setDataContext(Object widget, Object dataContext) {
		setData(widget, IUserDataConstants.XWT_DATACONTEXT_KEY, dataContext);
	}
	
	public static Widget getWidget(Object target) {
		if (JFacesHelper.isViewer(target)) {
			return JFacesHelper.getControl(target);
		} else if (target instanceof Widget) {
			return (Widget) target;
		}
		return null;
	}

	public static Viewer getLocalViewer(Object object) {
		return (Viewer)getLocalData(object, IUserDataConstants.XWT_VIEWER_KEY);
	}
	
	public static Object getLocalDataContext(Object object) {
		return getLocalData(object, IUserDataConstants.XWT_DATACONTEXT_KEY);
	}

	public static Object getLocalData(Object object, String key) {
		Widget widget = getWidget(object);
		if (widget == null) {
			return null;
		}
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary == null) {
			return null;
		}
		return dataDictionary.getData(key);
	}

	public static void removeLocalData(Object object, String key) {
		Widget widget = getWidget(object);
		if (widget == null) {
			return;
		}
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary == null) {
			return;
		}
		dataDictionary.removeData(key);
	}

	
	public static Map<String, Object> getLocalResources(Object object) {
		return (Map<String, Object>)getLocalData(object, IUserDataConstants.XWT_RESOURCES_KEY);
	}

	public static void setResources(Object object, Map<?, ?> resources) {
		setData(object, IUserDataConstants.XWT_RESOURCES_KEY, resources);
	}

	public static void setParent(Object object, Object parent) {
		setData(object, IUserDataConstants.XWT_PARENT_KEY, parent);
	}

	public static void setViewer(Object object, Object parent) {
		setData(object, IUserDataConstants.XWT_VIEWER_KEY, parent);
	}

	public static void setEventController(Object object, Controller controller) {
		setData(object, IUserDataConstants.XWT_CONTROLLER_KEY, controller);
	}

	public static Controller updateEventController(Object object) {
		UserData dataDictionary = updateDataDictionary(object);
		Controller controller = (Controller) dataDictionary.getData(IUserDataConstants.XWT_CONTROLLER_KEY);
		if (controller == null) {
			controller = new Controller();
			dataDictionary.setData(IUserDataConstants.XWT_CONTROLLER_KEY, controller);
		}
		return controller;
	}

	public static void setData(Object object, String key, Object value) {
		UserData dataDictionary = updateDataDictionary(object);
		dataDictionary.setData(key, value);
	}

	public static IObservableValueManager getObservableValueManager(Object object) {
		Widget widget = getWidget(object);
		if (widget == null) {
			return null;
		}
		
		UserData userData = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (userData != null) {
			return userData.getObservableValueManager();
		}
		return null;
	}
	
	public static void setObservableValueManager(Object object, IObservableValueManager eventManager) {
		Widget widget = getWidget(object);
		if (widget == null) {
			throw new IllegalStateException("Not SWT Widget");
		}
		UserData userData = (UserData)updateDataDictionary(object);
		userData.setObservableValueManager(eventManager);
	}
	
	
	public static Object findData(Object object, String key) {
		Widget widget = getWidget(object);
		if (widget == null) {
			return Collections.EMPTY_MAP;
		}
		
		UserData dataDictionary = (UserData)widget.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		Object resources = null;
		if (dataDictionary != null) {
			resources = dataDictionary.getData(key);
			if (resources != null) {
				return resources;
			}
		}
		Widget parent = widget;
		while (parent != null) {
			dataDictionary = (UserData)parent.getData(IUserDataConstants.XWT_USER_DATA_KEY);
			if (dataDictionary != null) {
				resources = dataDictionary.getData(key);
				if (resources != null) {
					return resources;
				}
				parent = (Widget) dataDictionary.getData(IUserDataConstants.XWT_PARENT_KEY);
			}
			else {
				break;
			}
		}
		return null;
	}
	
	public static Map<?, ?> getResources(Object object) {
		return (Map<?, ?>) findData(object, IUserDataConstants.XWT_RESOURCES_KEY);
	}
	
	public static void setObjectName(Object object, String name) {
		Widget widget = getWidget(object);
		NameScope nameScoped;
		if (UserData.findElementByName(widget, name) != null) {
			// throw an exception or log a message?
			return;
		}
		Widget parent = UserData.getTreeParent(widget);
		UserData dataDictionary = (UserData)parent.getData(IUserDataConstants.XWT_USER_DATA_KEY);
		if (dataDictionary != null) {
			if (dataDictionary.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY) != null) {
				nameScoped = (NameScope) dataDictionary.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY);
			} else {
				NameScope parentNameScope = parent == null ? null : findNameContext(parent);
				nameScoped = new NameScope(parentNameScope);
				bindNameContext(parent, nameScoped);
			}
			nameScoped.put(name, widget);
		}
		// throw an exception or log a message?
	}
}