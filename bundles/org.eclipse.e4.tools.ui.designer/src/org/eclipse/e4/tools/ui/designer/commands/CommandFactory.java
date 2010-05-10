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

import java.util.Collection;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
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
		if (container instanceof MApplication) {
			if (child instanceof MCommand) {
				return new AddApplicationCommandChildCommand(
						(MApplication) container, (MCommand) child, -1);
			} else if (child instanceof MKeyBinding) {
				return new AddApplicationKeyBindingChildCommand(
						(MApplication) container, (MKeyBinding) child, -1);
			} else if (child instanceof MHandler) {
				return new AddApplicationHandlerChildCommand(
						(MApplication) container, (MHandler) child, -1);
			} else if (child instanceof MPartDescriptor) {
				return new AddApplicationPartDescriptorChildCommand(
						(MApplication) container, (MPartDescriptor) child, -1);
			} else if (child instanceof MUIElement) {
				return new AddChildCommand((MApplication) container,
						(MUIElement) child, -1);
			}
		} else if (child instanceof MMenu && container instanceof MWindow) {
			return new AddWindowMenuChildCommand((MWindow) container,
					(MMenu) child);
		} else if (container instanceof MElementContainer
				&& child instanceof MUIElement) {
			return new AddChildCommand(
					(MElementContainer<MUIElement>) container,
					(MUIElement) child, index);
		} else if (container instanceof MPart && child instanceof MMenu) {
			return new AddPartMenuChildCommand((MPart) container, (MMenu) child);
		}
		// throw new
		// UnsupportedOperationException(container.getClass().getName()
		// + " " + child.getClass().getName());
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
