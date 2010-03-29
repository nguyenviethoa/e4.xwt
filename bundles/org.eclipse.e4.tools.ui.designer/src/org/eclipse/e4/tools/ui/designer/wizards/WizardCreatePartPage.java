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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.XWTCodegen;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.XWTProjectUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class WizardCreatePartPage extends NewClassWizardPage {

	protected Object dataContext;
	private List<String> dataContextProperties;

	protected boolean isUsingXWT = true;

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createTypeNameControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createAdditionalControl(composite, nColumns);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		createSeparator(composite, nColumns);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
				IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	protected void createAdditionalControl(Composite parent, int numColumns) {
	}

	public void createType(IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		checkDependencies();
		super.createType(monitor);
		createAdditionalFiles(monitor);
	}

	protected boolean isCreatingFiles() {
		return isUsingXWT();
	}

	protected void createAdditionalFiles(IProgressMonitor monitor) {
		if (!isCreatingFiles()) {
			return;
		}
		IResource resource = getModifiedResource();
		IPath resourcePath = resource.getProjectRelativePath()
				.removeFileExtension();
		resourcePath = resourcePath.addFileExtension(IConstants.XWT_EXTENSION);
		try {
			IFile file = resource.getProject().getFile(resourcePath);
			XWTCodegen.createFile(getCreatedType(), file, dataContext,
					getDataContextProperties());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected List<String> getDataContextProperties() {
		if (dataContextProperties == null) {
			dataContextProperties = new ArrayList<String>();
			computeDataContextProperties();
		}
		return dataContextProperties;
	}

	protected void computeDataContextProperties() {

	}

	protected Object getDataContextType() {
		Object dc = getDataContext();
		if (dc == null) {
			return null;
		}
		if (dc instanceof Class<?> || dc instanceof EClass) {
			return dc;
		} else if (dc instanceof EObject) {
			return ((EObject) dc).eClass();
		}
		return dc.getClass();
	}

	public void setDataContextProperties(List<String> dataContextProperties) {
		this.dataContextProperties = dataContextProperties;
	}

	protected void checkDependencies() {
		if (isUsingXWT()) {
			IProject project = getJavaProject().getProject();
			XWTProjectUtil.updateXWTWorkbenchDependencies(project);
		}
	}

	public void setDataContext(Object dataContext) {
		this.dataContext = dataContext;
		if (dataContext == null) {
			return;
		}
		String typeName = null;
		if (dataContext instanceof EClass) {
			typeName = ((EClass) dataContext).getName();
		} else if (dataContext instanceof EObject) {
			typeName = ((EObject) dataContext).eClass().getName();
		} else if (dataContext instanceof Class<?>) {
			typeName = ((Class<?>) dataContext).getSimpleName();
		} else {
			typeName = dataContext.getClass().getSimpleName();
		}
		setTypeName(typeName + "Part", true);
	}

	public Object getDataContext() {
		return dataContext;
	}

	public int getModifiers() {
		return F_PUBLIC;
	}

	public List getSuperInterfaces() {
		return Collections.EMPTY_LIST;
	}

	public String getTypeName() {
		String typeName = super.getTypeName();
		if (typeName == null || typeName.equals("")) {
			return typeName;
		}
		/*
		 * Make sure the first character of the new Class name is a upperCase
		 * one. Because the Element parser of the XWT file convert the top
		 * element to this format.
		 */
		return Character.toUpperCase(typeName.charAt(0))
				+ typeName.substring(1);
	}

	public boolean isCreateInherited() {
		return true;
	}

	public boolean isCreateMain() {
		return false;
	}

	public void setUsingXWT(boolean isUsingXWT) {
		this.isUsingXWT = isUsingXWT;
	}

	public boolean isUsingXWT() {
		return isUsingXWT;
	}
}
