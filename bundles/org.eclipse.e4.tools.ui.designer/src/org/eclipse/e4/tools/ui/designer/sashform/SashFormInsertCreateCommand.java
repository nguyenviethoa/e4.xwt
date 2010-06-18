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
package org.eclipse.e4.tools.ui.designer.sashform;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.xwt.tools.ui.palette.tools.EntryHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashFormInsertCreateCommand extends Command {

	private CreateRequest request;
	private SashFormEditPart parent;
	private EditPart reference;

	private MGenericTile<MUIElement> parentModel;
	private MUIElement creatingModel;
	private int index = -1;
	private boolean after;
	private Integer weight;

	public SashFormInsertCreateCommand(SashFormEditPart parent,
			CreateRequest request, EditPart reference) {
		this(parent, request, reference, false);
	}

	public SashFormInsertCreateCommand(SashFormEditPart parent,
			CreateRequest request, EditPart reference, boolean after) {
		this.parent = parent;
		this.request = request;
		this.reference = reference;
		this.after = after;
	}

	public boolean canExecute() {
		if (parent == null) {
			return false;
		}
		Object model = parent.getModel();
		if (model instanceof MGenericTile) {
			parentModel = (MGenericTile<MUIElement>) model;
		}

		Object element = EntryHelper.getNewObject(request);
		if (element instanceof MUIElement) {
			creatingModel = (MUIElement) element;
		}
		return parentModel != null && creatingModel != null
				&& creatingModel instanceof MPartStack;
	}

	public void execute() {
		List<MUIElement> children = parentModel.getChildren();

		if (reference != null) {
			Object model = reference.getModel();
			index = children.indexOf(model);
			if (after) {
				index++;
				if (index > children.size() - 1) {
					index = children.size() - 1;
				}
			}
		}
		if (index == -1) {
			index = children.size() - 1;
		}
		// Fixed a bug of create PartStack to empty SashForm
		if (index != -1) {
			// update weights
			MUIElement muiElement = children.get(index);
			weight = SashFormUtil.getWeight(muiElement);
			if (weight != null && weight != -1) {
				int part = weight / 2;
				muiElement.setContainerData(Integer.toString(part));
				creatingModel.setContainerData(Integer.toString(part));
			}
			children.add(index, creatingModel);
		} else {
			children.add(creatingModel);
		}
	}

	public boolean canUndo() {
		return parentModel != null && creatingModel != null;
	}

	public void undo() {
		parentModel.getChildren().remove(creatingModel);
		if (weight != null && index >= 0
				&& index < parentModel.getChildren().size() - 1) {
			MUIElement muiElement = parentModel.getChildren().get(index);
			muiElement.setContainerData(weight.toString());
		}
	}
}
