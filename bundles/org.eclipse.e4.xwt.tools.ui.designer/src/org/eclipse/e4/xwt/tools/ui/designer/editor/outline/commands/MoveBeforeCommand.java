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

import org.eclipse.e4.xwt.tools.ui.designer.commands.AddNewChildCommand;
import org.eclipse.e4.xwt.tools.ui.designer.commands.DeleteCommand;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class MoveBeforeCommand extends MoveCommand {

	public MoveBeforeCommand(Object source, Object target) {
		super(source, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands.MoveCommand#collectCommands(org.eclipse.gef.commands.CompoundCommand)
	 */
	protected void collectCommands(CompoundCommand command) {
		XamlNode sourceNode = (XamlNode) getSource();
		XamlNode targetNode = (XamlNode) getTarget();
		XamlNode parent = targetNode.getParent();
		int index = parent.getChildNodes().indexOf(targetNode);

		XamlNode newNode = null;
		if (sourceNode.eContainer() != null) {
			newNode = (XamlNode) EcoreUtil.copy(sourceNode);
		} else {
			newNode = sourceNode;
		}

		command.add(new AddNewChildCommand(parent, newNode, index));

		if (sourceNode.eContainer() != null) {
			command.add(new DeleteCommand(sourceNode));
		}
	}

}
