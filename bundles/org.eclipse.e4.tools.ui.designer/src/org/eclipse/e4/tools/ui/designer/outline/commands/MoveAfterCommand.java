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
package org.eclipse.e4.tools.ui.designer.outline.commands;

import java.util.Iterator;

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.commands.DeleteCommand;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.tools.EntryHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class MoveAfterCommand extends MoveCommand {

	public MoveAfterCommand(IStructuredSelection source, MUIElement target,
			int operation) {
		super(source, target, operation);
		setLabel("Move After");
	}

	@Override
	public boolean canExecute() {
		if (!super.canExecute() || ((EObject) getTarget()).eContainer() == null) {
			return false;
		}

		EObject target = ((EObject) getTarget()).eContainer();
		for (Iterator<?> iterator = getSource().iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			if (element instanceof Entry) {
				if (!ApplicationModelHelper.canAddedChild(target, ((Entry) element)
						.getType())) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands.MoveCommand
	 * #collectCommands(org.eclipse.gef.commands.CompoundCommand)
	 */
	protected void collectCommands(CompoundCommand command) {
		IStructuredSelection sourceNodes = getSource();
		MApplicationElement targetNode = getTarget();
		EObject parent = ((EObject) getTarget()).eContainer();
		int index = ApplicationModelHelper.getChildIndex(parent, targetNode);
		MApplicationElement newNode = null;

		for (Iterator<?> iterator = sourceNodes.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			Object sourceNode = null;
			if (element instanceof Entry) {
				sourceNode = EntryHelper.getNewObject((Entry) element);
			} else {
				sourceNode = (MUIElement) element;
			}
			if (sourceNode == null) {
				continue;
			}

			if (ApplicationModelHelper.isLive(sourceNode)) {
				newNode = (MApplicationElement) EcoreUtil
						.copy((EObject) sourceNode);
				newNode.setElementId(EcoreUtil.generateUUID());
				if (newNode instanceof MUIElement) {
					MUIElement uiElement = (MUIElement) newNode;
					uiElement.setWidget(null);
				}
			} else if (sourceNode instanceof MApplicationElement) {
				newNode = (MApplicationElement) sourceNode;
			}
			command.add(CommandFactory.createAddChildCommand(parent, newNode,
					++index));
			if (isMove() && ApplicationModelHelper.isLive(sourceNode)
					&& sourceNode instanceof MUIElement) {
				command.add(new DeleteCommand((MUIElement) sourceNode));
			}
		}
	}
}
