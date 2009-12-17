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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreateCommand extends Command {

	private CreateRequest request;
	private EditPart parent;
	private EditPart intsertAfter;

	public CreateCommand(EditPart parent, CreateRequest request,
			EditPart insertAfter) {
		this.parent = parent;
		this.request = request;
		this.intsertAfter = insertAfter;
	}

	public void execute() {
		System.out.println(request.toString());
	}
}
