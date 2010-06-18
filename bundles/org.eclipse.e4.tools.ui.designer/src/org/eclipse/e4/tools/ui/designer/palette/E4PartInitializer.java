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
package org.eclipse.e4.tools.ui.designer.palette;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.tools.ui.designer.E4Designer;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.wizards.part.NewOptionsPartWizard;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4PartInitializer extends E4PaletteInitializer {

	public boolean initialize(Entry entry, Object newObject) {
		boolean initialize = super.initialize(entry, newObject);
		return initialize && promptInitialize(entry, newObject);
	}

	protected boolean promptInitialize(Entry entry, Object newObject) {
		if (newObject == null || !(newObject instanceof MPart)) {
			return false;
		}
		IFile file = null;
		try {
			E4Designer designer = (E4Designer) E4DesignerPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			file = designer.getFile();
		} catch (Exception e) {
			E4DesignerPlugin.logError(e);
		}
		if (file == null) {
			return false;
		}

		NewOptionsPartWizard newWizard = new NewOptionsPartWizard(file,
				(MPart) newObject, entry.getDataContext());
		WizardDialog dialog = new WizardDialog(new Shell(), newWizard);
		return Window.OK == dialog.open();
	}
}
