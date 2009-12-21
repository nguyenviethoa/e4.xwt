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
package org.eclipse.e4.xwt.tools.ui.designer.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.loader.XWTProxy;
import org.eclipse.e4.xwt.tools.ui.designer.policies.TabFolderLayoutEditPolicy;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class TabFolderEditPart extends CompositeEditPart {

	public TabFolderEditPart(Composite tabFolder, XamlNode model) {
		super(tabFolder, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.xwt.editparts.WidgetEditPart#getExternalModels()
	 */
	protected Collection<Object> getExternalModels() {
		List<Object> externales = new ArrayList<Object>(super
				.getExternalModels());
		Control control = getActiveItemControl();
		if (control != null && !control.isDisposed()) {
			Object data = XWTProxy.getModel(control);
			if (data != null) {
				externales.add(data);
			}
		}
		return externales;
	}

	public Control getActiveItemControl() {
		Item activeItem = getActiveItem();
		if (activeItem == null || activeItem.isDisposed()) {
			return null;
		}
		if (activeItem instanceof TabItem) {
			return ((TabItem) activeItem).getControl();
		} else if (activeItem instanceof CTabItem) {
			return ((CTabItem) activeItem).getControl();
		}
		return null;
	}

	public Item getActiveItem() {
		Widget widget = getWidget();
		if (widget == null || widget.isDisposed()) {
			return null;
		}
		if (widget instanceof TabFolder) {
			TabItem[] selection = ((TabFolder) widget).getSelection();
			if (selection != null && selection.length > 0) {
				return selection[0];
			}
		} else if (widget instanceof CTabFolder) {
			CTabFolder folder = (CTabFolder) widget;
			CTabItem selection = folder.getSelection();
			if (selection != null) {
				return selection;
			}
			CTabItem[] items = folder.getItems();
			if (items != null && items.length > 0) {
				folder.setSelection(items[0]);
				return items[0];
			}
		}
		return null;
	}

	public EditPart getActiveItemPart() {
		Item tabItem = getActiveItem();
		if (tabItem == null) {
			return null;
		}
		Object data = XWTProxy.getModel(tabItem);
		if (data != null) {
			return (EditPart) getViewer().getEditPartRegistry().get(data);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.parts.CompositeEditPart#
	 * createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new TabFolderLayoutEditPolicy());
	}
}
