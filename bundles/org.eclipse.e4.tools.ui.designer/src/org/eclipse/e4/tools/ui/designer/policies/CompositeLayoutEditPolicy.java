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

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CompositeLayoutEditPolicy extends XYLayoutEditPolicy {

	protected Command getCreateCommand(CreateRequest request) {
		return CommandFactory.createCreateCommand(request, getHost(), null);
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return CommandFactory.createChangeConstraintCommand(child, constraint);
	}
}
