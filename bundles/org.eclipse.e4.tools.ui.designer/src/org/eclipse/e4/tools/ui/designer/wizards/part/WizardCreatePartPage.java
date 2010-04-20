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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.XWTProjectUtil;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class WizardCreatePartPage extends NewClassWizardPage {

	protected PartDataContext dataContext;

	protected boolean isUsingXWT = true;

	private String selectionEventHandler = "handleSelectionEvent";

	public WizardCreatePartPage(PartDataContext dataContext) {
		this.dataContext = dataContext;
		Assert.isNotNull(dataContext, "Data Context can not be NULL.");
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

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(type, imports, monitor);
		if (dataContext.hasMasterProperties()) {
			createEventHandlers(type, imports, monitor);
		}
	}

	protected void createEventHandlers(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		try {
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			buf.append("//Handle Selection Event.");
			buf.append(lineDelim);
			imports.addImport(Event.class.getName());
			imports.addImport(TreeViewer.class.getName());
			imports.addImport(IStructuredSelection.class.getName());
			imports.addImport(IServiceConstants.class.getName());
			buf.append("protected void " + getSelectionEventHandler() + "(Object object, Event event) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\tViewer localViewer = UserData.getLocalViewer(object);"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\tif (localViewer != null) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t\tIStructuredSelection selection = (IStructuredSelection) localViewer.getSelection();"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t\tgetContext().modify(IServiceConstants.SELECTION, selection.size() == 1 ? selection.getFirstElement() : selection.toArray());"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t}");
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			imports.addImport(Viewer.class.getName());
			imports.addImport(UserData.class.getName());
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void createTypeNameControls(Composite composite, int nColumns) {
		super.createTypeNameControls(composite, nColumns);
		dataContext.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (propertyName.equals(PartDataContext.TYPE)
						|| propertyName.equals(PartDataContext.VALUE)) {
					String displayName = dataContext.getDisplayName();
					setTypeName(displayName + "Part", true);
				}
			}
		});
		setTypeName(dataContext.getDisplayName() + "Part", true);
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
			if (dataContext.hasMasterProperties()
					&& getSelectionEventHandler() != null) {
				List<Object> masterProperties = dataContext
						.getMasterProperties();
				for (Object object : masterProperties) {
					dataContext.addEventHandler(object, "Selection",
							getSelectionEventHandler());
				}
			}
			PDCCodegen.createFile(getCreatedType(), file, dataContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void checkDependencies() {
		if (isUsingXWT()) {
			IProject project = getJavaProject().getProject();
			XWTProjectUtil.updateXWTWorkbenchDependencies(project);
		}
	}

	public PartDataContext getDataContext() {
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

	public void setSelectionEventHandler(String selectionEventHandler) {
		this.selectionEventHandler = selectionEventHandler;
	}

	public String getSelectionEventHandler() {
		return selectionEventHandler;
	}
}
