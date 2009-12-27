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
package org.eclipse.e4.xwt.tools.ui.designer.editor.sash;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.policies.NewResizableEditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 *
 */
public class SashFormChildResizableEditPolicy extends NewResizableEditPolicy {
	static final int WIDTH = 10;
	
	public SashFormChildResizableEditPolicy(int directions,
			boolean displayNonHandles) {
		super(directions, displayNonHandles);
	}

	@Override
	public boolean understandsRequest(Request request) {
		if (request instanceof CreateRequest) {
			GraphicalEditPart editPart = (GraphicalEditPart) getHost();
			IFigure figure = editPart.getFigure();
			Rectangle bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
					
			CreateRequest createRequest = (CreateRequest) request;
			Point location = createRequest.getLocation();
						
			SashFormEditPart sashFormEditPart = (SashFormEditPart) editPart.getParent();
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
