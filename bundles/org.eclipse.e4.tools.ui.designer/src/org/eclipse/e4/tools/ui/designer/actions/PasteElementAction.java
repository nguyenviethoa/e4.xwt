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

import java.util.List;

import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.xwt.tools.categorynode.node.CategoryNode;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.ceditor.ConfigureDesigner;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

public class PasteElementAction extends SelectionAction {

	public PasteElementAction(IWorkbenchPart part) {
		super(part);
		setText(WorkbenchMessages.Workbench_paste);
		setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);
		setId(ActionFactory.PASTE.getId());
		setAccelerator(SWT.MOD1 | 'v');
		ISharedImages sharedImages = part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		Object contents = Clipboard.getDefault().getContents();
		if (contents == null) {
			return false;
		}

		ISelection selection = getSelection();
		if (selection.isEmpty()) {
			return false;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object parent = structuredSelection.getFirstElement();
		if (parent instanceof EditPart) {
			parent = ((EditPart)parent).getModel();
		}
		if (parent == null) {
			return false;
		}
		
		if (parent instanceof CategoryNode) {
			CategoryNode categoryNode = (CategoryNode) parent;
			if (categoryNode.getObject() != null) {
				parent = categoryNode.getObject();
			}
		}
		if (!(parent instanceof MUIElement)) {
			return false;			
		}

		return canPaste((MUIElement) parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Object contents = Clipboard.getDefault().getContents();
		
		IStructuredSelection structuredSelection = (IStructuredSelection) getSelection();
		Object parent = structuredSelection.getFirstElement();
		if (parent instanceof EditPart) {
			parent = ((EditPart)parent).getModel();
		}
		
		if (parent instanceof CategoryNode) {
			CategoryNode categoryNode = (CategoryNode) parent;
			if (categoryNode.getObject() != null) {
				parent = categoryNode.getObject();
			}
		}
		
		MUIElement parentNode = (MUIElement) parent;
		List<MApplicationElement> elements = (List<MApplicationElement>) contents;
		CompoundCommand cmd = new CompoundCommand("Paste");
		for (MApplicationElement child : elements) {
			MApplicationElement newChild = (MApplicationElement) EcoreUtil.copy((EObject)child);
			newChild.setElementId(EcoreUtil.generateUUID());
			if (newChild instanceof MUIElement) {
				MUIElement muiElement = (MUIElement) newChild;
				muiElement.setWidget(null);
			}
			cmd.add(CommandFactory.createAddChildCommand(parentNode, newChild, -1));
		}
		Command command = cmd.unwrap();
		if (command.canExecute()) {
			ConfigureDesigner editorPart = (ConfigureDesigner) getWorkbenchPart();
			editorPart.getEditDomain().getCommandStack().execute(command);
		}
	}

	public Boolean canPaste(MUIElement parent) {
		Object content = Clipboard.getDefault().getContents();
		if (!(content instanceof List<?>)) {
			return false;
		}
		List<?> contents = (List<?>) content;
		for (Object element : contents) {
			if (!(element instanceof MApplicationElement)) {
				return false;
			}
			if (!ApplicationModelHelper.canAddedChild(parent, (MApplicationElement)element))
				return false;
		}
		return true;
	}
}
