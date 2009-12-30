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

import org.eclipse.e4.tools.ui.designer.parts.PartEditPart;
import org.eclipse.e4.tools.ui.designer.parts.handlers.MovePartRequest;
import org.eclipse.e4.tools.ui.designer.parts.handlers.MovePosition;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MPSCElement;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MovePartCommand extends Command {

	private MovePartRequest request;
	private Command command;
	private PartEditPart editPart;
	private EditPart reference;
	private EditPart parent;
	private MUIElement model;
	private MUIElement refModel;
	private MElementContainer<MUIElement> parentModel;

	private int globalIndex = -1;

	public MovePartCommand(MovePartRequest request) {
		this.request = request;
	}

	public boolean canExecute() {
		if (request == null || request.getMovePosition() == null
				|| request.getEditPart() == null
				|| request.getReference() == null) {
			return false;
		}
		editPart = (PartEditPart) request.getEditPart();
		if (editPart == null) {
			return false;
		}
		reference = request.getReference();
		if (reference == null) {
			return false;
		}
		parent = reference.getParent();
		if (parent == null) {
			return false;
		}

		model = (MUIElement) editPart.getPartModel();
		if (reference instanceof PartEditPart) {
			refModel = ((PartEditPart) reference).getPartModel();
		} else {
			refModel = (MUIElement) reference.getModel();
		}
		parentModel = (MElementContainer<MUIElement>) parent.getModel();

		globalIndex = parentModel.getChildren().indexOf(refModel);
		if (globalIndex < 0 || globalIndex > parentModel.getChildren().size()) {
			globalIndex = parentModel.getChildren().size();
		}
		if (model == null || refModel == null || parentModel == null) {
			return false;
		}
		createCommand();
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

	private void createCommand() {
		MovePosition movePosition = request.getMovePosition();
		switch (movePosition) {
		case MoveToBottom:
			command = moveToBottom();
			break;
		case MoveToHeader:
			command = moveToHeader();
			break;
		case MoveToLeft:
			command = moveToLeft();
			break;
		case MoveToRight:
			command = moveToRight();
			break;
		case MoveToTop:
			command = moveToTop();
			break;
		}
	}

	private Command moveToTop() {
		CompoundCommand cmdList = new CompoundCommand();

		MPartSashContainer sashForm = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		sashForm.setHorizontal(false);

		MPartStack newParent = MApplicationFactory.eINSTANCE.createPartStack();
		newParent.setContainerData(refModel.getContainerData());

		cmdList.add(new ChangeParentCommand(newParent, model, 0));

		cmdList.add(new AddChildCommand(sashForm, newParent, 0));
		cmdList.add(new ChangeParentCommand(sashForm, refModel, 1));

		cmdList.add(new AddChildCommand(parentModel, sashForm, globalIndex));
		return cmdList.unwrap();
	}

	private Command moveToRight() {
		CompoundCommand cmdList = new CompoundCommand();

		MPartSashContainer sashForm = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		sashForm.setHorizontal(true);

		MPartStack newParent = MApplicationFactory.eINSTANCE.createPartStack();
		newParent.setContainerData(refModel.getContainerData());
		cmdList.add(new AddChildCommand(newParent, model, 0));

		cmdList.add(new ChangeParentCommand(sashForm, refModel, 0));
		cmdList.add(new AddChildCommand(sashForm, newParent, 1));
		cmdList.add(new AddChildCommand(parentModel, sashForm, globalIndex));
		return cmdList.unwrap();
	}

	private Command moveToLeft() {
		CompoundCommand cmdList = new CompoundCommand();

		MPartSashContainer sashForm = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		sashForm.setHorizontal(true);

		MPartStack newParent = MApplicationFactory.eINSTANCE.createPartStack();
		newParent.setContainerData(refModel.getContainerData());
		cmdList.add(new ChangeParentCommand(newParent, model, 0));

		cmdList.add(new AddChildCommand(sashForm, newParent, 0));
		cmdList.add(new ChangeParentCommand(sashForm, refModel, 1));
		cmdList.add(new AddChildCommand(parentModel, sashForm, globalIndex));
		return cmdList.unwrap();
	}

	private Command moveToHeader() {
		CompoundCommand cmdList = new CompoundCommand();
		EList<MUIElement> children = parentModel.getChildren();
		MPartStack partStack = null;
		if (refModel instanceof MPartStack) {
			partStack = (MPartStack) refModel;
		} else if (refModel instanceof MPart) {
			partStack = (MPartStack) (MUIElement) (((MPart) refModel)
					.getParent());
		} else if (refModel instanceof MPartSashContainer) {
			MPartSashContainer container = (MPartSashContainer) refModel;
			EList<MPSCElement> list = container.getChildren();
			for (int i = list.size(); i >= 0; i--) {
				if (list.get(i) instanceof MPartStack) {
					partStack = (MPartStack) list.get(i);
				}
			}
		}
		if (partStack == null) {
			return UnexecutableCommand.INSTANCE;
		}
		int index = partStack.getChildren().indexOf(refModel);
		if (index != -1) {
			index++;
		}
		if (index < 0 || index > children.size()) {
			index = children.size();
		}
		cmdList.add(new ChangeParentCommand((MElementContainer<?>) partStack,
				model, index));
		return cmdList.unwrap();
	}

	private Command moveToBottom() {
		CompoundCommand cmdList = new CompoundCommand();

		MPartSashContainer sashForm = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		sashForm.setHorizontal(false);

		MPartStack newParent = MApplicationFactory.eINSTANCE.createPartStack();
		newParent.setContainerData(refModel.getContainerData());
		cmdList.add(new ChangeParentCommand(newParent, model, 0));

		cmdList.add(new ChangeParentCommand(sashForm, refModel, 0));
		cmdList.add(new AddChildCommand(sashForm, newParent, 1));

		cmdList.add(new AddChildCommand(parentModel, sashForm, globalIndex));
		return cmdList.unwrap();
	}
}
