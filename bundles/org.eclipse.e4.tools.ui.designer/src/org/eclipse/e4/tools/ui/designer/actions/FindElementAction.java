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

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FindElementAction extends Action implements IMenuCreator {

	public static final String ID = "org.eclipse.e4.tools.ui.designer.actions.FindByAction";

	private MenuManager dropDownMenus;
	private EditPartViewer viewer;

	public FindElementAction(EditPartViewer viewer) {
		this.viewer = viewer;
		setId(ID);
		setText("Find Element By");
		setMenuCreator(this);
	}

	private MenuManager getDropDownMenus() {
		if (dropDownMenus == null) {
			dropDownMenus = new MenuManager("Find By");
			makeActions(dropDownMenus);
		}
		return dropDownMenus;
	}

	private void makeActions(MenuManager menuManager) {
		menuManager.add(new FindByElementIdAction(viewer));
		menuManager.add(new FindByContributionURIAction(viewer));
	}

	public Menu getMenu(Control parent) {
		MenuManager mm = getDropDownMenus();
		if (mm != null) {
			return mm.createContextMenu(parent);
		}
		return null;
	}

	public Menu getMenu(Menu parent) {
		MenuManager mm = getDropDownMenus();
		if (mm != null) {
			Menu menu = new Menu(parent);
			IContributionItem[] items = mm.getItems();
			for (int i = 0; i < items.length; i++) {
				IContributionItem item = items[i];
				IContributionItem newItem = item;
				if (item instanceof ActionContributionItem) {
					newItem = new ActionContributionItem(
							((ActionContributionItem) item).getAction());
				}
				newItem.fill(menu, -1);
			}
			return menu;
		}
		return null;
	}

	public void dispose() {
		if (dropDownMenus != null) {
			dropDownMenus.dispose();
		}
	}

}
