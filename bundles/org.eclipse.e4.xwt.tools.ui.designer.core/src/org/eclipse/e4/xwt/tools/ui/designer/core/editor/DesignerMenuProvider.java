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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class DesignerMenuProvider extends ContextMenuProvider {

	protected Designer designer;

	/**
	 * @param viewer
	 */
	public DesignerMenuProvider(Designer editor) {
		super(editor.getGraphicalViewer());
		this.designer = editor;
	}

	public ActionRegistry getActionRegistry() {
		return (ActionRegistry) designer.getAdapter(ActionRegistry.class);
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
		menu.add(new Separator(ActionConstants.ADDITIONS));
		super.menuAboutToShow(menu);
	}

	public Designer getDesigner() {
		return designer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		ActionRegistry actionRegistry = getActionRegistry();
		menu.appendToGroup(ActionConstants.UNDO, actionRegistry.getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(ActionConstants.UNDO, actionRegistry.getAction(ActionFactory.UNDO.getId()));
		menu.appendToGroup(ActionConstants.DELETE, actionRegistry.getAction(ActionFactory.DELETE.getId()));
		menu.appendToGroup(ActionConstants.COPY, actionRegistry.getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(ActionConstants.COPY, actionRegistry.getAction(ActionFactory.PASTE.getId()));
		menu.appendToGroup(ActionConstants.COPY, actionRegistry.getAction(ActionFactory.CUT.getId()));
		menu.appendToGroup(ActionConstants.EDIT, actionRegistry.getAction(ActionFactory.SELECT_ALL.getId()));
	}

	public interface ActionConstants {
		String UNDO = "group.undo";
		String COPY = "copyelement";
		String EDIT = "group.edit";
		String PRINT = "group.print";
		String ADDITIONS = "group.additions";
		String DELETE = "group.delete";
		String PASTE = "group.paste";
		String CUT = "group.cut";
	}

}
