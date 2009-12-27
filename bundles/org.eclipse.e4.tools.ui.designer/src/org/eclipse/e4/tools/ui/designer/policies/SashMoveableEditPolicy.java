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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.e4.tools.ui.designer.parts.SashEditPart;
import org.eclipse.e4.tools.ui.designer.parts.handlers.DragSashTracker;
import org.eclipse.e4.tools.ui.designer.sashform.SashFormEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.tools.SelectionHandle;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.SashUtil;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 * @author Yves YANG (yves.yang@soyatec.com)
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
	protected List<Handle> createSelectionHandles() {
		List<Handle> list = new ArrayList<Handle>();
		list.add(new SelectionHandle(editPart));
		Handle moveHandle = ResizableHandleKit.moveHandle(editPart,
				new DragSashTracker(editPart), null);
		list.add(moveHandle);
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
		
		SashFormEditPart sashFormEditPart = (SashFormEditPart) editPart.getParent();
		List children = sashFormEditPart.getChildren();
		SashForm sashForm = (SashForm) sashFormEditPart.getWidget();
		int[] weights = sashForm.getWeights();
		GraphicalEditPart previous = null;
		int previousIndex = 0;
		GraphicalEditPart next = null;
		int nextIndex = 0;
		int i = 0;
		for (Object object : children) {
			if (!(object instanceof SashEditPart)){
				if (object instanceof GraphicalEditPart) {
					if (next == null) {
						previous = (GraphicalEditPart) object;
						previousIndex = i;
					}
					else if (next == editPart) {
						next = (GraphicalEditPart) object;
						nextIndex = i;
					}					
				}
				i++;
			}
			else if (object == editPart){
				next = editPart;
			}
		}
		int total = weights[previousIndex] + weights[nextIndex];

		PrecisionRectangle previousBounds = null;
		{
			IFigure figure = previous.getFigure();
			previousBounds = new PrecisionRectangle(figure.getBounds());
			figure.translateToAbsolute(previousBounds);
		}
		PrecisionRectangle nextBounds = null;
		{
			IFigure figure = next.getFigure();
			nextBounds = new PrecisionRectangle(figure.getBounds());
			figure.translateToAbsolute(nextBounds);
		}
		
		if (rect.x < previousBounds.x) {
			rect.x = previousBounds.x;
		}
		if (rect.x + rect.width > (previousBounds.x + previousBounds.width + nextBounds.width)) {
			rect.x = (previousBounds.x + previousBounds.width + nextBounds.width) - rect.width;
		}
		
		if (rect.y < previousBounds.y) {
			rect.y = previousBounds.y;
		}
		if (rect.y + rect.height > (previousBounds.y + previousBounds.height + nextBounds.height)) {
			rect.y = (previousBounds.y + previousBounds.height + nextBounds.height) - rect.height;
		}
		feedback.translateToRelative(rect);

		feedback.setBounds(rect);
		
		if (editPart.isHorizontal()) {
			int previousWeight = (int)((rect.y - previousBounds.y) * total / (previousBounds.height + nextBounds.height - rect.height));
			weights[previousIndex] = previousWeight;
			weights[nextIndex] = total - previousWeight;
			
			label.setText(SashUtil.weightsDisplayString(weights));
			Dimension dimension = label.getPreferredSize();
			label.setSize(dimension);
			location.x = (int)rect.x + (rect.width - dimension.width)/2;
			location.y = (int)rect.y + 10;
		}
		else {
			int previousWeight = (int)((rect.x - previousBounds.x) * total / (previousBounds.width + nextBounds.width - rect.width));
			weights[previousIndex] = previousWeight;
			weights[nextIndex] = total - previousWeight;
			label.setText(SashUtil.weightsDisplayString(weights));
			Dimension dimension = label.getPreferredSize();
			label.setSize(dimension);		
			location.x = (int)rect.x + 10;
			location.y = (int)rect.y + (rect.height - dimension.height)/2;
		}
		label.setLocation(location);
	}
}
