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
package org.eclipse.e4.tools.ui.designer.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartWizard extends WizardNewPart {

	private NewSelectionPartDataContextPage fSelectionPage;
	private NewSelectionPartWizardPage fTypePage;

	public NewSelectionPartWizard(IFile file, MPart part) {
		super(file, part);
		setWindowTitle("Selection Part Initialization");
	}

	public void addPages() {

		fTypePage = new NewSelectionPartWizardPage();
		fSelectionPage = new NewSelectionPartDataContextPage(fTypePage);

		addPage(fSelectionPage);

		fTypePage.init(new StructuredSelection(fFile));
		addPage(fTypePage);
	}

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fTypePage.createType(monitor);
	}

	public IJavaElement getCreatedElement() {
		return fTypePage.getCreatedType();
	}

}
