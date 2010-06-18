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
package org.eclipse.e4.tools.ui.designer.commands.part;

import org.eclipse.e4.tools.ui.designer.part.Position;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartCommandFactory {

	public static Command createCommand(Position position, MUIElement model,
			MPartStack partStack, MPart header) {
		if (position == null) {
			return null;
		}
		switch (position) {
		case Bottom:
			return new MoveBottomCommand(model, partStack);
		case Header:
			return new MoveHeaderCommand(model, partStack, header);
		case Left:
			return new MoveLeftCommand(model, partStack);
		case Right:
			return new MoveRightCommand(model, partStack);
		case Top:
			return new MoveTopCommand(model, partStack);
		}
		return null;
	}
}
