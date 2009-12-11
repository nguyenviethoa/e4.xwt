/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.emf.ecore.EObject;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class ShellEditPart extends CompositeEditPart {

	public ShellEditPart(EObject model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.tools.ui.designer.parts.WidgetEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		List modelChildren = new ArrayList(super.getModelChildren());
		MUIElement model = getMuiElement();
		if (model instanceof MWindow && ((MWindow) model).getMainMenu() != null) {
			modelChildren.add(((MWindow) model).getMainMenu());
		}
		return modelChildren;
	}
}
