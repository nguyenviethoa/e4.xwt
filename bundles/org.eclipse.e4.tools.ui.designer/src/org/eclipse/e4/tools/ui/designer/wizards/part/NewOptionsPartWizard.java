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
package org.eclipse.e4.tools.ui.designer.wizards.part;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTInputPart;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewOptionsPartWizard extends WizardNewPart {

	private PartDataContext dataContext;

	public NewOptionsPartWizard(IFile selectedFile, MPart part, Object dataContextValue) {
		super(selectedFile, part);
		dataContext = new PartDataContext(dataContextValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		String superClass = null;
		if (fPart instanceof MInputPart) {
			superClass = XWTInputPart.class.getName();
		}
		NewOptionsPartWizardPage newTypePage = new NewOptionsPartWizardPage(superClass,
				dataContext);
		IStructuredSelection selection = getSelection();
		if (selection == null) {
			selection = new StructuredSelection(JavaCore.create(fFile
					.getProject()));
		}
		newTypePage.init(selection);
		addPage(newTypePage);

		setNewTypeWizardPage(newTypePage);
	}

}
