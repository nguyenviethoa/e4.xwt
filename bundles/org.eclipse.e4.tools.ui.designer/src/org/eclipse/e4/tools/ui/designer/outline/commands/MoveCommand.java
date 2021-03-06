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
package org.eclipse.e4.tools.ui.designer.outline.commands;

import java.util.Iterator;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class MoveCommand extends Command {

	private IStructuredSelection source;
	private MApplicationElement target;

	private Command command;
	
	private int operation;

	public MoveCommand(IStructuredSelection source, MApplicationElement target, int operation) {
		this.setSource(source);
		this.setTarget(target);
		this.setOperation(operation);		
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	protected boolean isMove() {
		return operation == DND.DROP_MOVE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		boolean state = source != null && target != null;
		if (!state) {
			return false;
		}
		
		IStructuredSelection sourceNodes = getSource();
		MUIElement parent = null;
		for (Iterator<?> iterator = sourceNodes.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			MUIElement sourceNode = null;
			if (element instanceof Entry) {
				continue;
			}
			else if (!(element instanceof MUIElement)) {
				return false;
			}
			else {
				sourceNode = (MUIElement) element;
				MUIElement sourceParent = sourceNode.getParent();
				if (sourceParent == null) {
					return false;
				}
				if (parent == null) {
					parent = sourceParent;
				}
				else if (parent != sourceParent) {
					return false;
				}
			}
		}
		
		if (isMove() && sourceNodes.size() == 1 && sourceNodes.getFirstElement() == target) {
			return false;
		}
		return true;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(IStructuredSelection source) {
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public IStructuredSelection getSource() {
		return source;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(MApplicationElement target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public MApplicationElement getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public final void execute() {
		CompoundCommand cmd = new CompoundCommand();
		collectCommands(cmd);
		command = cmd.unwrap();
		if (command.canExecute()) {
			command.execute();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		command.undo();
	}

	protected abstract void collectCommands(CompoundCommand command);
}
