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
package org.eclipse.e4.tools.ui.designer.commands;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.emf.ecore.EObject;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class MenuDeleteCommand extends AbstractDeleteCommand {

	private MMenu menu;
	private MWindow window;

	public MenuDeleteCommand(MMenu menu) {
		super((EObject)menu);
		this.menu = menu;
		if (menu instanceof EObject) {
			EObject object = (EObject) menu;
			EObject container = object.eContainer();
			if (container instanceof MWindow) {
				window = (MWindow) container;
			}
		}
	}

	public boolean canExecute() {
		return super.canExecute() && window != null;
	}

	public void doExecute() {
		window.setMainMenu(null);
	}

	public boolean canUndo() {
		return window != null && menu != null;
	}

	public void doUndo() {
		window.setMainMenu(menu);
	}
}
