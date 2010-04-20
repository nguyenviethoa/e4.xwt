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

import org.eclipse.e4.tools.ui.designer.commands.part.PartCommandFactory;
import org.eclipse.e4.tools.ui.designer.part.PartCreateRequest;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.contribution.CreationCommand;
import org.eclipse.e4.xwt.tools.ui.palette.tools.EntryHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreatePartCommand extends CreationCommand {

	private PartCreateRequest partReq;
	private EClass creationType;

	private MUIElement creatingElement;
	private MPartStack partStack;
	private MPart header;
	private Command command;

	public CreatePartCommand(PartCreateRequest partReq) {
		super((CreateRequest) partReq.getRequest());
		this.partReq = partReq;
	}

	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}
		if (creationType == null) {
			creationType = partReq.getCreationType();
		}
		if (creationType == null) {
			return false;
		}

		if (creatingElement == null) {
			Object element = EntryHelper.getNewObject((CreateRequest) partReq.getRequest());
			if (element instanceof MUIElement) {
				creatingElement = (MUIElement) element;
			}
		}
		EditPart targetEditPart = partReq.getTargetEditPart();
		if (targetEditPart == null) {
			return false;
		}
		Object model = targetEditPart.getModel();
		if (model instanceof MPartStack) {
			partStack = (MPartStack) model;
		}
		EditPart reference = partReq.getReference();
		if (reference != null && reference.getModel() instanceof MPart) {
			header = (MPart) reference.getModel();
		}
		if (creatingElement == null || partStack == null) {
			return false;
		}

		if (command == null) {
			command = PartCommandFactory.createCommand(partReq.getPosition(), creatingElement,
					partStack, header);
		}
		return command != null && command.canExecute();
	}

	protected void doCreate(Entry entry, Object newObject) {
		command.execute();
	}

	public boolean canUndo() {
		return command != null && command.canExecute();
	}

	public void undo() {
		command.undo();
	}
}
