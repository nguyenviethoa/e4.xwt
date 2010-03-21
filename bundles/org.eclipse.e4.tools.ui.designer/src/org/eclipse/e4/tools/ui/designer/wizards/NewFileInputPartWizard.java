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
public class NewFileInputPartWizard extends WizardNewPart {

	private NewFileInputSelectionWizardPage fSelectionPage;
	private NewFileInputPartWizardPage fPartPage;

	public NewFileInputPartWizard(IFile file, MPart part) {
		super(file, part);
	}

	public void addPages() {
		fPartPage = new NewFileInputPartWizardPage();

		fSelectionPage = new NewFileInputSelectionWizardPage(fPartPage);
		addPage(fSelectionPage);

		fPartPage.init(new StructuredSelection(fFile));
		addPage(fPartPage);
	}

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fPartPage.createType(monitor);
	}

	public IJavaElement getCreatedElement() {
		return fPartPage.getCreatedType();
	}

}
