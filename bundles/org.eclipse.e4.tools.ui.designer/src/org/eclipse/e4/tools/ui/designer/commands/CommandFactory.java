/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.Collection;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.CreateRequest;

public class CommandFactory {

	static public Command createDeleteCommand(Object element) {
		if (element instanceof EditPart) {
			element = ((EditPart) element).getModel();
		}
		if (element instanceof Collection) {
			Collection<?> elements = (Collection<?>) element;
			CompoundCommand command = new CompoundCommand();
			for (Object object : elements) {
				command.add(createDeleteCommand(object));
			}
			return command;
		}
		if (element instanceof MMenu && ((MMenu) element).getParent() == null) {
			return new MenuDeleteCommand((MMenu) element);
		} else if (element instanceof MUIElement) {
			return new DeleteCommand((MUIElement) element);
		}
		throw new UnsupportedOperationException(element.getClass().getName());
	}

	static public Command createAddChildCommand(Object container, Object child,
			int index) {
		if (container instanceof EObject
				&& child instanceof EObject
				&& ApplicationModelHelper.canAddedChild((EObject) container,
						(EObject) child)) {
			return new AddChildCommand((EObject) container,
					(EObject) child, -1);			
		}
		return UnexecutableCommand.INSTANCE;
	}

	static public Command createCreateCommand(CreateRequest request,
			EditPart parent, EditPart insertAfter) {
		int index = -1;
		if (insertAfter != null) {
			index = parent.getChildren().indexOf(insertAfter);
		}
		return createCreateCommand(request, parent, index);
	}

	static public Command createCreateCommand(CreateRequest req,
			EditPart parent, int index) {
		return new CreateCommand(req, parent, index);
	}

	static public Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return new ChangeConstraintCommand(child, constraint);
	}
}
