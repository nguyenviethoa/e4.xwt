/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.sashform;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

public class SashFormChildResizableEditPolicy extends ResizableEditPolicy {

	public SashFormChildResizableEditPolicy() {
	}
	
	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}
}
