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

import org.eclipse.e4.tools.ui.designer.palette.E4PaletteHelper;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreateCommand extends Command {

	private CreateRequest request;
	private EditPart parent;
	private EditPart intsertAfter;

	private MElementContainer<MUIElement> parentModel;
	private MUIElement creatingModel;
	private int index = -1;

	public CreateCommand(EditPart parent, CreateRequest request,
			EditPart insertAfter) {
		this.parent = parent;
		this.request = request;
		this.intsertAfter = insertAfter;
	}

	public boolean canExecute() {
		if (parent == null) {
			return false;
		}
		Object model = parent.getModel();
		if (model instanceof MElementContainer) {
			parentModel = (MElementContainer<MUIElement>) model;
		}
		creatingModel = E4PaletteHelper.createElement(parentModel, request);
		return parentModel != null && creatingModel != null;
	}

	public void execute() {
		if (intsertAfter != null) {
			index = parentModel.getChildren().indexOf(intsertAfter.getModel());
		}
		if (index != -1) {
			parentModel.getChildren().add(index, creatingModel);
		} else {
			parentModel.getChildren().add(creatingModel);
		}
	}

	public boolean canUndo() {
		return parentModel != null && creatingModel != null;
	}

	public void undo() {
		parentModel.getChildren().remove(creatingModel);
	}
}
