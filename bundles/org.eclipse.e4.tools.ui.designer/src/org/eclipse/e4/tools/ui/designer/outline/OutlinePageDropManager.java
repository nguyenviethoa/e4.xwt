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
package org.eclipse.e4.tools.ui.designer.outline;

import org.eclipse.e4.tools.ui.designer.outline.commands.MoveAfterCommand;
import org.eclipse.e4.tools.ui.designer.outline.commands.MoveBeforeCommand;
import org.eclipse.e4.tools.ui.designer.outline.commands.MoveOnCommand;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd.OutlineDropManager;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

public class OutlinePageDropManager implements OutlineDropManager {

	private CommandStack commandStack;

	public OutlinePageDropManager(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.dnd.OutlineDropManager#execute(org.eclipse.gef.commands.Command)
	 */
	public boolean execute(Command command) {
		if (command == null || !command.canExecute()) {
			return false;
		}
		try {
			if (commandStack != null) {
				commandStack.execute(command);
			} else {
				command.execute();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.dnd.OutlineDropManager#getMoveAfter(java.lang.Object, java.lang.Object)
	 */
	public Command getMoveAfter(IStructuredSelection source, Object target, int operation) {
		if (target instanceof MUIElement) {
			return new MoveAfterCommand(source, (MUIElement)target, operation);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.dnd.OutlineDropManager#getMoveBefore(java.lang.Object, java.lang.Object)
	 */
	public Command getMoveBefore(IStructuredSelection source, Object target, int operation) {
		if (target instanceof MUIElement) {
			return new MoveBeforeCommand(source, (MUIElement)target, operation);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.dnd.OutlineDropManager#getMoveOn(java.lang.Object, java.lang.Object)
	 */
	public Command getMoveOn(IStructuredSelection source, Object target, int operation) {
		if (target instanceof MUIElement) {
			return new MoveOnCommand(source, (MUIElement) target, operation);
		}
		return null;
	}
}
