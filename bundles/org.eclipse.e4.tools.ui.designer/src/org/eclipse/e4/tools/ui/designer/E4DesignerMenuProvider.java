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
package org.eclipse.e4.tools.ui.designer;

import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.DesignerMenuProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class E4DesignerMenuProvider extends DesignerMenuProvider {

	private static final String BINDINGS = "Bindings";

	private static final String EXTERNALIZE = "Externalize"; // add by xrchen 2009/9/22

	public E4DesignerMenuProvider(Designer editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ContextMenuProvider#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager menu) {
		menu.add(new Separator(ActionConstants.UNDO));
		menu.add(new Separator(ActionConstants.DELETE));
		menu.add(new Separator(ActionConstants.PRINT));
		menu.add(new Separator(ActionConstants.COPY));
		menu.add(new Separator(ActionConstants.EDIT));
		menu.add(new Separator(BINDINGS));
		menu.add(new Separator(EXTERNALIZE)); // add by xrchen 2009/9/22
		menu.add(new Separator(ActionConstants.ADDITIONS));
		super.menuAboutToShow(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.EditorMenuProvider#buildContextMenu(org.eclipse .jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		super.buildContextMenu(menu);
		ActionRegistry actionRegistry = getActionRegistry();
		// menu.appendToGroup(BINDINGS, actionRegistry.getAction(BindingLayerAction.ID));
		List selectedEditParts = getViewer().getSelectedEditParts();
		if (selectedEditParts == null || selectedEditParts.isEmpty()) {
			// Diagram directly...
		} else {
			if (selectedEditParts.size() == 1) {
				// Single selection...
			} else {
				// Multi-Selection.
			}
		}
	}
}
