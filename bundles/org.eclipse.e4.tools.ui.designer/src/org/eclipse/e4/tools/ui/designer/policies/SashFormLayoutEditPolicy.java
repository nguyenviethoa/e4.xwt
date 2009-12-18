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

import org.eclipse.e4.tools.ui.designer.commands.CreateCommand;
import org.eclipse.e4.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashFormLayoutEditPolicy extends FlowLayoutEditPolicy {

	protected Command getCreateCommand(CreateRequest request) {
		EditPart insertAfter = getInsertionReference(request);
		return new CreateCommand(getHost(), request, insertAfter);
	}

	protected Command createAddCommand(EditPart child, EditPart after) {
		return null;
	}

	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return null;
	}

	protected boolean isHorizontal() {
		SashFormEditPart host = (SashFormEditPart) getHost();
		if (host == null) {
			return true;
		}
		MPartSashContainer model = (MPartSashContainer) host.getModel();
		return model.isHorizontal();
	}
}
