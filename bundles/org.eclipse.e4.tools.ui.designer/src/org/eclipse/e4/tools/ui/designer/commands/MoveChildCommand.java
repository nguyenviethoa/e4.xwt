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

import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.workbench.modeling.EModelService;
import org.eclipse.e4.workbench.modeling.ModelService;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

/**
 * 
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class MoveChildCommand extends Command {

	private EditPart child;
	private EditPart after;

	private int oldPosition = -1;
	private int newPosition = -1;
	private MElementContainer<MUIElement> parentNode;
	private MUIElement childNode;
	private EModelService modelService;

	public MoveChildCommand(EditPart child, EditPart after) {
		super("Move Child Command");
		this.child = child;
		this.after = after;
		
		
	}

	public boolean canExecute() {
		if (child == null || child == after) {
			return false;
		}
		if (after != null) {
			Object model = after.getModel();
			if (model instanceof MUIElement) {
				parentNode = ((MUIElement) model).getParent();
			}
		}
		if (parentNode == null) {
			Object model = child.getModel();
			if (model instanceof MUIElement) {
				parentNode = ((MUIElement) model).getParent();
			}
		}
		if (parentNode == null) {
			return false;
		}
		
		MWindow window = ApplicationModelHelper.findMWindow(parentNode);
		if (window == null) {
			return false;
		}
		IEclipseContext context = window.getContext();
		if (context == null) {
			return false;
		}
		Object value = context.get(EModelService.class.getName());
		if (value == null || !(value instanceof EModelService)) {
			return false;
		}
		modelService = (EModelService) value;
		if (modelService == null) {
			return false;
		}
		
		oldPosition = parentNode.getChildren().indexOf(child.getModel());
		if (after != null) {
			newPosition = parentNode.getChildren().indexOf(after.getModel());
		} else {
			newPosition = parentNode.getChildren().size() - 1;
		}
		if (newPosition > oldPosition) {
			newPosition--;
		}
		// return true;
		return newPosition != -1 && oldPosition != -1
				&& oldPosition != newPosition;
	}

	public void execute() {
		List<MUIElement> children = parentNode.getChildren();
		MUIElement child = children.get(oldPosition);
		modelService.move(child, parentNode, newPosition);
	}

	public boolean canUndo() {
		return parentNode != null && newPosition != -1 && oldPosition != -1
				&& newPosition != oldPosition;
	}

	public void undo() {
		List<MUIElement> children = parentNode.getChildren();
		MUIElement child = children.get(newPosition);
		modelService.move(child, parentNode, oldPosition);
	}
}
