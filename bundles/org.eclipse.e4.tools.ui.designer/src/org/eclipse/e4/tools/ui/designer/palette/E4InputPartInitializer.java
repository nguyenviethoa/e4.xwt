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
package org.eclipse.e4.tools.ui.designer.palette;

import org.eclipse.e4.tools.ui.designer.E4Designer;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.wizards.part.NewDynamicFilePartWizard;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4InputPartInitializer extends E4PartInitializer {

	protected boolean promptInitialize(Entry entry, Object newObject) {
		if (newObject == null || !(newObject instanceof MPart)) {
			return false;
		}
		IFileEditorInput input = null;
		MApplication application = null;
		try {
			E4Designer designer = (E4Designer) E4DesignerPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			EditDomain editDomain = designer.getEditDomain();
			input = (IFileEditorInput) editDomain
					.getData(Designer.DESIGNER_INPUT);
			application = (MApplication) designer.getDocumentRoot();
		} catch (Exception e) {
		}
		if (input == null) {
			return false;
		}

		NewDynamicFilePartWizard newWizard = new NewDynamicFilePartWizard(input
				.getFile(), (MPart) newObject, application);
		WizardDialog dialog = new WizardDialog(new Shell(), newWizard);
		return Window.OK == dialog.open();
	}
}
