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

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.gef.commands.Command;

/**
 * @yyang <yves.yang@soyatec.com>
 */
public class SetActivePerspectiveCommand extends Command {
	private MPerspective perspective;
	private MPerspective oldPerspective;
	private MPerspectiveStack perspectiveStack;

	public SetActivePerspectiveCommand(MPerspective perspective,
			MPerspectiveStack perspectiveStack) {
		this("Swicth perspective", perspective, perspectiveStack);
	}

	public SetActivePerspectiveCommand(String label, MPerspective perspective,
			MPerspectiveStack perspectiveStack) {
		super(label);
		this.perspective = perspective;
		this.perspectiveStack = perspectiveStack;
	}

	@Override
	public boolean canExecute() {
		return perspective != null && perspectiveStack != null;
	}

	@Override
	public void execute() {
		this.oldPerspective = perspectiveStack.getSelectedElement();
		perspectiveStack.setSelectedElement(perspective);
	}

	@Override
	public boolean canUndo() {
		return perspective != null && perspectiveStack != null;
	}

	@Override
	public void undo() {
		perspectiveStack.setSelectedElement(oldPerspective);
	}
}
