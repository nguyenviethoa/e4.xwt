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
package org.eclipse.e4.tools.ui.designer.editparts;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.policies.DiagramLayoutEditPolicy;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.impl.ApplicationImpl;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.AbstractDiagramEditPart;
import org.eclipse.gef.EditPolicy;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class DiagramEditPart extends AbstractDiagramEditPart {

	public DiagramEditPart(ApplicationImpl documentRoot) {
		super(documentRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		return ((MApplication) getModel()).getChildren();
		// return super.getModelChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#toString()
	 */
	public String toString() {
		return "";
	}
}
