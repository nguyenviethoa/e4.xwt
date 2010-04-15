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
package org.eclipse.e4.tools.ui.designer.commands;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AddWindowMenuChildCommand extends Command {

	protected MWindow parent;
	protected MMenu newChild;
	protected MMenu oldChild;

	public AddWindowMenuChildCommand(MWindow parent, MMenu newChild) {
		this.parent = parent;
		this.newChild = newChild;
	}

	public boolean canExecute() {
		return parent != null && newChild != null;
	}

	public void execute() {
		oldChild = parent.getMainMenu();
		parent.setMainMenu(newChild);
	}

	public boolean canUndo() {
		return parent != null;
	}

	public void undo() {
		parent.setMainMenu(oldChild);
	}
}
