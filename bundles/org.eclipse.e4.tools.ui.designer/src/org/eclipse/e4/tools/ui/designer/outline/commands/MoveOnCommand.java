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
import org.eclipse.e4.tools.ui.designer.palette.E4PaletteHelper;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class MoveOnCommand extends MoveCommand {

	public MoveOnCommand(IStructuredSelection source, MUIElement target,
			int operation) {
		super(source, target, operation);
	}

	@Override
	public boolean canExecute() {
		boolean canExecute = super.canExecute();
		if (!canExecute) {
			return false;
		}

		IStructuredSelection sourceNodes = getSource();
		MUIElement target = getTarget();
		for (Iterator<?> iterator = sourceNodes.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			MUIElement sourceNode = null;
			if (element instanceof Entry) {
				Entry entry = (Entry) element;
				if (!ApplicationModelHelper.canAddedChild(entry, target)) {
					return false;
				}
				continue;
			} else if (!(element instanceof MUIElement)) {
				return false;
			} else {
				sourceNode = (MUIElement) element;
				if (!ApplicationModelHelper.canAddedChild(sourceNode, target)) {
					return false;
				}
				MElementContainer<MUIElement> sourceParent = sourceNode
						.getParent();
				if (sourceParent == null) {
					return false;
				}
				if (sourceParent == getTarget()) {
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
	 * org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands.MoveAfterCommand
	 * #collectCommands(org.eclipse.gef.commands.CompoundCommand)
	 */
	protected void collectCommands(CompoundCommand command) {
		IStructuredSelection sourceNodes = getSource();
		MUIElement target = getTarget();
		for (Iterator<?> iterator = sourceNodes.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			MUIElement sourceNode = null;
			if (element instanceof Entry && target instanceof MElementContainer) {
				sourceNode = E4PaletteHelper
						.createElement((MElementContainer<MUIElement>) target,
								(Entry) element);
			} else {
				sourceNode = (MUIElement) element;
			}
			if (target instanceof MElementContainer) {
				MUIElement newChild = null;
				if (sourceNode.getParent() != null) {
					newChild = (MUIElement) EcoreUtil
							.copy((EObject) sourceNode);
				} else {
					newChild = sourceNode;
				}
				command.add(CommandFactory.createAddChildCommand(target,
						newChild, -1));
				if (isMove() && sourceNode.getParent() != null) {
					command.add(new DeleteCommand(sourceNode));
				}
			}
		}
	}
}
