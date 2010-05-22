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
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.List;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.xwt.tools.categorynode.node.CategoryNode;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.contribution.CreationCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreateCommand extends CreationCommand {

	private EditPart parent;
	private int index;
	private Object parentModel;
	private Object newObj;

	public CreateCommand(CreateRequest createRequest, EditPart parent, int index) {
		super(createRequest);
		this.parent = parent;
		this.index = index;
	}

	public boolean canExecute() {
		if (parent == null || !super.canExecute()) {
			return false;
		}
		Entry entry = getEntry();
		if (entry == null) {
			return false;
		}
		EClass type = entry.getType();
		Object tmpObj = EcoreUtil.create(type);
		if (tmpObj == null) {
			return false;
		}
		parentModel = parent.getModel();
		if (parentModel instanceof MElementContainer<?>) {
			MElementContainer<?> container = (MElementContainer<?>) parentModel;
			return (ApplicationModelHelper.canAddedChild(container, (EObject) tmpObj));
		} else if (parentModel instanceof CategoryNode) {
			CategoryNode categoryNode = (CategoryNode) parentModel;
			EReference reference = categoryNode.getReference();
			if (categoryNode.getObject() == null || reference == null
					|| !reference.isMany()) {
				return false;
			}
			EClassifier eType = reference.getEType();
			return eType != null && eType.isInstance(tmpObj);
		}
		return false;
	}

	protected void doCreate(Entry entry, Object newObject) {
		if (parentModel instanceof MElementContainer<?>) {
			List children = ((MElementContainer<?>) parentModel).getChildren();
			addChild(children, newObject);
		} else if (parentModel instanceof CategoryNode) {
			CategoryNode category = ((CategoryNode) parentModel);
			Object value = category.getReferenceValue();
			EReference reference = category.getReference();
			if (reference.isMany()) {
				List children = (List) value;
				addChild(children, newObject);
			}
			else {
				category.getObject().eSet(reference, newObject);
				this.newObj = newObject;
			}
		}
	}

	private void addChild(List children, Object child) {
		if (index == -1 || index >= children.size()) {
			children.add(child);
		} else {
			children.add(index, child);
		}
		this.newObj = child;
	}

	public boolean canUndo() {
		return parentModel != null && newObj != null;
	}

	public void undo() {
		if (parentModel instanceof MElementContainer<?>) {
			((MElementContainer<?>) parentModel).getChildren().remove(newObj);
		} else if (parentModel instanceof CategoryNode) {
			List children = (List) ((CategoryNode) parentModel)
					.getReferenceValue();
			children.remove(newObj);
		}
	}

}
