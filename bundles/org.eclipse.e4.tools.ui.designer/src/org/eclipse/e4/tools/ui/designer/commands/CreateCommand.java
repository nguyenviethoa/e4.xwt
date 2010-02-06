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
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Initializer;
import org.eclipse.e4.xwt.tools.ui.palette.request.EntryCreationFactory;
import org.eclipse.e4.xwt.tools.ui.palette.tools.PaletteCreateRequest;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;

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
	private boolean after;
	private Class<?> childType;
	private Initializer initializer;

	public CreateCommand(EditPart parent, CreateRequest request,
			EditPart reference, Class<?> childType) {
		this(parent, request, reference, childType, false);
	}

	public CreateCommand(EditPart parent, CreateRequest request,
			EditPart reference, Class<?> childType, boolean after) {
		this.parent = parent;
		this.request = request;
		this.reference = reference;
		this.after = after;
		this.childType = childType;
		if (request instanceof PaletteCreateRequest) {
			PaletteCreateRequest paletteCreateRequest = (PaletteCreateRequest) request;
			CreationFactory creationFactory = paletteCreateRequest.getFactory();
			if (creationFactory instanceof EntryCreationFactory) {
				EntryCreationFactory entryCreationFactory = (EntryCreationFactory) creationFactory;
				Entry entry = entryCreationFactory.getEntry();
				Initializer type = entry.getInitializer();
			}
		}
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
			Object element = E4PaletteHelper
					.createElement(parentModel, request);
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
		if (initializer != null) {
			initializer.initialize(creatingModel);
		}
	}

	public boolean canUndo() {
		return parentModel != null && creatingModel != null;
	}

	public void undo() {
		parentModel.getChildren().remove(creatingModel);
	}
}
