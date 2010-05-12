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
package org.eclipse.e4.tools.ui.designer.actions;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.tools.ui.designer.widgets.ElementCreateDialog;
import org.eclipse.e4.ui.model.application.node.CategoryNode;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CategoryCreateAction extends SelectionAction {

	public static final String ID = CategoryCreateAction.class.getName();
	private CategoryNode categoryNode;
	private int index = -1;

	public CategoryCreateAction(IWorkbenchPart part) {
		super(part);
		setText("New...");
		setId(ID);
	}

	protected boolean calculateEnabled() {
		categoryNode = null;
		index = -1;
		List selectedObjects = getSelectedObjects();
		if (selectedObjects == null || selectedObjects.size() != 1) {
			return false;
		}
		Object object = selectedObjects.get(0);
		calculateCategoryNode(object);
		return canBeEnabled();
	}
	
	private boolean canBeEnabled () {
		if (categoryNode == null || categoryNode.getObject() == null
				|| categoryNode.getReference() == null) {
			return false;
		}
		EReference reference = categoryNode.getReference();
		if (!reference.isMany() && categoryNode.getObject().eGet(reference) != null) {
			return false;
		}
		return true;
	}

	private void calculateCategoryNode(Object object) {
		if (object == null) {
			return;
		}
		if (object instanceof EditPart) {
			EditPart editPart = (EditPart) object;
			Object model = editPart.getModel();
			if (model instanceof CategoryNode) {
				categoryNode = (CategoryNode) model;
			}
			// lookup parent
			EditPart parent = editPart.getParent();
			Object parentModel = parent.getModel();
			if (parentModel instanceof CategoryNode) {
				categoryNode = (CategoryNode) parentModel;
				index = parent.getChildren().indexOf(object);
			}
		}
	}

	public void run() {
		EClassifier eType = categoryNode.getReference().getEType();
		if (eType instanceof EClass) {
			ElementCreateDialog dialog = new ElementCreateDialog(
					getWorkbenchPart().getSite().getShell(), (EClass) eType);
			if (dialog.open() == Window.OK) {
				EObject result = dialog.getResult();
				Command command = CommandFactory.createAddChildCommand(
						categoryNode.getObject(), result, index);
				execute(command);
			}
		}
	}

	protected void refresh() {
		super.refresh();
		if (categoryNode == null) {
			setText("New...");
			setImageDescriptor(null);
			return;
		}
		EReference reference = categoryNode.getReference();
		if (reference == null) {
			return;
		}
		EClassifier eType = reference.getEType();
		if (eType == null) {
			return;
		}
		setText("New " + eType.getName());
		if (eType instanceof EClass) {
			Image image = ApplicationModelHelper.getImage(EcoreUtil
					.create((EClass) eType));
			if (image != null) {
				setImageDescriptor(ImageDescriptor.createFromImage(image));
			}
		}
	}
}
