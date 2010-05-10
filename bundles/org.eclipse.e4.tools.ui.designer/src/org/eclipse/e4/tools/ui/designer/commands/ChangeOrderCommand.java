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
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ChangeOrderCommand extends Command {

	private MElementContainer<MUIElement> conatiner;
	private MUIElement element;
	private int newPosition;
	private int oldPosition;

	public ChangeOrderCommand(MElementContainer<MUIElement> conatiner,
			MUIElement element, int index) {
		this.conatiner = conatiner;
		this.element = element;
		this.newPosition = index;
	}

	public boolean canExecute() {
		if (element == null) {
			return false;
		}
		if (conatiner == null) {
			conatiner = element.getParent();
		}
		List<MUIElement> children = conatiner.getChildren();
		oldPosition = children.indexOf(element);
		return newPosition >= 0 && newPosition < children.size()
				&& newPosition != oldPosition;
	}

	public void execute() {
		move(oldPosition, newPosition);
	}

	private void move(int oldPosition, int newPosition) {
		List<MUIElement> children = conatiner.getChildren();
		if (children.remove(element)) {
			children.add(newPosition, element);
		}
	}

	public void undo() {
		move(newPosition, oldPosition);
	}

}
