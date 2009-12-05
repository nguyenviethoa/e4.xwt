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

import java.util.Iterator;

import org.eclipse.e4.xwt.tools.ui.designer.commands.AddNewChildCommand;
import org.eclipse.e4.xwt.tools.ui.designer.commands.DeleteCommand;
import org.eclipse.e4.xwt.tools.ui.designer.editor.palette.CreateReqHelper;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class MoveOnCommand extends MoveAfterCommand {

	public MoveOnCommand(IStructuredSelection source, Object target, int operation) {
		super(source, target, operation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands.MoveAfterCommand#collectCommands(org.eclipse.gef.commands.CompoundCommand)
	 */
	protected void collectCommands(CompoundCommand command) {
		IStructuredSelection sourceNodes = getSource();
		XamlNode target = getTarget();
		for (Iterator iterator = sourceNodes.iterator(); iterator.hasNext();) {
			XamlNode sourceNode = (XamlNode) iterator.next();
			if (CreateReqHelper.canCreate(target, sourceNode)) {
				XamlNode newChild = null;
				if (sourceNode.eContainer() != null) {
					newChild = (XamlNode) EcoreUtil.copy(sourceNode);
				} else {
					newChild = sourceNode;
				}
				command.add(new AddNewChildCommand(target, newChild));
				if (isMove() && sourceNode.eContainer() != null) {
					command.add(new DeleteCommand(sourceNode));
				}
			} 
		}
	}
}
