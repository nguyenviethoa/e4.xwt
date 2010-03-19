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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
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

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartDataContextPage extends WizardPage {

	private TreeViewer treeViewer;
	private Label detailLabel;

	private IType selectionType;
	private NewSelectionPartWizardPage fTypePage;

	protected NewSelectionPartDataContextPage(NewSelectionPartWizardPage typePage) {
		super("DataContextSelectionPage");
		this.fTypePage = typePage;
		setTitle("Data Context Selection");
		setMessage("Choose a Model as data context to create part.");
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		treeViewer = new TreeViewer(control);
		treeViewer.getTree().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).create());
		treeViewer.setContentProvider(new StandardJavaElementContentProvider());
		treeViewer.setLabelProvider(new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_DEFAULT));
		treeViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				try {
					if (element instanceof IJavaProject
							|| element instanceof ICompilationUnit
							|| element instanceof IType) {
						return true;
					} else if (element instanceof IPackageFragmentRoot) {
						return IPackageFragmentRoot.K_SOURCE == ((IPackageFragmentRoot) element)
								.getKind();
					} else if (element instanceof IPackageFragment) {
						return ((IPackageFragment) element).getChildren().length > 0;
					}
				} catch (JavaModelException e) {
				}
				return false;
			}
		});
		treeViewer.setInput(JavaCore.create(ResourcesPlugin.getWorkspace()
				.getRoot()));
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelection();
			}
		});

		detailLabel = new Label(control, SWT.NONE);
		detailLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true,
				false).create());

		setControl(control);
		setSelectionType(null);
		Dialog.applyDialogFont(control);
	}

	protected void handleSelection() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		Object firstElement = selection.getFirstElement();

		IType type = null;
		if (firstElement instanceof IType) {
			type = (IType) firstElement;
		} else if (firstElement instanceof ICompilationUnit) {
			type = ((ICompilationUnit) firstElement).findPrimaryType();
		}
		setSelectionType(type);
	}

	private void setSelectionType(IType type) {
		this.selectionType = type;
		if (selectionType == null) {
			setErrorMessage("Selection Type is empty.");
			if (detailLabel != null && !detailLabel.isDisposed()) {
				detailLabel.setText("");
			}
			fTypePage.setDataContext(null);
		} else {
			setErrorMessage(null);
			if (detailLabel != null && !detailLabel.isDisposed()) {
				detailLabel.setText(selectionType.getFullyQualifiedName());
			}
			Class<?> dataContextType = ProjectContext.getContext(
					type.getJavaProject()).loadClass(
					type.getFullyQualifiedName());
			fTypePage.setDataContext(dataContextType);
		}
	}

	public IType getSelectionType() {
		return selectionType;
	}
}
