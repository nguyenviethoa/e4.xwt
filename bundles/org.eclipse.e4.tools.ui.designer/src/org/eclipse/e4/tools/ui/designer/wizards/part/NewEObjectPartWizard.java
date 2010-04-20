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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewEObjectPartWizard extends WizardNewPart {

	private PartDataContext dataContext;
	private EPackage ePackage;
	public NewEObjectPartWizard(IFile selectedFile, MPart part,
			EObject dataContextValue) {
		super(selectedFile, part);
		this.dataContext = new PartDataContext(dataContextValue);
		if (dataContextValue != null) {
			ePackage = dataContextValue.eClass().getEPackage();
		}
	}

	public void addPages() {
		NewEObjectPartWizardPage newPartPage = new NewEObjectPartWizardPage(
				dataContext, ePackage, true);
		newPartPage.init(new StructuredSelection(fFile.getProject()));
		addPage(newPartPage);
		setNewTypeWizardPage(newPartPage);
	}
}
