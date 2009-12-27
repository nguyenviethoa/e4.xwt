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

import org.eclipse.e4.tools.ui.designer.palette.E4PaletteHelper;
import org.eclipse.e4.ui.model.application.MGenericTile;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.emf.common.util.EList;
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
	private Integer[] weights;
	private Integer[] oldWeights;
	
	private MGenericTile<MUIElement> parentModel;
	private MUIElement creatingModel;
	private int index = -1;
	private boolean after;

	public SashFormInsertCreateCommand(SashFormEditPart parent, CreateRequest request,
			EditPart reference) {
		this(parent, request, reference, false);
	}

	public SashFormInsertCreateCommand(SashFormEditPart parent, CreateRequest request,
			EditPart reference, boolean after) {
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
		if (oldWeights != null && creatingModel != null) {
			return true;
		}
		
		oldWeights = SashFormUtil.computeWeights(parent);
		if (oldWeights == null) {
			return false;
		}

		creatingModel = E4PaletteHelper.createElement(parentModel, request);
		return parentModel != null && creatingModel != null;
	}

	public void execute() {
		if (reference != null) {
			index = parentModel.getChildren().indexOf(reference.getModel());
			if (after) {
				index ++;
				if (index > parentModel.getChildren().size() - 1) {
					index = parentModel.getChildren().size() - 1;
				}
			}
		}
		if (index != -1) {
			parentModel.getChildren().add(index, creatingModel);
		} else {
			index = parentModel.getChildren().size() - 1;
			parentModel.getChildren().add(creatingModel);
		}
		EList<Integer> eList = parentModel.getWeights();
		eList.clear();
		
		if (after && index != -1) {
			index --;
		}
		weights = new Integer[oldWeights.length + 1];
		
		for (int i = 0; i < oldWeights.length; i++) {
			int weight = oldWeights[i];
			if (i == index) {
				int part = weight/2;
				eList.add(part);
				eList.add(weight - part);
			}
			else {
				eList.add(weight);
			}
		}
	}

	public boolean canUndo() {
		return parentModel != null && creatingModel != null;
	}

	public void undo() {
		parentModel.getChildren().remove(creatingModel);
		EList<Integer> eList = parentModel.getWeights();
		eList.clear();
		for (int i = 0; i < oldWeights.length; i++) {
			int weight = oldWeights[i];
			eList.add(weight);
		}		
	}
}
