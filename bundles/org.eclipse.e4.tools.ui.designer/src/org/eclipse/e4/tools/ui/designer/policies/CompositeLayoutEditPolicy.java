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
package org.eclipse.e4.tools.ui.designer.policies;

import org.eclipse.e4.tools.ui.designer.commands.factory.CommandsFactory;
import org.eclipse.e4.tools.ui.designer.commands.factory.CompositeCommandsFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CompositeLayoutEditPolicy extends XYLayoutEditPolicy {

	private CommandsFactory factory;

	public void activate() {
		super.activate();
		if (factory == null) {
			factory = new CompositeCommandsFactory(getHost());
		}
	}

	protected Command getCreateCommand(CreateRequest request) {
		return factory.getCreateCommand(request, null);
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return factory.getChangeConstraintCommand(child, constraint);
	}

}
