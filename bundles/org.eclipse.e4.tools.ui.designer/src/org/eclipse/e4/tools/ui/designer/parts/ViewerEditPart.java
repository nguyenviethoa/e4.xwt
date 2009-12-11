/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.parts;

import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.ViewerInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class ViewerEditPart extends VisualEditPart {

	public ViewerEditPart(EObject model) {
		super(model);
	}

	protected IVisualInfo createVisualInfo() {
		Object model = getModel();
		if (model instanceof MUIElement) {
			Object widget = ((MUIElement) model).getWidget();
			if (widget instanceof Viewer) {
				return new ViewerInfo((Viewer) widget, isRoot());
			}
		}
		return null;
	}

}
