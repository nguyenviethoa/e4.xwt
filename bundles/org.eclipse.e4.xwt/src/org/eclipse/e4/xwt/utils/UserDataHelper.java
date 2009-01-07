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
package org.eclipse.e4.xwt.utils;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.NameContext;
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

	public static void bindNameContext(Widget widget, NameContext nameContext) {
		if (widget.getData(IConstants.XWT_NAMECONTEXT_KEY) != null)
			throw new IllegalStateException("Name context is already set");
		widget.setData(IConstants.XWT_NAMECONTEXT_KEY, nameContext);
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

	public static NameContext findNameContext(Widget widget) {
		Object data = widget.getData(IConstants.XWT_NAMECONTEXT_KEY);
		if (data != null) {
			return (NameContext) data;
		}
		Widget parent = getParent(widget);
		if (parent != null) {
			return findNameContext(parent);
		}
		return null;
	}

	public static void setCLR(Widget widget, Class<?> type) {
		widget.setData(IConstants.XWT_CLR_KEY, type);
	}

	public static Class<?> getCLR(Widget widget) {
		Object data = widget.getData(IConstants.XWT_CLR_KEY);
		if (data != null) {
			return (Class<?>) data;
		}
		Widget parent = getParent(widget);
		if (parent != null) {
			return getCLR(parent);
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

}