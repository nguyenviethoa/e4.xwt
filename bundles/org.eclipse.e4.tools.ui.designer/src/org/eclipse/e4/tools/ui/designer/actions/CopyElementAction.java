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
package org.eclipse.e4.tools.ui.designer.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

public class CopyElementAction extends SelectionAction {

	public CopyElementAction(IWorkbenchPart part) {
		super(part);
		setText(WorkbenchMessages.Workbench_copy);
		setToolTipText(WorkbenchMessages.Workbench_copyToolTip);
		setId(ActionFactory.COPY.getId());
		setAccelerator(SWT.MOD1 | 'c');
		ISharedImages sharedImages = part.getSite().getWorkbenchWindow()
				.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
	}

	protected boolean calculateEnabled() {
		ISelection selection = getSelection();
		if (selection != null && !selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Iterator<?> iterator = structuredSelection.iterator(); iterator
					.hasNext();) {
				Object element = iterator.next();
				if (element instanceof EditPart) {
					EditPart editPart = (EditPart) element;
					element = editPart.getModel();
				}
				if (element instanceof EObject) {
					EObject eObject = (EObject) element;
					if (eObject.eContainer() == null) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void run() {
		IStructuredSelection structuredSelection = (IStructuredSelection) getSelection();
		List<MApplicationElement> selectResult = new ArrayList<MApplicationElement>();
		if (structuredSelection.isEmpty()) {
			// Diagram directly...
		} else {
			for (Iterator<?> iterator = structuredSelection.iterator(); iterator
					.hasNext();) {
				Object element = iterator.next();
				if (element instanceof EditPart) {
					EditPart editPart = (EditPart) element;
					element = editPart.getModel();
				}
				if (element instanceof EObject) {
					EObject parentModel = ((EObject) element).eContainer();
					if (element instanceof MApplicationElement
							&& parentModel instanceof MApplicationElement) {
						MApplicationElement copymodel = (MApplicationElement) EcoreUtil
								.copy((EObject) element);
						copymodel.setElementId(EcoreUtil.generateUUID());
						if (copymodel instanceof MUIElement) {
							MUIElement muiElement = (MUIElement) copymodel;
							muiElement.setWidget(null);
						}
						selectResult.add(copymodel);
					}
				}
			}
		}
		if (selectResult != null && selectResult.size() != 0)
			Clipboard.getDefault().setContents(selectResult);
		super.run();
	}
}
