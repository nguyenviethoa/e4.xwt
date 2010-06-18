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

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.gef.commands.Command;

/**
 * @yyang <yves.yang@soyatec.com>
 */
public class SetActivePartCommand extends Command {
	private MPart part;
	private MPart oldPart;
	private MPartStack partStack;

	public SetActivePartCommand(MPart part,
			MPartStack partStack) {
		this("Swicth perspective", part, partStack);
	}

	public SetActivePartCommand(String label, MPart part,
			MPartStack partStack) {
		super(label);
		this.part = part;
		this.partStack = partStack;
	}

	@Override
	public boolean canExecute() {
		return part != null && partStack != null;
	}

	@Override
	public void execute() {
		this.oldPart = (MPart) partStack.getSelectedElement();
		partStack.setSelectedElement(part);
	}

	@Override
	public boolean canUndo() {
		return part != null && partStack != null;
	}

	@Override
	public void undo() {
		partStack.setSelectedElement(oldPart);
	}
}
