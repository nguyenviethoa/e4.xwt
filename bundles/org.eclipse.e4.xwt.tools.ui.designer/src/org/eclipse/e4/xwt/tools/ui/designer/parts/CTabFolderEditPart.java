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

import org.eclipse.e4.xwt.tools.ui.designer.loader.XWTProxy;
import org.eclipse.e4.xwt.tools.ui.designer.policies.NewNonResizeEditPolicy;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class CTabFolderEditPart extends CompositeEditPart {

	public CTabFolderEditPart(CTabFolder cTabFolder, XamlNode model) {
		super(cTabFolder, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createChild(java.lang.Object)
	 */
	protected EditPart createChild(Object model) {
		EditPart childEditPart = super.createChild(model);
		if (childEditPart != null) {
			childEditPart.installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NewNonResizeEditPolicy(false));
		}
		return childEditPart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.xwt.editparts.WidgetEditPart#getExternalModels()
	 */
	protected Collection<Object> getExternalModels() {
		Collection<Object> externals = new ArrayList<Object>(super.getExternalModels());
		CTabFolder tabFolder = (CTabFolder) getWidget();
		if (tabFolder != null) {
			CTabItem selection = tabFolder.getSelection();
			if (selection != null && selection.getControl() != null) {
				Control control = selection.getControl();
				Object data = XWTProxy.getModel(control);
				if (data != null) {
					externals.add(data);
				}
			}
		}
		return externals;
	}

}
