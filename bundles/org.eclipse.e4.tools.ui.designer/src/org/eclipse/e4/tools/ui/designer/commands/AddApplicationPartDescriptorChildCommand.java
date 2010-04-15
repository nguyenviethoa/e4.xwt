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

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AddApplicationPartDescriptorChildCommand extends Command {

	protected MApplication parent;
	protected MPartDescriptor newChild;
	protected int index;

	public AddApplicationPartDescriptorChildCommand(MApplication parent, MPartDescriptor newChild,
			int index) {
		this.parent = parent;
		this.newChild = newChild;
		this.index = index;
	}

	public boolean canExecute() {
		return parent != null && newChild != null;
	}

	public void execute() {
		if (index < 0 || index > parent.getChildren().size()) {
			index = parent.getDescriptors().size();
		}
		EList<MPartDescriptor> commands = (EList<MPartDescriptor>) parent.getDescriptors();
		commands.add(index, newChild);
	}

	public boolean canUndo() {
		return parent != null;
	}

	public void undo() {
		parent.getDescriptors().remove(newChild);
	}
}
