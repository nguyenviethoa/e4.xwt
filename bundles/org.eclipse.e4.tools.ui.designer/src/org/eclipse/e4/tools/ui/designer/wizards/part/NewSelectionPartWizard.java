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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartWizard extends WizardNewPart {

	public NewSelectionPartWizard(IFile file, MPart part) {
		super(file, part);
		setWindowTitle("Selection Part Initialization");
	}

	public void addPages() {
		PartDataContext dataContext = new PartDataContext();
		NewSelectionPartDataContextPage newSelectionPage = new NewSelectionPartDataContextPage(
				dataContext);

		addPage(newSelectionPage);

		NewSelectionPartWizardPage newTypePage = new NewSelectionPartWizardPage(
				dataContext);
		newTypePage.init(new StructuredSelection(fFile));
		addPage(newTypePage);

		setNewTypeWizardPage(newTypePage);
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish) {
			// try to add this variable, so that, the selection event of all
			// children EclipseContext are coming.
			fPart.getVariables().add(IServiceConstants.SELECTION);
		}
		return performFinish;
	}
}
