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
package org.eclipse.e4.tools.ui.designer.sashform;

import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

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
		
		weights = SashFormUtil.computeWeights(parent, request);
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
}
