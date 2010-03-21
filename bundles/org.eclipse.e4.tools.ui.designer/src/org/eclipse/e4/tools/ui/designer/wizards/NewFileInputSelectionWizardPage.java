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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewFileInputSelectionWizardPage extends WizardPage {

	private TreeViewer treeViewer;
	private Label detailLabel;

	private NewFileInputPartWizardPage fPartPage;

	protected NewFileInputSelectionWizardPage(
			NewFileInputPartWizardPage partPage) {
		super("NewFileInputSelectionWizardPage");
		this.fPartPage = partPage;

		setTitle("File Selection");
		setMessage("Choose a *.xmi file with EMF dynamic models.");
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		treeViewer = new TreeViewer(control);
		treeViewer.getTree().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).create());
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IContainer) {
					return true;
				} else if (element instanceof IFile) {
					String fileExtension = ((IFile) element).getFileExtension();
					return "xmi".equals(fileExtension);
				}
				return false;
			}
		});
		treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelection();
			}
		});

		detailLabel = new Label(control, SWT.NONE);
		detailLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true,
				false).create());

		setControl(control);
		Dialog.applyDialogFont(control);
	}

	protected void handleSelection() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		Object firstElement = selection.getFirstElement();
		if (firstElement != null
				&& firstElement instanceof IFile
				&& ("xmi".equalsIgnoreCase(((IFile) firstElement)
						.getFileExtension()))) {
			fPartPage.setInput((IFile) firstElement);
			setErrorMessage(null);
		} else {
			fPartPage.setInput(null);
			setErrorMessage("The selected file is invalid.");
		}
		getContainer().updateButtons();
	}

	public boolean canFlipToNextPage() {
		return getErrorMessage() == null;
	}
}
