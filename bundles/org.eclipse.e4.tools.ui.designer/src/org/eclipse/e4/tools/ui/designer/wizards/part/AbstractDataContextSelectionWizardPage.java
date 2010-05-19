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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class AbstractDataContextSelectionWizardPage extends WizardPage {

	private static final String SOURCE_EMPTY_ERROR = "Data Context source is empty.";
	private static final String PDC_TYPE_EMPTY_ERROR = "Data Context Selection is null.";

	protected PartDataContext dataContext;

	private Label sourceLabel;
	private Text sourceText;
	private Button browserButton;
	private TableViewer dataContextViewer;
	private ExpandableComposite dataContextComp;

	private IFile source;

	public AbstractDataContextSelectionWizardPage(PartDataContext dataContext,
			String pageName) {
		super(pageName);
		this.dataContext = dataContext;
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(3, false));

		sourceLabel = new Label(control, SWT.NONE);;
		sourceLabel.setText("Source");

		sourceText = new Text(control, SWT.BORDER);
		sourceText.setLayoutData(GridDataFactory.fillDefaults().grab(true,
				false).create());

		browserButton = new Button(control, SWT.NONE);
		browserButton.setText("Browser...");
		browserButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setSource(chooseSource());
			}
		});

		dataContextComp = new ExpandableComposite(control, SWT.NONE);
		dataContextComp.setText("Data Context");
		dataContextComp.setLayoutData(GridDataFactory.fillDefaults().grab(true,
				true).span(3, 1).create());
		SashForm container = new SashForm(dataContextComp, SWT.VERTICAL);
		
		Composite dataTypeContainer = new Composite(container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		dataTypeContainer.setLayout(gridLayout);

		Label label = new Label(dataTypeContainer, SWT.NONE);
		label.setText("Data Type:");

		dataContextViewer = createDataContextViewer(dataTypeContainer);
		Assert.isNotNull(dataContextViewer);
		dataContextViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		dataContextViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) dataContextViewer
								.getSelection();
						dataContext.setType(selection.getFirstElement());
						validate();
					}
				});
		new PropertiesComposite(container, dataContext, false);

		container.setWeights(new int[]{1, 2});

		dataContextComp.setClient(container);
		dataContextComp.setExpanded(false);

		setControl(control);
		Dialog.applyDialogFont(control);

		validate();
	}

	protected TableViewer createDataContextViewer(Composite parent) {		
		final TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				return new Object[0];
			}
		});
		tableViewer.setLabelProvider(new PDCTypeLabelProvider());

		return tableViewer;
	}

	public TableViewer getDataContextViewer() {
		return dataContextViewer;
	}

	public PartDataContext getDataContext() {
		return dataContext;
	}

	private void setSource(IFile source) {
		this.source = source;
		if (dataContextViewer != null && dataContextViewer.getControl() != null
				&& !dataContextViewer.getControl().isDisposed()) {
			Object[] dataContexts = computeDataContext(source);
			dataContextViewer.setInput(dataContexts);
			if (dataContexts != null && dataContexts.length > 0) {
				dataContextViewer.setSelection(new StructuredSelection(
						dataContexts[0]));
			}
		}
		if (sourceText != null && !sourceText.isDisposed()) {
			sourceText.setText(source == null ? "" : source
					.getProjectRelativePath().toString());
		}
		if (dataContextComp != null && !dataContextComp.isDisposed()) {
			dataContextComp.setExpanded(source != null);
		}
		if (source != null) {
			dataContext.setSource(source);
		} else {
			dataContext.setSource(null);
		}
		validate();
	}

	protected void validate() {
		if (source == null) {
			setErrorMessage(SOURCE_EMPTY_ERROR);
		} else if (SOURCE_EMPTY_ERROR.equals(getErrorMessage())) {
			setErrorMessage(null);
		}
		if (getErrorMessage() == null
				|| PDC_TYPE_EMPTY_ERROR.equals(getErrorMessage())) {
			if (dataContext.getType() == null) {
				setErrorMessage(PDC_TYPE_EMPTY_ERROR);
			} else {
				setErrorMessage(null);
			}
		}
		try {
			// safely update buttons.
			getContainer().updateButtons();
		} catch (Exception e) {
		}
	}

	protected IFile chooseSource() {
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(),
				IResource.FILE) {
			private boolean isCreating;
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				isCreating = true;
				refresh(true);
				isCreating = false;
				return control;
			}
			protected String adjustPattern() {
				String adjustPattern = super.adjustPattern();
				if (isCreating && "".equals(adjustPattern)) {
					return "*.java";
				}
				return adjustPattern;
			}
		};
		dialog.setTitle("Data Context Selection Dialog");
		dialog.setMessage("Select a *.java or EMF model file to retrieve data context.");
		if (Window.OK == dialog.open()) {
			Object[] result = dialog.getResult();
			if (result.length >= 1) {
				IFile file = (IFile) result[0];
				return file;
			}
		}
		return null;
	}

	public IFile getSource() {
		return source;
	}

	public boolean isPageComplete() {
		return super.isPageComplete() && getErrorMessage() == null;
	}

	protected abstract Object[] computeDataContext(IFile source);

	public static EClassifier[] loadEObjects(IFile file) {
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = null;
		try {
			URI uri = URI.createPlatformPluginURI(
					file.getFullPath().toString(), true);
			resource = rs.getResource(uri, true);
		} catch (Exception e) {
			try {
				URI uri = URI.createPlatformResourceURI(file.getFullPath()
						.toString(), true);
				resource = rs.getResource(uri, true);
			} catch (Exception e1) {
				try {
					URI uri = URI.createFileURI(file.getLocation().toString());
					resource = rs.getResource(uri, true);
				} catch (Exception e2) {
				}
			}
		}
		if (resource == null) {
			return null;
		}
		Set<EPackage> packages = new HashSet<EPackage>();
		EList<EObject> contents = resource.getContents();
		for (EObject eObject : contents) {
			if (eObject instanceof EPackage) {
				packages.add((EPackage) eObject);
			} else {
				packages.add(eObject.eClass().getEPackage());
			}
		}
		List<EClassifier> result = new ArrayList<EClassifier>();
		for (EPackage eObject : packages) {
			result.addAll(eObject.getEClassifiers());
		}
		return result.toArray(new EClassifier[0]);
	}
}
