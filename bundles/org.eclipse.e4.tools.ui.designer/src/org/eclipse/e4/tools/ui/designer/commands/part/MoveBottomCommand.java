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
package org.eclipse.e4.tools.ui.designer.commands.part;

import org.eclipse.e4.tools.ui.designer.commands.AddChildCommand;
import org.eclipse.e4.tools.ui.designer.commands.ChangeParentCommand;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MoveBottomCommand extends AbstractPartCommand {

	public MoveBottomCommand(MUIElement model, MPartStack partStack) {
		super(model, partStack);
	}

	protected Command computeCommand() {
		CompoundCommand result = new CompoundCommand();
		MElementContainer<MUIElement> parent = partStack.getParent();
		int index = parent.getChildren().indexOf(partStack);

		MPartSashContainer newSash = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		newSash.setHorizontal(false);

		result.add(new ChangeParentCommand(newSash, partStack, 0));

		if (model instanceof MPartStack) {
			if (model.getParent() == null) {
				result.add(new AddChildCommand(newSash, model, 1));
			} else {
				result.add(new ChangeParentCommand(newSash, model, 1));
			}
		} else if (model instanceof MPart) {
			MPart part = (MPart) model;
			MPartStack createPartStack = MApplicationFactory.eINSTANCE
					.createPartStack();
			if (part.getParent() != null) {
				result.add(new ChangeParentCommand(createPartStack, part, 0));
			} else {
				result.add(new AddChildCommand(createPartStack, part, 0));
			}
			result.add(new AddChildCommand(newSash, createPartStack, 1));
		}

		result.add(new AddChildCommand(parent, newSash, index));
		return result.unwrap();
	}

}
