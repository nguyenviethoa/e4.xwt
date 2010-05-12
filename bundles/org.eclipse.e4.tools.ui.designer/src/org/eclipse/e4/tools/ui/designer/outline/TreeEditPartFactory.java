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

import java.util.Collections;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.TreeItemEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPolicy;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class TreeEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if (context == null && model instanceof MApplication) {
			return new RootTreeEditPart((MApplication) model);
		}
		return new TreeEditPart(model);
	}

	private static class TreeEditPart extends TreeItemEditPart {

		public TreeEditPart(Object model) {
			super(model, ApplicationModelHelper.getContentProvider(),
					ApplicationModelHelper.getLabelProvider());
		}

		protected void createEditPolicies() {
			installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE,
					new TreeEditPolicy());
		}
	}

	private static class RootTreeEditPart extends TreeEditPart {

		private MApplication model;

		public RootTreeEditPart(MApplication model) {
			super(model);
			this.model = model;
		}

		protected List getModelChildren() {
			if (model != null) {
				return Collections.singletonList(model);
			}
			return super.getModelChildren();
		}
	}
}
