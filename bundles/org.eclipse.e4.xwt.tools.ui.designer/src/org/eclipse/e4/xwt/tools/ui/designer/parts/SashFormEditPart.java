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
package org.eclipse.e4.xwt.tools.ui.designer.parts;

import org.eclipse.e4.xwt.tools.ui.designer.policies.SashFormLayoutEditPolicy;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.custom.SashForm;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class SashFormEditPart extends CompositeEditPart {

	public SashFormEditPart(SashForm composite, XamlNode model) {
		super(composite, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.parts.CompositeEditPart#
	 * createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new SashFormLayoutEditPolicy());
	}
}
