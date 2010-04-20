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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDynamicFileSelectionWizardPage
		extends
			AbstractDataContextSelectionWizardPage {

	private static final String LOAD_TYPE_ERROR = "Can not load dynamic instances from give file: ";

	protected NewDynamicFileSelectionWizardPage(PartDataContext dataContext) {
		super(dataContext, "NewFileInputSelectionWizardPage");
		this.dataContext = dataContext;

		setTitle("Dynamic Instance Selection");
		setMessage("Choose a *.* file with EMF dynamic instances.");
	}

	protected IFile chooseSource() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle("Input Selection Dialog");
		dialog.setMessage("Choose a file with emf models as input.");
		dialog.setAllowMultiple(false);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length != 1) {
					return new Status(IStatus.ERROR,
							E4DesignerPlugin.PLUGIN_ID,
							"Invalid selection items.");
				}
				Object sel = selection[0];
				if (!(sel instanceof IFile)) {
					return new Status(IStatus.ERROR,
							E4DesignerPlugin.PLUGIN_ID,
							"Selection is not a File.");
				}
				return Status.OK_STATUS;
			}
		});
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IProject) {
					return ((IProject) element).isOpen();
				}
				return true;
			}
		});
		if (Window.OK == dialog.open()) {
			return (IFile) dialog.getResult()[0];
		}
		return null;
	}

	protected Object[] computeDataContext(IFile source) {
		EClassifier[] eObjects = loadEObjects(source);
		if (eObjects == null) {
			setErrorMessage(LOAD_TYPE_ERROR + "\'"
					+ source.getProjectRelativePath().toString() + "\'");
		} else if (getErrorMessage() != null
				&& getErrorMessage().startsWith(LOAD_TYPE_ERROR)) {
			setErrorMessage(null);
		}
		return eObjects;
	}
}
