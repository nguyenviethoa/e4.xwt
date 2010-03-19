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
import org.eclipse.e4.ui.model.application.MInputPart;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTInputPart;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewPartWizard extends WizardNewPart {

	private NewPartWizardPage fNewPagePage;
	private Object dataContext;

	public NewPartWizard(IFile selectedFile, MPart part, Object dataContext) {
		super(selectedFile, part);
		this.dataContext = dataContext;
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Part");
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
		fNewPagePage = new NewPartWizardPage(superClass, dataContext);
		IStructuredSelection selection = getSelection();
		if (selection == null) {
			selection = new StructuredSelection(JavaCore.create(fFile
					.getProject()));
		}
		fNewPagePage.init(selection);
		addPage(fNewPagePage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !fNewPagePage.isEnclosingTypeSelected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fNewPagePage.createType(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaElement getCreatedElement() {
		return fNewPagePage.getCreatedType();
	}

}
