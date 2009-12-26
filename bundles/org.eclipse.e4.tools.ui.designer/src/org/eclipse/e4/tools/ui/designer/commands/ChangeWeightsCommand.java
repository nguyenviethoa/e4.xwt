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
package org.eclipse.e4.tools.ui.designer.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.e4.tools.ui.designer.parts.SashFormEditPart;
import org.eclipse.e4.ui.model.application.MPSCElement;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class ChangeWeightsCommand extends Command {
	private SashFormEditPart parent;
	private ChangeBoundsRequest request;
	private Integer[] weights;
	private Integer[] oldWeights;

	public ChangeWeightsCommand(SashFormEditPart parent,
			ChangeBoundsRequest request) {
		super("Change Weights");
		this.parent = parent;
		this.request = request;
	}

	public boolean canExecute() {
		if (parent == null || request == null || request.getEditParts() == null) {
			return false;
		}
		if (!(parent instanceof VisualEditPart)) {
			return false;
		}
		IVisualInfo visualInfo = ((VisualEditPart) parent).getVisualInfo();
		SashForm sashForm = (SashForm) visualInfo.getVisualObject();
		weights = computeWeights(sashForm);
		if (weights == null) {
			return false;
		}
		for (Integer integer : weights) {
			if (integer == null || integer.intValue() < 0) {
				return false;
			}
		}
		return true;
	}

	public void execute() {
		MPartSashContainer parentNode = (MPartSashContainer) parent.getModel();
		EList<Integer> widgetList = parentNode.getWeights();
		oldWeights = new Integer[widgetList.size()];
		for (int i = 0; i < oldWeights.length; i++) {
			oldWeights[i] = widgetList.get(i);
		}		
		widgetList.clear();

		for (int i = 0; i < weights.length; i++) {
			widgetList.add(weights[i]);
		}
	}

	public boolean canUndo() {
		return oldWeights != null && oldWeights.length > 0;
	}

	public void undo() {
		MPartSashContainer parentNode = (MPartSashContainer) parent.getModel();
		EList<Integer> widgetList = parentNode.getWeights();
		widgetList.clear();
		for (int i = 0; i < oldWeights.length; i++) {
			widgetList.add(oldWeights[i]);
		}
		oldWeights = null;
	}

	private VisualEditPart getEditPart() {
		List<VisualEditPart> editParts = new ArrayList<VisualEditPart>(request
				.getEditParts());
		if (editParts.size() > 0) {
			return editParts.get(0);
		}
		return null;
	}

	private Integer[] computeWeights(SashForm sashForm) {

		int[] weights = sashForm.getWeights();
		int[] newWeights = weights;

		VisualEditPart editPart = getEditPart();
		if (editPart != null) {
			MPartSashContainer parentNode = (MPartSashContainer) parent
					.getModel();
			EList<MPSCElement> children = parentNode.getChildren();
			Object visualObject = editPart.getVisualInfo().getVisualObject();
			if (visualObject instanceof Sash) {
				Sash sash = (Sash) visualObject;
				int sashIndex = getSashIndex(sashForm, sash);
				org.eclipse.draw2d.geometry.Point moveDelta = request
						.getMoveDelta();
				boolean horizontal = (sash.getStyle() & SWT.VERTICAL) != 0;
				Control[] controls = getControls(sashForm);
				if (controls != null) {
					int resizeOffset = getResizeOffset(controls[sashIndex],
							newWeights, sashIndex, moveDelta.x, moveDelta.y,
							horizontal);
					newWeights[sashIndex] += resizeOffset;
					newWeights[sashIndex + 1] -= resizeOffset;
				}
			} else {
				int index = -1;
				for (int i = 0; i < children.size(); i++) {
					Object widget = children.get(i).getWidget();
					if (widget != null) {
						index++;
					}
					if (visualObject.equals(widget)) {
						break;
					}
				}
				if (index != -1) {
					int resizeDirection = request.getResizeDirection();
					Dimension sizeDelta = request.getSizeDelta();
					int offset = getResizeOffset(
							(Control) visualObject,
							newWeights,
							index,
							sizeDelta.width,
							sizeDelta.height,
							resizeDirection == PositionConstants.EAST
									|| resizeDirection == PositionConstants.WEST);
					for (int i = 0; i < newWeights.length; i++) {
						if (i == index) {
							newWeights[i] += offset;
						} else {
							newWeights[i] -= offset / (newWeights.length - 1);
						}
					}
				}
			}
		}
		List<Integer> ws = new ArrayList<Integer>();
		for (int i = 0; i < newWeights.length; i++) {
			ws.add(newWeights[i]);
		}
		return ws.toArray(new Integer[ws.size()]);
	}

	private int getSashIndex(SashForm sashForm, Sash sash) {
		try {
			Field field = SashForm.class.getDeclaredField("sashes");
			field.setAccessible(true);
			Sash[] sashes = (Sash[]) field.get(sashForm);
			for (int i = 0; i < sashes.length; i++) {
				if (sashes[i].equals(sash)) {
					return i;
				}
			}
		} catch (Exception e) {
		}
		return -1;
	}

	private Control[] getControls(SashForm sashForm) {
		try {
			Field field = SashForm.class.getDeclaredField("controls");
			field.setAccessible(true);
			return (Control[]) field.get(sashForm);
		} catch (Exception e) {
		}
		return null;
	}

	private int getResizeOffset(Control control, int[] weights, int index,
			int x, int y, boolean horizontal) {
		float total = 0;
		for (int i : weights) {
			total += i;
		}

		Point size = SWTTools.getSize(control);
		// int resizeDirection = request.getResizeDirection();
		// Dimension sizeDelta = request.getSizeDelta();
		if (horizontal) {
			float percent = weights[index] / total;
			float width = (size.x / percent);
			float newPercent = (size.x + x) / width;
			int newWeight = (int) (total * newPercent);
			return newWeight - weights[index];
		} else {
			float percent = weights[index] / total;
			float height = (size.y / percent);
			float newPercent = (size.y + y) / height;
			int newWeight = (int) (total * newPercent);
			return newWeight - weights[index];
		}
	}
}
