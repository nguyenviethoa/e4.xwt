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
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.contribution.CreationCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreateCommand extends CreationCommand {

	private EditPart parent;
	private int index;
	private MElementContainer parentModel;
	private Object newObj;
	public CreateCommand(CreateRequest createRequest, EditPart parent,
			int index) {
		super(createRequest);
		this.parent = parent;
		this.index = index;
	}

	public boolean canExecute() {
		if (parent == null || !super.canExecute()) {
			return false;
		}
		Object model = parent.getModel();
		if (!(model instanceof MElementContainer<?>)) {
			return false;
		}
		parentModel = (MElementContainer<?>) model;
		Entry entry = getEntry();
		if (entry == null) {
			return false;
		}
		EClass type = entry.getType();
		Object tmpObj = EcoreUtil.create(type);
		if (tmpObj == null || !(tmpObj instanceof MUIElement)) {
			return false;
		}
		return ApplicationModelHelper.canAddedChild((MUIElement) tmpObj,
				parentModel);
	}

	protected void doCreate(Entry entry, Object newObject) {
		List children = parentModel.getChildren();
		if (index == -1 || index >= children.size()) {
			children.add(newObject);
		} else {
			children.add(index, newObject);
		}
		this.newObj = newObject;
	}

	public boolean canUndo() {
		return parentModel != null && newObj != null;
	}

	public void undo() {
		parentModel.getChildren().remove(newObj);
	}

}
