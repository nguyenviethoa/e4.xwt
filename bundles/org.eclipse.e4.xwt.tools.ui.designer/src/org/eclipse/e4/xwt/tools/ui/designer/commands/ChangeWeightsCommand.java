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
package org.eclipse.e4.xwt.tools.ui.designer.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.StringUtil;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class ChangeWeightsCommand extends Command {
	private EditPart parent;
	private ChangeBoundsRequest request;
	private Command command;

	public ChangeWeightsCommand(EditPart parent, ChangeBoundsRequest request) {
		super("Change Weights");
		this.parent = parent;
		this.request = request;
	}

	public boolean canExecute() {
		if (parent == null || request == null || request.getEditParts() == null) {
			return false;
		}
		return parent instanceof VisualEditPart;
	}

	public void execute() {
		IVisualInfo visualInfo = ((VisualEditPart) parent).getVisualInfo();
		SashForm sashForm = (SashForm) visualInfo.getVisualObject();
		Integer[] weights = computeWeights(sashForm);
		command = new ApplyAttributeSettingCommand(
				(XamlNode) parent.getModel(), "weights",
				IConstants.XWT_NAMESPACE, StringUtil.format(weights));
		command.execute();
	}

	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	public void undo() {
		command.undo();
	}

	private Integer[] computeWeights(SashForm sashForm) {
		int[] weights = sashForm.getWeights();

		List editParts = request.getEditParts();
		//		
		XamlNode parentNode = (XamlNode) parent.getModel();
		for (Object object : editParts) {
			int index = parentNode.getChildNodes().indexOf(
					((EditPart) object).getModel());
			if (index == -1) {
				continue;
			}
			int offset = getResizeOffset((VisualEditPart) object, weights,
					index);
			for (int i = 0; i < weights.length; i++) {
				if (i == index) {
					weights[i] += offset;
				} else {
					weights[i] -= offset / (weights.length - 1);
				}
			}
		}
		List<Integer> ws = new ArrayList<Integer>();
		for (int i = 0; i < weights.length; i++) {
			ws.add(weights[i]);
		}
		return ws.toArray(new Integer[ws.size()]);
	}

	private int getResizeOffset(VisualEditPart visualEp, int[] weights,
			int index) {
		float total = 0;
		for (int i : weights) {
			total += i;
		}

		Object visualObject = visualEp.getVisualInfo().getVisualObject();
		Point size = SWTTools.getSize((Control) visualObject);
		int resizeDirection = request.getResizeDirection();
		Dimension sizeDelta = request.getSizeDelta();
		if (resizeDirection == PositionConstants.EAST
				|| resizeDirection == PositionConstants.WEST) {
			float percent = weights[index] / total;
			float width = (size.x / percent);
			float newPercent = (size.x + sizeDelta.width) / width;
			int newWeight = (int) (total * newPercent);
			return newWeight - weights[index];
		} else {
			float percent = weights[index] / total;
			float height = (size.y / percent);
			float newPercent = (size.y + sizeDelta.height) / height;
			int newWeight = (int) (total * newPercent);
			return newWeight - weights[index];
		}
	}
}
