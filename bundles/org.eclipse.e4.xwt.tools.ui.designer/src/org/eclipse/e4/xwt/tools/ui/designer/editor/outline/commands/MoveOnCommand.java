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
import org.eclipse.e4.xwt.tools.ui.designer.editor.palette.CreateReqHelper;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class MoveOnCommand extends MoveAfterCommand {

	public MoveOnCommand(Object source, Object target) {
		super(source, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.editor.outline.commands.MoveAfterCommand#collectCommands(org.eclipse.gef.commands.CompoundCommand)
	 */
	protected void collectCommands(CompoundCommand command) {
		XamlNode source = getSource();
		XamlNode target = getTarget();
		if (CreateReqHelper.canCreate(target, source)) {
			XamlNode newChild = null;
			if (source.eContainer() != null) {
				newChild = (XamlNode) EcoreUtil.copy(source);
			} else {
				newChild = source;
			}
			command.add(new AddNewChildCommand(target, newChild));
			if (source.eContainer() != null) {
				command.add(new DeleteCommand(source));
			}
		} else {
			super.collectCommands(command);
		}
	}

}
