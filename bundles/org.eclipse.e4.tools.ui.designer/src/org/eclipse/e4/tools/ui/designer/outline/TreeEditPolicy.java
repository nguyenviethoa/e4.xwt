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
package org.eclipse.e4.tools.ui.designer.outline;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.commands.DeleteCommand;
import org.eclipse.e4.tools.ui.designer.commands.CreateCommand;
import org.eclipse.e4.tools.ui.designer.outline.commands.MoveChildrenCommand;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.TreeItemEditPolicy;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class TreeEditPolicy extends TreeItemEditPolicy {

	protected Command getDeleteCommand(Request req) {
		Object model = getHost().getModel();
		if (model instanceof MUIElement) {
			return new DeleteCommand((MUIElement) model);
		}
		return super.getDeleteCommand(req);
	}

	protected Command getOrphanChildrenCommand(GroupRequest req) {
		List editParts = req.getEditParts();
		if (editParts != null && !editParts.isEmpty()) {
			CompoundCommand cc = new CompoundCommand();
			for (Object object : editParts) {
				Object model = ((EditPart) object).getModel();
				if (!(model instanceof MUIElement)) {
					continue;
				}
				cc.add(new DeleteCommand((MUIElement) model));
			}
			return cc.unwrap();
		}
		return null;
	}

	protected Command getAddCommand(ChangeBoundsRequest request) {
		EditPart host = getHost();
		Object parentModel = host.getModel();
		List editParts = request.getEditParts();
		int index = findIndexOfTreeItemAt(request.getLocation());
		CompoundCommand commands = new CompoundCommand();
		for (Object object : editParts) {
			Object model = ((EditPart) object).getModel();
			commands.add(CommandFactory.createAddChildCommand(parentModel,
					model, index));
		}
		return commands.unwrap();
	}

	protected Command getCreateCommand(CreateRequest request) {
		EditPart targetEditPart = getTargetEditPart(request);
		int index = findIndexOfTreeItemAt(request.getLocation());
		return new CreateCommand(request, targetEditPart, index);
	}

	protected Command getMoveChildrenCommand(ChangeBoundsRequest request) {
		EditPart host = getHost();
		List editParts = request.getEditParts();
		Point pt = request.getLocation();
		int index = findMoveIndexAt(pt);
		return new MoveChildrenCommand(host, editParts, index);
	}

	protected int findMoveIndexAt(Point pt) {
		int index = -1;
		TreeItem item = findTreeItemAt(pt);
		if (item != null) {
			index = getHost().getChildren().indexOf(item.getData());
			if (isInUpperHalf(item.getBounds(), pt)) {
				index--;
			}
		}
		return index;
	}

	private boolean isInUpperHalf(Rectangle rect, Point pt) {
		Rectangle tempRect = new Rectangle(rect.x, rect.y, rect.width,
				rect.height / 2);
		return tempRect
				.contains(new org.eclipse.swt.graphics.Point(pt.x, pt.y));
	}
}
