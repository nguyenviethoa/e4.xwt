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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.tools.SelectionHandle;
import org.eclipse.e4.xwt.tools.ui.designer.parts.SashEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.parts.misc.DragSashTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SashMoveableEditPolicy extends ResizableEditPolicy {

	private SashEditPart editPart;
	private Label label;
	private Point location = new Point();

	public SashMoveableEditPolicy(SashEditPart editPart) {
		this.editPart = editPart;
	}
	
	/**
	 * Creates the figure used for feedback.
	 * @return the new feedback figure
	 */
	protected IFigure createDragSourceFeedbackFigure() {
		label = new Label();
		label.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		getFeedbackLayer().add(label);
		return super.createDragSourceFeedbackFigure();
	}
	
	@Override
	protected void eraseChangeBoundsFeedback(ChangeBoundsRequest request) {
		super.eraseChangeBoundsFeedback(request);
		if (label != null) {
			removeFeedback(label);
		}
		label = null;
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
		
		GraphicalEditPart graphicalEditPart = (GraphicalEditPart) editPart.getParent();
		IFigure parentFigure = graphicalEditPart.getFigure();
		PrecisionRectangle bounds = new PrecisionRectangle(parentFigure.getBounds());
		parentFigure.translateToAbsolute(bounds);
		
		if (rect.x < bounds.x) {
			rect.x = bounds.x;
		}
		if (rect.x + rect.width > bounds.x + bounds.width) {
			rect.x = bounds.x + bounds.width - rect.width;
		}
		
		if (rect.y < bounds.y) {
			rect.y = bounds.y;
		}
		if (rect.y + rect.height > bounds.y + bounds.height) {
			rect.y = bounds.y + bounds.height - rect.height;
		}
		feedback.translateToRelative(rect);

		feedback.setBounds(rect);
		
		if (editPart.isHorizontal()) {
			int weight = (int)((rect.y - bounds.y) * 100 / bounds.height);
			label.setText("[" + weight + ", " + (100 - weight) + "]");
			Dimension dimension = label.getPreferredSize();
			label.setSize(dimension);
			location.x = (int)rect.x + (rect.width - dimension.width)/2;
			location.y = (int)rect.y + 10;
		}
		else {
			int weight = (int)((rect.x - bounds.x) * 100 / bounds.width);
			label.setText("[" + weight + ", " + (100 - weight) + "]");
			Dimension dimension = label.getPreferredSize();
			label.setSize(dimension);		
			location.x = (int)rect.x + 10;
			location.y = (int)rect.y + (rect.height - dimension.height)/2;
		}
		label.setLocation(location);
	}
}
