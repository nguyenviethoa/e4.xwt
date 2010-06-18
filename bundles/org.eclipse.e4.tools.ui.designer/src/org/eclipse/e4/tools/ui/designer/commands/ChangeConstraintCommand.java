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
package org.eclipse.e4.tools.ui.designer.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ChangeConstraintCommand extends Command {

	private EditPart child;
	private Object constraint;
	private MUIElement element;

	public ChangeConstraintCommand(EditPart child, Object constraint) {
		this.child = child;
		this.constraint = constraint;
	}

	public boolean canExecute() {
		if (child == null || constraint == null) {
			return false;
		}
		Object model = child.getModel();
		if (model != null && model instanceof MUIElement) {
			element = (MUIElement) model;
		}
		return element != null;
	}

	public void execute() {
		if (constraint instanceof Rectangle) {
			Rectangle r = (Rectangle) constraint;
			if (element instanceof MWindow) {
				((MWindow) element).setX(r.x);
				((MWindow) element).setY(r.y);
				((MWindow) element).setWidth(r.width);
				((MWindow) element).setHeight(r.height);
			}
		}
	}
}
