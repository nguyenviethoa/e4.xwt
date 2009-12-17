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
package org.eclipse.e4.tools.ui.designer.parts;

import org.eclipse.e4.tools.ui.designer.policies.ToolBarLayoutEditPolicy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPolicy;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ToolBarEditPart extends CompositeEditPart {

	public ToolBarEditPart(EObject model) {
		super(model);
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ToolBarLayoutEditPolicy());
	}
}
