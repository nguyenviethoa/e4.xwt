/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.editparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.MUILabel;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.WidgetInfo;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class WidgetEditPart extends VisualEditPart {

	/**
	 * @param model
	 */
	public WidgetEditPart(EObject model) {
		super(model);
	}

	protected boolean isVisualInfoObsolate() {
		Object uiElmeent = getMuiElement().getWidget();
		IVisualInfo visualInfo = getVisualInfo();
		Object visualObject = visualInfo.getVisualObject();
		if (visualObject instanceof Widget) {
			Widget widget = (Widget) visualObject;
			if (widget.isDisposed() && uiElmeent != widget) {
				return true;
			}
		}
		return super.isVisualInfoObsolate();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart#
	 * createVisualInfo()
	 */
	protected IVisualInfo createVisualInfo() {
		Object widget = getMuiElement().getWidget();
		return new WidgetInfo(widget, isRoot());
	}

	public MUIElement getMuiElement() {
		return (MUIElement) getModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		List children = new ArrayList();
		Object[] childrenArray = ApplicationModelHelper.getModelChildren(getModel());
		if (childrenArray != null) {
			children.addAll(Arrays.asList(childrenArray));
		}
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.parts.VisualEditPart#
	 * createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				List<?> editParts = deleteRequest.getEditParts();
				CompoundCommand command = new CompoundCommand();
				for (Iterator<?> iterator = editParts.iterator(); iterator
						.hasNext();) {
					EditPart editPart = (EditPart) iterator.next();
					Object model = editPart.getModel();
					Command deleteCommand = CommandFactory
							.createDeleteCommand(model);
					if (deleteCommand != null) {
						command.add(deleteCommand);
					}
				}
				return command.unwrap();
			}
		});
		// installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {
		// protected Command getCreateCommand(CreateRequest request) {
		// return null;
		// }
		//
		// protected Command createChangeConstraintCommand(EditPart child,
		// Object constraint) {
		// return null;
		// }
		// });
	}

	public Object getWidget() {
		MUIElement muiElement = getMuiElement();
		if (muiElement != null) {
			return muiElement.getWidget();
		}
		return null;
	}

	public void refresh() {
		// refreshVisuals();
		// refreshChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#toString()
	 */
	public String toString() {
		MUIElement muiElement = getMuiElement();
		String value = "";
		if (muiElement != null) {
			if (muiElement instanceof EObject) {
				EClass eClass = ((EObject) muiElement).eClass();
				value = eClass.getName();
			}
		}
		if (muiElement instanceof MUILabel) {
			String label = ((MUILabel) muiElement).getLabel();
			if (label != null && !"".equals(label)) {
				value += " - \"" + label + "\"";
			}
		}
		// Object widget = getWidget();
		// if (widget != null) {
		// value += "-\"" + widget.getClass().getSimpleName() + "\"";
		// }
		return value;
	}

}
