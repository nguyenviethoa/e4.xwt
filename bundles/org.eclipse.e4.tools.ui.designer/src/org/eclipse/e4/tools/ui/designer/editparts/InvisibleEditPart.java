/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.ActionFilterConstants;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.JavaModelUtil;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IActionFilter;

/**
 * @yyang <yves.yang@soyatec.com>
 */
public class InvisibleEditPart extends AbstractGraphicalEditPart implements IActionFilter {
	
	public InvisibleEditPart() {
		super();
	}

	public InvisibleEditPart(Object model) {
		setModel(model);
	}

	public boolean testAttribute(Object target, String name, String value) {
		if (ActionFilterConstants.MODEL_TYPE.equals(name)) {
			Object model = getModel();
			if (model != null) {
				Class<?> type = model.getClass();
				return JavaModelUtil.isKindOf(type, value);
			}
		}
		return false;
	}
		
	@Override
	protected IFigure createFigure() {
		return new Figure();
	}
	
	@Override
	protected void addChildVisual(EditPart child, int index) {
	}

	@Override
	protected void createEditPolicies() {
		
	}

	@Override
	protected void removeChildVisual(EditPart child) {
	}

	public DragTracker getDragTracker(Request request) {
		return null;
	}
}
