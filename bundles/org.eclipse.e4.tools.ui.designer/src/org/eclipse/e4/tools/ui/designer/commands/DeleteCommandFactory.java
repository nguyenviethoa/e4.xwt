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

import org.eclipse.e4.ui.model.application.MMenu;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.gef.commands.Command;

public class DeleteCommandFactory {

	static public Command createDeleteCommand(Object element) {
		if (element instanceof MMenu && ((MMenu)element).getParent() == null) {
			return new MenuDeleteCommand((MMenu) element);
		}
		else if (element instanceof MUIElement) {
			return new DeleteCommand((MUIElement) element);
		}
		throw new UnsupportedOperationException(element.getClass().getName());
	}
}
