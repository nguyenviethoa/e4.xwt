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
package org.eclipse.e4.tools.ui.designer.commands.part;

import org.eclipse.e4.ui.model.application.ui.MGenericStack;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class AbstractPartCommand extends Command {

	protected MUIElement model;
	protected MPartStack partStack;

	private Command command;

	public AbstractPartCommand(MUIElement model, MPartStack partStack) {
		this.model = model;
		this.partStack = partStack;
	}

	public boolean canExecute() {
		if (model == null
				|| partStack == null
				|| (model instanceof EObject
						&& partStack.getChildren().size() == 1 && (EObject) partStack == ((EObject) model)
						.eContainer())) {
			return false;
		}
		if (command == null) {
			command = computeCommand();
		}
		return command != null && command.canExecute();
	}

	public void execute() {
		command.execute();
	}

	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	public void undo() {
		command.undo();
	}

	protected abstract Command computeCommand();

	protected MGenericStack<MUIElement> findParentStack() {
		if (model.getParent() instanceof MGenericStack<?>) {
			MGenericStack<MUIElement> stack = (MGenericStack<MUIElement>) model
					.getParent();
			if (stack.getChildren().size() == 1) {
				return stack;
			}
		}
		return null;
	}
}
