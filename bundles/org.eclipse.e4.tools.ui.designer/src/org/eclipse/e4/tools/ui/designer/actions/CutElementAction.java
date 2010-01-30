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

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

public class CutElementAction extends SelectionAction {

	/**
	 * @param part
	 */
	public CutElementAction(IWorkbenchPart part) {
		super(part);
		this.setText(WorkbenchMessages.Workbench_cut);
		this.setToolTipText(WorkbenchMessages.Workbench_cutToolTip);
		this.setId(ActionFactory.CUT.getId());
		this.setAccelerator(SWT.MOD1 | 'x');
		ISharedImages sharedImages = part.getSite().getWorkbenchWindow()
				.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IStructuredSelection structuredSelection = (IStructuredSelection) getSelection();
		List<MUIElement> selectResult = new ArrayList<MUIElement>();
		for (Iterator<?> iterator = structuredSelection.iterator(); iterator
				.hasNext();) {
			Object element = iterator.next();
			if (element instanceof EditPart) {
				EditPart editPart = (EditPart) element;
				element = editPart.getModel();
			}
			if (element instanceof MUIElement) {
				MUIElement copymodel = (MUIElement) EcoreUtil
						.copy((EObject) element);
				selectResult.add(copymodel);
			}
		}
		if (selectResult != null && selectResult.size() != 0) {
			Command command = CommandFactory.createDeleteCommand(selectResult);
			if (command != null && command.canExecute()) {
				Clipboard.getDefault().setContents(selectResult);
				Designer editorPart = (Designer) getWorkbenchPart();
				editorPart.getEditDomain().getCommandStack().execute(command);
			}
		}
	}
}
