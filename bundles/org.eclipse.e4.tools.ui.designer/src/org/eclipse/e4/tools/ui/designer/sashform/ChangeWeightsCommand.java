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
package org.eclipse.e4.tools.ui.designer.sashform;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.ApplyAttributeSettingCommand;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class ChangeWeightsCommand extends Command {
	private SashFormEditPart parent;
	private ChangeBoundsRequest request;
	private Integer[] weights;
	private Command command;

	public ChangeWeightsCommand(SashFormEditPart parent,
			ChangeBoundsRequest request) {
		super("Change Weights");
		this.parent = parent;
		this.request = request;
	}

	public boolean canExecute() {
		if (parent == null || request == null || request.getEditParts() == null) {
			return false;
		}

		weights = SashFormUtil.computeWeights(parent, request);
		if (weights == null) {
			return false;
		}
		for (Integer integer : weights) {
			if (integer == null || integer.intValue() < 0) {
				return false;
			}
		}
		return true;
	}

	public void execute() {
		MPartSashContainer parentNode = (MPartSashContainer) parent.getModel();
		List<MPartSashContainerElement> children = parentNode.getChildren();
		CompoundCommand cmd = new CompoundCommand();
		int index = -1;
		for (int i = 0; i < children.size(); i++) {
			MPartSashContainerElement child = children.get(i);
			if (child.getWidget() == null) {
				continue;
			}
			index++;
			if (index >= 0 && index < weights.length) {
				Integer integer = weights[index];
				ApplyAttributeSettingCommand applyCommand = new ApplyAttributeSettingCommand(
						(EObject) child, "containerData", integer.toString());
				if (applyCommand.canExecute()) {
					cmd.add(applyCommand);
				}
			}
		}
		command = cmd.unwrap();
		if (command.canExecute()) {
			command.execute();
		}
	}

	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	public void undo() {
		command.undo();
	}
}
