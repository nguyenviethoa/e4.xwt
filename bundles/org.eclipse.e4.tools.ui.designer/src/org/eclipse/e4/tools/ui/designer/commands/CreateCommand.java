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

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.tools.EntryHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreateCommand extends Command {

	private CreateRequest request;
	private EditPart parent;
	private EditPart reference;

	private MElementContainer<MUIElement> parentModel;
	private MUIElement creatingModel;
	private int index = -1;
	private Class<?> childType;

	public CreateCommand(EditPart parent, CreateRequest request, EditPart reference,
			Class<?> childType) {
		this.parent = parent;
		this.request = request;
		this.reference = reference;
		this.childType = childType;
	}

	public boolean canExecute() {
		if (parent == null) {
			return false;
		}
		Object model = parent.getModel();
		if (model instanceof MElementContainer) {
			parentModel = (MElementContainer<MUIElement>) model;
		}
		if (creatingModel == null) {
			Object element = EntryHelper.getNewObject(request);
			if (element instanceof MUIElement) {
				creatingModel = (MUIElement) element;
			}
		}
		if (childType != null && !childType.isInstance(creatingModel)) {
			return false;
		}
		return parentModel != null && creatingModel != null;
	}

	public void execute() {
		if (reference != null) {
			index = parentModel.getChildren().indexOf(reference.getModel());
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
