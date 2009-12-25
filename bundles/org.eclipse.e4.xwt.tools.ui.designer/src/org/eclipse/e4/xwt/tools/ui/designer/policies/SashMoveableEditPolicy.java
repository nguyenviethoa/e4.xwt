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
package org.eclipse.e4.xwt.tools.ui.designer.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.tools.SelectionHandle;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.parts.misc.DragSashTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashMoveableEditPolicy extends ResizableEditPolicy {

	private SashEditPart editPart;

	public SashMoveableEditPolicy(SashEditPart editPart) {
		this.editPart = editPart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.ResizableEditPolicy#createSelectionHandles()
	 */
	protected List<SelectionHandle> createSelectionHandles() {
		List<SelectionHandle> list = new ArrayList<SelectionHandle>();
		list.add(new SelectionHandle(editPart));
		ResizableHandleKit.moveHandle(editPart, new DragSashTracker(editPart),
				null);
		return list;
	}
	
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		IFigure feedback = getDragSourceFeedbackFigure();
		
		PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds());
		getHostFigure().translateToAbsolute(rect);
		
		Point moveDelta = request.getMoveDelta();
		if (editPart.isHorizontal()) {
			rect.performTranslate(0, moveDelta.y);
		}
		else {
			rect.performTranslate(moveDelta.x, 0);
		}
	
		rect.resize(request.getSizeDelta());
		feedback.translateToRelative(rect);		
		
		GraphicalEditPart graphicalEditPart = (GraphicalEditPart) editPart.getParent();
		IFigure panrertFigure = graphicalEditPart.getFigure();
		PrecisionRectangle bounds = new PrecisionRectangle(panrertFigure.getBounds());
		panrertFigure.translateToAbsolute(bounds);
		
		if (rect.preciseX < bounds.preciseX) {
			rect.preciseX = bounds.preciseX;
		}
		if (rect.preciseX + rect.preciseWidth > bounds.preciseX + bounds.preciseWidth) {
			rect.preciseX = bounds.preciseX + bounds.preciseWidth - rect.preciseWidth;
		}
		
		if (rect.preciseY < bounds.preciseY) {
			rect.preciseY = bounds.preciseY;
		}
		if (rect.preciseY + rect.preciseHeight > bounds.preciseY + bounds.preciseHeight) {
			rect.preciseY = bounds.preciseY + bounds.preciseHeight - rect.preciseHeight;
		}
		
		rect.updateInts();
		feedback.setBounds(rect);
	}
}
