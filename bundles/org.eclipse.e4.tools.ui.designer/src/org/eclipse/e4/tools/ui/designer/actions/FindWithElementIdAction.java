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
import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.tools.ui.designer.widgets.FindElementsWithIdDialog;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
		List<EObject> contexts = new ArrayList<EObject>();
		List<EditPart> editparts = new ArrayList<EditPart>();
		List selectedEditParts = viewer.getSelectedEditParts();
		if (selectedEditParts != null && !selectedEditParts.isEmpty()) {
			editparts.addAll(selectedEditParts);
		} else {
			editparts.add(viewer.getRootEditPart());
		}
		for (EditPart ep : editparts) {
			Object model = ep.getModel();
			if (model instanceof EObject) {
				contexts.add((EObject) model);
			}
		}
		List<Object> elements = new ArrayList<Object>();
		for (EObject parent : contexts) {
			Object[] children = ApplicationModelHelper.getChildren(parent,
					new IFilter() {
						public boolean select(Object toTest) {
							if (toTest instanceof EObject
									&& toTest instanceof MApplicationElement) {
								return ((EObject) toTest).eResource() != null;
							}
							return false;
						}
					}, true);
			if (children == null || children.length == 0) {
				continue;
			}
			elements.addAll(Arrays.asList(children));
		}
		FindElementsWithIdDialog dialog = new FindElementsWithIdDialog(new Shell(),
				elements.toArray(new Object[0]));
		if (Window.OK == dialog.open()) {
			Object object = dialog.getFirstResult();
			EditPart editpart = (EditPart) viewer.getEditPartRegistry().get(
					object);
			if (editpart != null) {
				viewer.reveal(editpart);
				viewer.select(editpart);
			} else {
				StructuredSelection selection = new StructuredSelection(object);
				ISelectionProvider selectionProvider = E4DesignerPlugin
						.getDefault().getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActivePart().getSite()
						.getSelectionProvider();
				if (selectionProvider != null) {
					selectionProvider.setSelection(selection);
				}
			}
		}
	}
}