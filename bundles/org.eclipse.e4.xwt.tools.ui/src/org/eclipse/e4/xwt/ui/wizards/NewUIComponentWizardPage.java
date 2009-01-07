/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ui.ExceptionHandle;
import org.eclipse.e4.xwt.ui.XWTUIPlugin;
import org.eclipse.e4.xwt.ui.jdt.ProjectHelper;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class NewUIComponentWizardPage extends org.eclipse.jdt.ui.wizards.NewClassWizardPage {
	public static final String VIEW_LOCATION = "Resources/vues/";

	protected TableViewer modelTableViewer;

	protected String superClass;

	protected IResource guiResource;

	public NewUIComponentWizardPage() {
		setTitle("New Wizard Creation");
		setDescription("This wizard creates a *.xwt file with java host class.");
	}

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

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createSeparator(composite, nColumns);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	public boolean isCreateConstructors() {
		return true;
	}

	@Override
	protected void initTypePage(IJavaElement elem) {
		super.initTypePage(elem);
		setSuperClass(getSuperClassName(), false);
	}

	protected String getSuperClassName() {
		return Composite.class.getName();
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (modelTableViewer != null && modelTableViewer.getSelection().isEmpty() && getErrorMessage() == null) {
			setErrorMessage("Veuillez selectionner le type de vue.");
		}
	}

	public int getModifiers() {
		return F_PUBLIC;
	}

	/**
	 * Returns the content of the superclass input field.
	 * 
	 * @return the superclass name
	 */
	public String getSuperClass() {
		return super.getSuperClass();
	}

	/**
	 * Returns the chosen super interfaces.
	 * 
	 * @return a list of chosen super interfaces. The list's elements are of type <code>String</code>
	 */
	public List getSuperInterfaces() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns the current selection state of the 'Create Main' checkbox.
	 * 
	 * @return the selection state of the 'Create Main' checkbox
	 */
	public boolean isCreateMain() {
		return false;
	}

	protected InputStream getContentStream() {
		IType type = getCreatedType();
		String hostClassName = type.getFullyQualifiedName();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);

		printStream.println("<j:" + type.getElementName() + " xmlns=\"" + IConstants.XWT_NAMESPACE + "\"");
		if (hostClassName != null) {
			printStream.println("\t xmlns:x=\"" + IConstants.XWT_X_NAMESPACE + "\"");
			String packageName = type.getPackageFragment().getElementName();
			if (packageName != null && packageName.length() > 0) {
				printStream.println("\t xmlns:j=\"" + IConstants.XWT_CLR_NAMESPACE_PROTO + packageName + "\"");
			}
			printStream.println("\t x:Class=\"" + hostClassName + "\">");
		} else {
			printStream.println("\t xmlns:x=\"" + IConstants.XWT_X_NAMESPACE + "\">");
		}
		printStream.println("</j:" + type.getElementName() + ">");

		try {
			byte[] content = arrayOutputStream.toByteArray();
			printStream.close();
			arrayOutputStream.close();
			return new ByteArrayInputStream(content);
		} catch (Exception e) {
			XWTUIPlugin.log(e);
			ExceptionHandle.handle(e, "save failed in the file: " + getModifiedResource().getLocation());
		}
		return new ByteArrayInputStream(new byte[] {});
	}

	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
		// Add external Jars before create a new Java Source Type.
		ProjectHelper.checkDependenceJars(getJavaProject());
		super.createType(monitor);

		IResource resource = getModifiedResource();
		IPath resourcePath = resource.getProjectRelativePath().removeFileExtension();
		resourcePath = resourcePath.addFileExtension(IConstants.XWT_EXTENSION);
		try {
			IFile file = resource.getProject().getFile(resourcePath);
			file.create(getContentStream(), IResource.FORCE | IResource.KEEP_HISTORY, monitor);
			guiResource = file;
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandle.handle(e, "save failed in the file: " + getModifiedResource().getLocation());
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#getTypeName()
	 */
	public String getTypeName() {
		String typeName = super.getTypeName();
		if (typeName == null || typeName.equals("")) {
			return typeName;
		}
		/*
		 * Make sure the first character of the new Class name is a upperCase one. Because the Element parser of the XWT file convert the top element to this format.
		 */
		return Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
	}

	@Override
	public boolean isPageComplete() {
		if (modelTableViewer != null && modelTableViewer.getSelection().isEmpty()) {
			setErrorMessage("Veuillez selectionner le type mdoèlde de vue.");
			return false;
		}
		return super.isPageComplete();
	}

	/**
	 * Returns the current selection state of the 'Create inherited abstract methods' checkbox.
	 * 
	 * @return the selection state of the 'Create inherited abstract methods' checkbox
	 */
	public boolean isCreateInherited() {
		return true;
	}

	public IResource getGuiResource() {
		return guiResource;
	}
}
