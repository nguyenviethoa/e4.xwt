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

import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MMenu;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.gef.commands.Command;

public class CommandFactory {

	static public Command createDeleteCommand(Object element) {
		if (element instanceof MMenu && ((MMenu) element).getParent() == null) {
			return new MenuDeleteCommand((MMenu) element);
		} else if (element instanceof MUIElement) {
			return new DeleteCommand((MUIElement) element);
		}
		throw new UnsupportedOperationException(element.getClass().getName());
	}

	static public Command createAddChildCommand(Object container, Object child,
			int index) {
		if (child instanceof MMenu && container instanceof MWindow) {
			return new AddMenuChildCommand((MWindow) container, (MMenu) child);
		} else if (container instanceof MElementContainer
				&& child instanceof MUIElement) {
			return new AddChildCommand(
					(MElementContainer<MUIElement>) container,
					(MUIElement) child, index);
		}
		throw new UnsupportedOperationException(container.getClass().getName()
				+ " " + child.getClass().getName());
	}
}
