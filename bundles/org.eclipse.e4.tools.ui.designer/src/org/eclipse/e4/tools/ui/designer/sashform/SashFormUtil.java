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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MPSCElement;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 * 
 */
public class SashFormUtil {
	public static Integer[] computeWeights(SashFormEditPart parent) {
		MPartSashContainer parentNode = (MPartSashContainer) parent.getModel();
		List<Integer> integers = new ArrayList<Integer>();
		for (MUIElement child : parentNode.getChildren()) {
			Integer weight = getWeight(child);
			if (weight == null) {
				continue;
			}
			integers.add(weight);
		}
		return integers.toArray(new Integer[integers.size()]);
	}

	public static Integer getWeight(MUIElement element) {
		if (element == null || element.getWidget() == null) {
			return null;
		}
		Object widget = element.getWidget();
		if (widget instanceof Control) {
			MElementContainer<MUIElement> psc = element.getParent();
			Object parent = psc.getWidget();
			if (parent instanceof SashForm) {
				int[] weights = ((SashForm) parent).getWeights();
				int weightIndex = 0;
				for (MUIElement pscElement : psc.getChildren()) {
					if (pscElement == element) {
						return weights[weightIndex];
					} else if (pscElement.getWidget() instanceof Control) {
						weightIndex++;
					}
				}
			}
		}
		String containerData = element.getContainerData();
		try {
			return Integer.parseInt(containerData);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static Integer[] computeWeights(SashFormEditPart parent,
			ChangeBoundsRequest request) {
		IVisualInfo visualInfo = ((VisualEditPart) parent).getVisualInfo();
		SashForm sashForm = (SashForm) visualInfo.getVisualObject();

		int[] weights = sashForm.getWeights();
		int[] newWeights = weights;

		VisualEditPart editPart = getEditPart(request);
		if (editPart != null) {
			MElementContainer<MPSCElement> parentNode = (MElementContainer<MPSCElement>) parent
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

	static private int getSashIndex(SashForm sashForm, Sash sash) {
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

	static private Control[] getControls(SashForm sashForm) {
		try {
			Field field = SashForm.class.getDeclaredField("controls");
			field.setAccessible(true);
			return (Control[]) field.get(sashForm);
		} catch (Exception e) {
		}
		return null;
	}

	static private int getResizeOffset(Control control, int[] weights,
			int index, int x, int y, boolean horizontal) {
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

	static private VisualEditPart getEditPart(GroupRequest request) {
		List<VisualEditPart> editParts = new ArrayList<VisualEditPart>(request
				.getEditParts());
		if (editParts.size() > 0) {
			return editParts.get(0);
		}
		return null;
	}

	static boolean isHorizontal(SashForm sashForm) {
		return (sashForm.getStyle() & SWT.HORIZONTAL) != 0;
	}

	public static boolean isHorizontal(SashFormEditPart editPart) {
		if (editPart == null || editPart.getVisualInfo() == null) {
			throw new NullPointerException();
		}
		SashForm sashForm = (SashForm) editPart.getVisualInfo()
				.getVisualObject();
		return isHorizontal(sashForm);
	}
}
