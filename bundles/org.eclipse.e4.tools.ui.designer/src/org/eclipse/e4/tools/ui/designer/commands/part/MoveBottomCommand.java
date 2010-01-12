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
import org.eclipse.e4.tools.ui.designer.commands.ApplyAttributeSettingCommand;
import org.eclipse.e4.tools.ui.designer.commands.ChangeParentCommand;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MGenericStack;
import org.eclipse.e4.ui.model.application.MGenericTile;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class MoveBottomCommand extends AbstractPartCommand {

	public MoveBottomCommand(MUIElement model, MPartStack partStack) {
		super(model, partStack);
	}

	protected Command computeCommand() {
		MElementContainer<MUIElement> parent = partStack.getParent();
		EList<MUIElement> children = parent.getChildren();
		int index = children.indexOf(partStack);
		if (parent instanceof MGenericTile<?>) {
			MGenericTile<?> genericTile = (MGenericTile<?>) parent;
			int modelIndex = children.indexOf(model);
			if (modelIndex == -1) {
				MGenericStack<MUIElement> partStack = findParentStack();
				modelIndex = children.indexOf(partStack);
			}
			if (index == 0 && modelIndex == 1 && children.size() == 2 && !genericTile.isHorizontal()) {
				return UnexecutableCommand.INSTANCE;
			}
		}

		CompoundCommand result = new CompoundCommand();

		MPartSashContainer newSash = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		newSash.setHorizontal(false);
		String preferData = partStack.getContainerData();
		newSash.setContainerData(preferData);

		result.add(new ChangeParentCommand(newSash, partStack, 0));

		if (model instanceof MPartStack) {
			if (model.getParent() == null) {
				model.setContainerData(preferData);
				result.add(new AddChildCommand(newSash, model, 1));
			} else {
				result.add(new ChangeParentCommand(newSash, model, 1));
				if (!preferData.equals(model.getContainerData())) {
					result.add(new ApplyAttributeSettingCommand(
							(EObject) model, "containerData", preferData));
				}
			}
		} else if (model instanceof MPart) {
			MPart part = (MPart) model;
			MPartStack createPartStack = MApplicationFactory.eINSTANCE
					.createPartStack();
			createPartStack.setContainerData(preferData);
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
