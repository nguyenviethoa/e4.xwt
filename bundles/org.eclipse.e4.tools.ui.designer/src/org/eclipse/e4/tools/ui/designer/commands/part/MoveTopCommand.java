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

import org.eclipse.e4.tools.ui.designer.commands.ApplyAttributeSettingCommand;
import org.eclipse.e4.tools.ui.designer.commands.ChangeParentCommand;
import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
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
public class MoveTopCommand extends AbstractPartCommand {

	public MoveTopCommand(MUIElement model, MPartStack partStack) {
		super(model, partStack);
	}

	protected Command computeCommand() {
		MUIElement selectedPart = model;
		
		if (selectedPart instanceof MPart) {
			MGenericStack<MUIElement> partStack = findParentStack();
			if (partStack != null) {
				selectedPart = partStack;
			}
		}

		MElementContainer<MUIElement> parent = partStack.getParent();
		EList<MUIElement> children = parent.getChildren();
		int index = children.indexOf(partStack);
		if (parent instanceof MGenericTile<?>) {
			MGenericTile<?> genericTile = (MGenericTile<?>) parent;
			int modelIndex = children.indexOf(selectedPart);
			if (modelIndex == 0 && index == 1 && children.size() == 2 && !genericTile.isHorizontal()) {
				return UnexecutableCommand.INSTANCE;
			}
		}

		CompoundCommand result = new CompoundCommand();

		MPartSashContainer newSash = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		String preferData = partStack.getContainerData();
		newSash.setContainerData(preferData);
		newSash.setHorizontal(false);

		if (selectedPart instanceof MPartStack) {
			if (selectedPart.getParent() == null) {
				selectedPart.setContainerData(preferData);
				result.add(CommandFactory.createAddChildCommand(newSash, selectedPart, 0));
			} else {
				result.add(new ChangeParentCommand(newSash, selectedPart, 0));
				if (!preferData.equals(selectedPart.getContainerData())) {
					result.add(new ApplyAttributeSettingCommand(
							(EObject) selectedPart, "containerData", preferData));
				}
			}
		} else if (selectedPart instanceof MPart) {
			MPart part = (MPart) selectedPart;
			MPartStack createPartStack = MApplicationFactory.eINSTANCE
					.createPartStack();
			createPartStack.setContainerData(preferData);
			if (part.getParent() != null) {
				result.add(new ChangeParentCommand(createPartStack, part, 0));
			} else {
				result.add(CommandFactory.createAddChildCommand(createPartStack, part, 0));
			}
			result.add(CommandFactory.createAddChildCommand(newSash, createPartStack, 0));
		}

		result.add(new ChangeParentCommand(newSash, partStack, 1));

		result.add(CommandFactory.createAddChildCommand(parent, newSash, index));
		return result.unwrap();
	}

}
