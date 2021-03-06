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
package org.eclipse.e4.tools.ui.designer;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.designer.actions.FindElementAction;
import org.eclipse.e4.tools.ui.designer.actions.NewChildAction;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.DesignerActionConstants;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.DesignerMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class E4DesignerMenuProvider extends DesignerMenuProvider {

	private static final String GROUP_NEW = "New";

	private static final String BINDINGS = "Bindings";

	private static final String EXTERNALIZE = "Externalize"; // add by xrchen
																// 2009/9/22
	private IProject project;

	public E4DesignerMenuProvider(IProject project, EditPartViewer viewer,
			ActionRegistry actionRegistry) {
		super(viewer, actionRegistry);
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ContextMenuProvider#menuAboutToShow(org.eclipse.jface
	 * .action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager menu) {
		menu.add(new Separator(GROUP_NEW));
		menu.add(new Separator(DesignerActionConstants.UNDO));
		menu.add(new Separator(DesignerActionConstants.UNDO));
		menu.add(new Separator(DesignerActionConstants.DELETE));
		menu.add(new Separator(DesignerActionConstants.PRINT));
		menu.add(new Separator(DesignerActionConstants.COPY));
		menu.add(new Separator(DesignerActionConstants.EDIT));
		menu.add(new Separator(BINDINGS));
		menu.add(new Separator(EXTERNALIZE)); // add by xrchen 2009/9/22
		menu.add(new Separator(DesignerActionConstants.ADDITIONS));
		super.menuAboutToShow(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.xaml.ve.editor.EditorMenuProvider#buildContextMenu(org.eclipse
	 * .jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		super.buildContextMenu(menu);
		// ActionRegistry actionRegistry = getActionRegistry();
		// menu.appendToGroup(BINDINGS,
		// actionRegistry.getAction(BindingLayerAction.ID));
		List selectedEditParts = getViewer().getSelectedEditParts();
		if (selectedEditParts == null || selectedEditParts.isEmpty()) {
			// Diagram directly...
		} else {
			if (selectedEditParts.size() == 1) {
				// Single selection...
				EditPart editPart = (EditPart) selectedEditParts.get(0);
				menu.appendToGroup(GROUP_NEW, new NewChildAction(project,
						editPart));
			} else {
				// Multi-Selection.
			}
			menu.add(new FindElementAction(getViewer()));
		}
	}
}
