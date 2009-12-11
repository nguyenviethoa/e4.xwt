/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html Contributors: Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.commands;

import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.gef.commands.Command;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class DeleteCommand extends Command {

	private MUIElement eObject;
	private MElementContainer<MUIElement> container;
	private int index = -1;

	public DeleteCommand(MUIElement eObject) {
		this.eObject = eObject;
	}

	public boolean canExecute() {
		return eObject != null && eObject.getParent() != null;
	}

	public void execute() {
		container = eObject.getParent();
		index = container.getChildren().indexOf(eObject);
		container.getChildren().remove(eObject);
	}

	public boolean canUndo() {
		return container != null && eObject != null;
	}

	public void undo() {
		container.getChildren().add(index, eObject);
	}
}
