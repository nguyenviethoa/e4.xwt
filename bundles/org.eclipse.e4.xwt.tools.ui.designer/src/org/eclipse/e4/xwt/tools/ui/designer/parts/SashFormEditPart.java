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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.xwt.tools.ui.designer.policies.SashFormLayoutEditPolicy;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.parts.CompositeEditPart#refresh()
	 */
	public void refresh() {
		super.refresh();
		EditPolicy layoutPolicy = getEditPolicy(EditPolicy.LAYOUT_ROLE);
		if (layoutPolicy != null) {
			layoutPolicy.deactivate();
			layoutPolicy.activate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.parts.ControlEditPart#getExternalModels
	 * ()
	 */
	protected Collection<Object> getExternalModels() {
		Collection<Object> externalModels = new ArrayList<Object>(super
				.getExternalModels());
		SashForm sashForm = (SashForm) getWidget();
		if (sashForm != null && !sashForm.isDisposed()) {
			Control[] children = sashForm.getChildren();
			for (Control control : children) {
				if (control instanceof Sash) {
					externalModels.add(control);
				}
			}
		}
		return externalModels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractEditPart#createChild(java.lang.Object)
	 */
	protected EditPart createChild(Object model) {
		if (model instanceof Sash) {
			return new SashEditPart((Sash) model, null);
		}
		return super.createChild(model);
	}
}
