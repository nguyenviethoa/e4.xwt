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

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.emf.ecore.EObject;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class DeleteCommand extends AbstractDeleteCommand {

	private MUIElement eObject;
	private MElementContainer<MUIElement> container;
	private int index = -1;

	public DeleteCommand(MUIElement eObject) {
		super((EObject)eObject);
		this.eObject = eObject;
	}

	public void doExecute() {
		container = eObject.getParent();
		index = container.getChildren().indexOf(eObject);
		container.getChildren().remove(eObject);
	}

	public boolean canUndo() {
		return container != null;
	}

	public void doUndo() {
		if (index >= container.getChildren().size()) {
			container.getChildren().add(eObject);
		} else {
			container.getChildren().add(index, eObject);
		}
	}
}
