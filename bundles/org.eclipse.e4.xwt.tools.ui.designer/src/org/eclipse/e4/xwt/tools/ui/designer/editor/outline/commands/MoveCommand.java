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
package org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands;

import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class MoveCommand extends Command {

	private Object source;
	private Object target;

	private Command command;

	public MoveCommand(Object source, Object target) {
		this.setSource(source);
		this.setTarget(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return source != null && target != null && source instanceof XamlNode && target instanceof XamlNode && getTarget().getParent() != null;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public XamlNode getSource() {
		return (XamlNode) source;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public XamlNode getTarget() {
		return (XamlNode) target;
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
