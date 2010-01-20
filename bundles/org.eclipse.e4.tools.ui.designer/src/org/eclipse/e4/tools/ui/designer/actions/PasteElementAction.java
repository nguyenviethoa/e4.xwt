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
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

public class PasteElementAction extends SelectionAction {
	private Designer part;
	private Object contents;
	private EditPart parent;

	public PasteElementAction(IWorkbenchPart part) {
		super(part);
		this.part = (Designer) part;
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
		contents = Clipboard.getDefault().getContents();
		if (contents == null) {
			return false;
		}

		if (part.getGraphicalViewer() == null) {
			return false;
		}

		List<?> parts = part.getGraphicalViewer().getSelectedEditParts();
		if (parts == null || parts.isEmpty()) {
			return false;
		}
		parent = (EditPart) parts.get(0);
		Object model = parent.getModel();
		if (model == null || !(model instanceof MUIElement)) {
			return false;
		}
		return canPaste((MUIElement) model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		MUIElement parentNode = (MUIElement) parent.getModel();
		List<MUIElement> elements = (List<MUIElement>) contents;
		CompoundCommand cmd = new CompoundCommand("Paste");
		for (MUIElement child : elements) {
			MUIElement newChild = (MUIElement) EcoreUtil.copy((EObject)child);
			cmd.add(CommandFactory.createAddChildCommand(parentNode, newChild, -1));
		}
		Command command = cmd.unwrap();
		if (command.canExecute()) {
			EditDomain.getEditDomain(parent).getCommandStack().execute(command);
		}
	}

	public Boolean canPaste(MUIElement parent) {
		List<MUIElement> contents = (List<MUIElement>) Clipboard.getDefault().getContents();
		for (MUIElement element : contents) {
			if (!ApplicationModelHelper.canAddedChild(element, parent))
				return false;
		}
		return true;
	}
}
