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
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class TabFolderEditPart extends CompositeEditPart {

	public TabFolderEditPart(TabFolder tabFolder, XamlNode model) {
		super(tabFolder, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.xwt.editparts.WidgetEditPart#getExternalModels()
	 */
	protected Collection<Object> getExternalModels() {
		List<Object> externales = new ArrayList<Object>(super.getExternalModels());
		TabFolder tabFolder = (TabFolder) getWidget();
		if (tabFolder != null) {
			TabItem[] selection = tabFolder.getSelection();
			for (TabItem tabItem : selection) {
				Control control = tabItem.getControl();
				Object data = XWTProxy.getModel(control);
				if (data != null) {
					externales.add(data);
				}
			}
		}
		return externales;
	}

	public TabItemEditPart getActiveItemPart() {
		TabFolder tabFolder = (TabFolder) getWidget();
		if (tabFolder != null) {
			TabItem[] selection = tabFolder.getSelection();
			for (TabItem tabItem : selection) {
				Object data = XWTProxy.getModel(tabItem);
				if (data != null) {
					return (TabItemEditPart) getViewer().getEditPartRegistry().get(data);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.parts.CompositeEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TabFolderLayoutEditPolicy());
	}
}
