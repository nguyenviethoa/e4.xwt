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

import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NoOpCommand extends Command {

	public static final NoOpCommand INSTANCE = new NoOpCommand();

	private NoOpCommand() {
	}

	public boolean canExecute() {
		return true;
	}

	public void execute() {
	}

	public void redo() {
	}

	public boolean canUndo() {
		return true;
	}

	public void undo() {
	}

}
