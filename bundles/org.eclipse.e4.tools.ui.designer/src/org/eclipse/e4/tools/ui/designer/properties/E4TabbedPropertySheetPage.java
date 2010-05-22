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
package org.eclipse.e4.tools.ui.designer.properties;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4TabbedPropertySheetPage extends TabbedPropertySheetPage {

	private CommandStack commandStack;

	public E4TabbedPropertySheetPage(
			ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor,
			CommandStack commandStack) {
		super(tabbedPropertySheetPageContributor);
		this.setCommandStack(commandStack);
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

}
