/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.designer.swt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.utils.StyleHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class SWTTools {

	public static Point getLocation(Widget widget) {
		if (widget == null || widget.isDisposed()) {
			return new Point(-1, -1);
		}
		if (widget instanceof Control) {
			Control control = (Control) widget;
			Point location = control.getLocation();
			if (control instanceof Shell) {
				return location;
			}
			Composite parent = control.getParent();
			if (parent instanceof Shell) {
				Point l = getOffset((Shell) parent);
				location.x = location.x + l.x;
				location.y = location.y + l.y;
			} else if (StyleHelper.checkStyle(parent, SWT.BORDER)) {
				int borderWidth = parent.getBorderWidth();
				location.x += borderWidth;
				location.y += borderWidth;
			}
			return location;
		}
		return WidgetLocator.getLocation(widget);
	}

	public static Point getOffset(Scrollable scroll) {
		if (scroll == null || scroll.isDisposed()) {
			return new Point(0, 0);
		}
		Rectangle bounds = scroll.getBounds();
		Rectangle clientArea = scroll.getClientArea();
		Rectangle calced = scroll.computeTrim(bounds.x, bounds.y, clientArea.width, clientArea.height);
		Rectangle correct = new Rectangle(2 * bounds.x - calced.x, 2 * bounds.y - calced.y, clientArea.width - 1, // bug workaround
				clientArea.height - 1);
		int x = correct.x - bounds.x;
		int y = correct.y - bounds.y;
		return new Point(x, y);

	}

	public static Point getSize(Control control) {
		if (control == null || control.isDisposed()) {
			return new Point(-1, -1);
		}
		Point size = control.getSize();
		return size;
	}

	public static Rectangle getBounds(Widget widget) {
		if (widget instanceof Control) {
			Control control = (Control) widget;
			Point l = getLocation(control);
			Point s = getSize(control);
			return new Rectangle(l.x, l.y, s.x, s.y);
		}
		return WidgetLocator.getBounds(widget);
	}

	public static Widget[] getChildren(Widget widget) {
		if (widget == null || widget.isDisposed()) {
			return new Widget[0];
		}
		List<Widget> children = new ArrayList<Widget>();
		if (widget instanceof Composite) {
			for (Control control : ((Composite) widget).getChildren()) {
				children.add(control);
			}
		}
		if (widget instanceof Decorations) {
			Menu menuBar = ((Decorations) widget).getMenuBar();
			if (menuBar != null) {
				children.add(menuBar);
			}
		}
		// For all items.
		{
			try {
				Method getItemsMethod = widget.getClass().getDeclaredMethod("getItems");
				Object[] items = (Object[]) getItemsMethod.invoke(widget, new Object[0]);
				for (Object item : items) {
					children.add((Widget) item);
				}
			} catch (Exception e) {
			}
		}
		// For controls of items.
		if (widget instanceof Item) {
			try {
				Method getControlMethod = widget.getClass().getDeclaredMethod("getControl");
				Object control = getControlMethod.invoke(widget, new Object[0]);
				if (control != null) {
					children.add((Widget) control);
				}
			} catch (Exception e) {
			}
		}
		// For Context Menu and sub menu of MenuItem
		{
			try {
				Method getMenuMethod = widget.getClass().getDeclaredMethod("getMenu");
				Object menu = getMenuMethod.invoke(widget, new Object[0]);
				if (menu != null) {
					children.add((Widget) menu);
				}
			} catch (Exception e) {
			}
		}
		{// getColumns
			try {
				Method getColumnsMethod = widget.getClass().getDeclaredMethod("getColumns", new Class<?>[0]);
				Object[] columns = (Object[]) getColumnsMethod.invoke(widget, new Object[0]);
				for (Object col : columns) {
					children.add((Widget) col);
				}
			} catch (Exception e) {
			}
		}
		return children.toArray(new Widget[children.size()]);
	}
}
