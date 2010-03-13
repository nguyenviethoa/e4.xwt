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

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.designer.commands.part.PartCommandFactory;
import org.eclipse.e4.tools.ui.designer.palette.E4PaletteHelper;
import org.eclipse.e4.tools.ui.designer.part.PartCreateRequest;
import org.eclipse.e4.tools.ui.designer.wizards.NewPartWizard;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Initializer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreatePartCommand extends Command {

	private PartCreateRequest partReq;
	private EClass creationType;

	private MUIElement creatingElement;
	private MPartStack partStack;
	private MPart header;
	private Command command;

	public CreatePartCommand(PartCreateRequest partReq) {
		this.partReq = partReq;
	}

	public boolean canExecute() {
		if (partReq == null) {
			return false;
		}
		if (creationType == null) {
			creationType = partReq.getCreationType();
		}
		if (creationType == null) {
			return false;
		}

		if (creatingElement == null) {
			Object element = E4PaletteHelper.createElement(null, creationType);
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
			command = PartCommandFactory.createCommand(partReq.getPosition(),
					creatingElement, partStack, header);
		}
		return command != null && command.canExecute();
	}

	public void execute() {
		Object dataContext = null;
		Entry entry = null;
		Request request = partReq.getRequest();
		if (request instanceof CreateRequest) {
			Object newObject = ((CreateRequest) request).getNewObject();
			if (newObject instanceof Entry) {
				entry = (Entry) newObject;
			}
		}
		if (entry != null) {
			Initializer initializer = entry.getInitializer();
			if (initializer != null) {
				try {
					if (!initializer.initialize(creatingElement)) {
						return;
					}
				} catch (Exception e) {
					return;
				}
			}
			dataContext = entry.getDataContext();
		}
		if (!promptInitPart(dataContext)) {
			return;
		}
		command.execute();
	}

	private boolean promptInitPart(Object dataContext) {
		if (creatingElement == null || !(creatingElement instanceof MPart)) {
			return false;
		}
		EditDomain editDomain = EditDomain.getEditDomain(partReq
				.getTargetEditPart());
		IFileEditorInput input = (IFileEditorInput) editDomain
				.getData(Designer.DESIGNER_INPUT);
		if (input == null) {
			return false;
		}
		
		NewPartWizard newWizard = new NewPartWizard(input.getFile(),
				(MPart) creatingElement, dataContext);
		WizardDialog dialog = new WizardDialog(new Shell(), newWizard);
		return Window.OK == dialog.open();
	}

	public boolean canUndo() {
		return command != null && command.canExecute();
	}

	public void undo() {
		command.undo();
	}
}
