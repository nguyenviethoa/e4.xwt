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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDataPartWizard extends NewElementWizard {

	private IFile selectedFile;
	private MPart fPart;
	private EObject dataContext;

	private NewDataPartWizardPage fDataPage;

	public NewDataPartWizard(IFile selectedFile, MPart part, EObject dataContext) {
		this.selectedFile = selectedFile;
		this.fPart = part;
		this.dataContext = dataContext;
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Part");
	}

	public void addPages() {
		EClass eClass = dataContext.eClass();
		fDataPage = new NewDataPartWizardPage(eClass.getEPackage());
		fDataPage.init(new StructuredSelection(selectedFile.getProject()));
		fDataPage.setDataContext(dataContext);
		addPage(fDataPage);
	}

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fDataPage.createType(monitor);
	}

	public IJavaElement getCreatedElement() {
		return fDataPage.getCreatedType();
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish) {
			IType type = (IType) getCreatedElement();
			String elementName = type.getFullyQualifiedName();
			String projectName = type.getJavaProject().getElementName();
			String partURI = URI.createPlatformPluginURI(
					projectName + "/" + elementName, true).toString();
			fPart.setURI(partURI);
			fPart.setLabel(type.getElementName());
		}
		return performFinish;
	}
}
