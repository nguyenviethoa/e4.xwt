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
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.tools.ui.designer.policies.CompositeLayoutEditPolicy;
import org.eclipse.e4.tools.ui.designer.sashform.SashFormEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.CompositeInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class CompositeEditPart extends ControlEditPart {
	static final int WIDTH = 20;

	public CompositeEditPart(EObject model) {
		super(model);
	}

	protected IVisualInfo createVisualInfo() {
		Object widget = getMuiElement().getWidget();
		return new CompositeInfo(widget, isRoot());
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.LAYOUT_ROLE);
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new CompositeLayoutEditPolicy());
	}
	
	@Override
	public boolean understandsRequest(Request request) {
		if (request instanceof CreateRequest) {
			IFigure figure = getFigure();
			Rectangle bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
					
			CreateRequest createRequest = (CreateRequest) request;
			Point location = createRequest.getLocation();
						
			SashFormEditPart sashFormEditPart = (SashFormEditPart) getParent();
			if (sashFormEditPart.isHorizontal()) {
				if (location.x <= bounds.x + WIDTH || location.x > bounds.x + bounds.width - WIDTH) {
					return false;
				}
			}
			else {
				if (location.y <= bounds.y + WIDTH || location.y > bounds.y + bounds.height - WIDTH) {
					return false;
				}
			}
		}		
		return super.understandsRequest(request);
	}
}
