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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.dialogs.FindByElementIdDialog;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FindWithElementIdAction extends Action {
	public static final String ID = "org.eclipse.e4.tools.ui.designer.actions.FindByElementIdAction";
	private EditPartViewer viewer;

	public FindWithElementIdAction(EditPartViewer viewer) {
		this.viewer = viewer;
		setId(ID);
		setText("Element Id");
	}

	public void run() {
		List<EditPart> editparts = new ArrayList<EditPart>();
		List selectedEditParts = viewer.getSelectedEditParts();
		if (selectedEditParts != null && !selectedEditParts.isEmpty()) {
			editparts.addAll(selectedEditParts);
		} else {
			editparts.add(viewer.getRootEditPart());
		}
		EObject selectedElement = null;
		for (EditPart ep : editparts) {
			Object model = ep.getModel();
			if (model instanceof EObject) {
				selectedElement = (EObject) model;
				break;
			}
		}
		if (selectedElement == null) {
			// TODO
			return;
		}
		List<?> elements = ApplicationModelHelper.collectAllElements(
				selectedElement, ApplicationPackageImpl.eINSTANCE
						.getApplicationElement());

		FindByElementIdDialog dialog = new FindByElementIdDialog(
				new Shell(), elements.toArray(new Object[0]));
		if (Window.OK == dialog.open()) {
			Object object = dialog.getFirstResult();
			DesignerSelectionTools.selectElement(object, viewer);
		}
	}
}
