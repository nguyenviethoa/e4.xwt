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

import org.eclipse.e4.xwt.core.EventTrigger;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.core.TriggerBase;
import org.eclipse.e4.xwt.internal.core.NameScope;
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

public class UserDataHelper {

	public static void bindNameContext(Widget widget, NameScope nameContext) {
		if (widget.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY) != null)
			throw new IllegalStateException("Name context is already set");
		widget.setData(IUserDataConstants.XWT_NAMECONTEXT_KEY, nameContext);
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
		Object data = widget.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY);
		if (data != null) {
			return (NameScope) data;
		}
		Widget parent = getTreeParent(widget);
		if (parent != null) {
			return findNameContext(parent);
		}
		return null;
	}

	public static Object findElementByName(Widget widget, String name) {
		NameScope nameContext = (NameScope) widget.getData(IUserDataConstants.XWT_NAMECONTEXT_KEY);
		if (nameContext != null) {
			Object element = nameContext.get(name);
			if (element != null) {
				return element;
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
			return (String) widget.getData(IUserDataConstants.XWT_NAME_KEY);
		} else if (object instanceof Viewer) {
			Viewer viewer = (Viewer) object;
			return (String) viewer.getControl().getData(IUserDataConstants.XWT_NAME_KEY);
		}
		return null;
	}

	public static void setCLR(Widget widget, Object type) {
		widget.setData(IUserDataConstants.XWT_CLR_KEY, type);
	}

	public static Object getCLR(Widget widget) {
		Object data = widget.getData(IUserDataConstants.XWT_CLR_KEY);
		if (data != null) {
			return data;
		}
		Widget parent = getParent(widget);
		if (parent != null) {
			return getCLR(parent);
		}
		return null;
	}

	public static Widget getTreeParent(Widget widget) {
		return (Widget) widget.getData(IUserDataConstants.XWT_PARENT_KEY);
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

	public static Object getDataContext(Widget widget) {
		Object data = widget.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
		Widget parent = widget;
		while (data == null && (parent = (Widget) parent.getData(IUserDataConstants.XWT_PARENT_KEY)) != null) {
			data = ((Widget) parent).getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
		}
		return data;
	}

	public static TriggerBase[] getTriggers(Widget widget) {
		TriggerBase[] triggers =  (TriggerBase[]) widget.getData(IUserDataConstants.XWT_TRIGGERS_KEY);
		if (triggers == null) {
			return TriggerBase.EMPTY_ARRAY;
		}
		return triggers;
	}

	public static void setTriggers(Widget widget, TriggerBase[] triggers) {
		widget.setData(IUserDataConstants.XWT_TRIGGERS_KEY, triggers);
	}

	public static Widget getDataContextHost(Widget widget) {
		Object data = widget.getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
		Widget parent = widget;
		while (data == null && (parent = (Widget) parent.getData(IUserDataConstants.XWT_PARENT_KEY)) != null) {
			data = ((Widget) parent).getData(IUserDataConstants.XWT_DATACONTEXT_KEY);
		}
		return parent;
	}

	public static void setDataContext(Widget widget, Object dataContext) {
		widget.setData(IUserDataConstants.XWT_DATACONTEXT_KEY, dataContext);
	}
	
	public static Widget getWidget(Object target) {
		if (JFacesHelper.isViewer(target)) {
			return JFacesHelper.getControl(target);
		} else if (target instanceof Widget) {
			return (Widget) target;
		}
		return null;
	}
}