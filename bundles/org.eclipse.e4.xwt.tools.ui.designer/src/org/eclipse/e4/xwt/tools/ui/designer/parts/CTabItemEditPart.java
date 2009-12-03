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

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.editor.XWTDesigner;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author rui.ban rui.ban@soyatec.com
 */
public class CTabItemEditPart extends ItemEditPart {

	public CTabItemEditPart(CTabItem item, XamlNode model) {
		super(item, model);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_OPEN) {
			Widget widget = getWidget();
			if (widget instanceof CTabItem) {
				CTabItem item = (CTabItem) widget;
				CTabFolder parent = item.getParent();
				parent.setSelection(item);
			}
			EditPart parent = getParent();
			EditDomain editDomain = EditDomain.getEditDomain(parent);
			XWTDesigner designer = (XWTDesigner) editDomain.getEditorPart();
			designer.refresh(parent);
		}
	}

}
