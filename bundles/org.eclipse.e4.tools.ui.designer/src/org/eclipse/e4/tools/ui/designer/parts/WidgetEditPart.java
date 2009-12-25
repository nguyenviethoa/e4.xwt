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
package org.eclipse.e4.tools.ui.designer.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.DeleteCommand;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
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
		MUIElement muiElement = getMuiElement();
		if (muiElement instanceof MElementContainer<?>) {
			for (Object object : ((MElementContainer) muiElement).getChildren()) {
				if (!(object instanceof MUIElement)) {
					continue;
				}
				if (((MUIElement) object).getWidget() != null) {
					children.add(object);
				}
			}
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
				List editParts = deleteRequest.getEditParts();
				CompoundCommand command = new CompoundCommand();
				for (Iterator iterator = editParts.iterator(); iterator
						.hasNext();) {
					EditPart editPart = (EditPart) iterator.next();
					Object model = editPart.getModel();
					if (model instanceof MUIElement) {
						command.add(new DeleteCommand((MUIElement) model));
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
		Object widget = getWidget();
		if (widget != null) {
			value += "-\"" + widget.getClass().getSimpleName() + "\"";
		}
		return value;
	}
}
