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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.ProjectLoader;
import org.eclipse.e4.xwt.ui.workbench.editors.XWTSaveablePart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTAbstractPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTDynamicPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTInputPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTSelectionStaticPart;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewPartWizardPage extends WizardCreatePartPage {

	private String superClassName = null;

	public static final String OPT_STATIC = "Static";
	public static final String OPT_SELECTION = "Selection";
	public static final String OPT_INPUT = "Input";
	public static final String OPT_SAVEABLE = "Saveable";
	public static final String OPT_DYNAMIC = "Dynamic";
	public static final String OPT_CUSTOM = "Custom";

	private Button staticButton;
	private Button selectionButton;
	private Button inputButton;
	private Button saveableButton;
	private Button dynamicButton;
	private Button customButton;

	private boolean creatingFile = true;
	private StringButtonDialogField dataContextField;
	private Button xwtOptionButton;

	public NewPartWizardPage(String superClass, Object dataContext) {
		this.superClassName = superClass;
		setDataContext(dataContext);
		if (dataContext != null || superClassName != null) {
		}
		setUsingXWT(dataContext != null || superClassName != null);
		setTitle("New Part Creation");
		setDescription("This wizard creates a Part");
	}

	protected void createAdditionalControl(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("XWT");
		label.setToolTipText("Using XWT to create new Part.");
		GridData gdLabel = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		label.setLayoutData(gdLabel);

		xwtOptionButton = new Button(composite, SWT.CHECK);
		xwtOptionButton.setText("Choose to create new Part with XWT templates");
		xwtOptionButton.setLayoutData(GridDataFactory.fillDefaults().span(3, 1)
				.create());
		xwtOptionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setUsingXWT(xwtOptionButton.getSelection());
				setOptionsEnabled(isUsingXWT());
			}
		});
		xwtOptionButton.setSelection(isUsingXWT());

		new Label(composite, SWT.NONE);

		final Composite xwtOptions = new Composite(composite, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = nColumns - 1;
		xwtOptions.setLayoutData(layoutData);

		GridLayout ly = new GridLayout(4, false);
		ly.marginWidth = 0;
		ly.marginHeight = 0;
		xwtOptions.setLayout(ly);

		if (superClassName == null) {
			createOptionsControls(xwtOptions, 4);
		}

		createDataContextControls(xwtOptions, 4);

		setOptionsEnabled(isUsingXWT());
	}

	protected void setOptionsEnabled(boolean enabled) {
		if (dataContextField != null) {
			dataContextField.setEnabled(enabled);
		}
		if (staticButton != null && !staticButton.isDisposed()) {
			staticButton.setEnabled(enabled);
		}
		if (selectionButton != null && !selectionButton.isDisposed()) {
			selectionButton.setEnabled(enabled);
		}
		if (inputButton != null && !inputButton.isDisposed()) {
			inputButton.setEnabled(enabled);
		}
		if (saveableButton != null && !saveableButton.isDisposed()) {
			saveableButton.setEnabled(enabled);
		}
		if (dynamicButton != null && !dynamicButton.isDisposed()) {
			dynamicButton.setEnabled(enabled);
		}
		if (customButton != null && !customButton.isDisposed()) {
			customButton.setEnabled(enabled);
		}
	}

	@SuppressWarnings("restriction")
	protected void createDataContextControls(Composite composite, int nColumns) {
		new Label(composite, SWT.NONE);
		DataContextFieldAdapter adapter = new DataContextFieldAdapter();
		dataContextField = new StringButtonDialogField(adapter) {
			public Control[] doFillIntoGrid(Composite parent, int nColumns) {
				assertEnoughColumns(nColumns);

				Label label = getLabelControl(parent);
				label.setLayoutData(gridDataForLabel(1));
				Text text = getTextControl(parent);
				GridData gd = gridDataForText(nColumns - 2);
				gd.grabExcessHorizontalSpace = true;
				text.setLayoutData(gd);
				Button button = getChangeControl(parent);
				button.setLayoutData(gridDataForButton(button, 1));

				return new Control[]{label, text, button};
			}

		};
		dataContextField.setDialogFieldListener(adapter);
		dataContextField.setButtonLabel("Browser...");
		dataContextField.setLabelText("DataContext:");
		dataContextField.doFillIntoGrid(composite, nColumns - 1);

		if (dataContext != null) {
			if (dataContext instanceof Class<?>) {
				setDataContextType((Class<?>) dataContext);
			} else if (dataContext instanceof EClass) {
				EClass dataContextType = (EClass) dataContext;
				dataContextField.setText(dataContextType.getInstanceTypeName());
			} else {
				setDataContextType(dataContext.getClass());
			}
		}
	}

	public void setDataContextType(Class<?> dataContextType) {
		IJavaProject project = getJavaProject();
		try {
			IType type = project.findType(dataContextType.getName());
			setPackageFragment(type.getPackageFragment(), true);
			setTypeName(type.getElementName() + "Part", true);
			dataContextField.setText(type.getFullyQualifiedName());
		} catch (JavaModelException e) {
		}
	}

	public IType chooseDataContext() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[]{project};
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				getShell(), false, getWizard().getContainer(), scope,
				IJavaSearchConstants.CLASS);
		dialog.setTitle("Choose a JavaBean");
		dialog.setMessage("Choose a JavaBean as a DataContext Type.");
		dialog.setInitialPattern("java.lang.Object");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected void createOptionsControls(Composite parent, int nColumns) {
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				boolean selection = button.getSelection();
				if (button == staticButton) {
					selectionButton.setEnabled(selection);
					inputButton.setEnabled(selection);
					saveableButton.setEnabled(selection);
					creatingFile = selection;
					dataContextField.setEnabled(true);
				} else if (selection && button == dynamicButton) {
					superClassName = XWTDynamicPart.class.getName();
					dataContextField.setEnabled(false);
				} else if (selection && button == customButton) {
					superClassName = XWTAbstractPart.class.getName();
					dataContextField.setEnabled(true);
				} else if (selection && staticButton.getSelection()) {
					if (button == selectionButton) {
						superClassName = XWTSelectionStaticPart.class.getName();
					} else if (button == inputButton) {
						superClassName = XWTInputPart.class.getName();
					} else if (button == saveableButton) {
						superClassName = XWTSaveablePart.class.getName();
					}
				}
			}
		};

		new Label(parent, SWT.NONE);
		staticButton = new Button(parent, SWT.RADIO);
		staticButton.setText(OPT_STATIC);
		staticButton.setSelection(true);
		superClassName = XWTStaticPart.class.getName();
		staticButton.setToolTipText("Create new Part with XWTStaticPart");
		staticButton.addListener(SWT.Selection, listener);

		Composite staticComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		staticComp.setLayout(layout);
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.GRAB_HORIZONTAL);
		// layoutData.horizontalIndent = -20;
		layoutData.horizontalSpan = 2;
		staticComp.setLayoutData(layoutData);

		selectionButton = new Button(staticComp, SWT.RADIO);
		selectionButton.setText(OPT_SELECTION);
		selectionButton
				.setToolTipText("Create new Part with XWTSelectionStaticPart");
		selectionButton.addListener(SWT.Selection, listener);

		inputButton = new Button(staticComp, SWT.RADIO);
		inputButton.setText(OPT_INPUT);
		inputButton.setToolTipText("Create new Part with XWTInputPart");
		inputButton.addListener(SWT.Selection, listener);

		saveableButton = new Button(staticComp, SWT.RADIO);
		saveableButton.setText(OPT_SAVEABLE);
		saveableButton.setToolTipText("Create new Part with XWTSaveablePart");
		saveableButton.addListener(SWT.Selection, listener);

		new Label(parent, SWT.NONE);
		dynamicButton = new Button(parent, SWT.RADIO);
		dynamicButton.setText(OPT_DYNAMIC);
		dynamicButton.setToolTipText("Create new Part with XWTDynamicPart");
		dynamicButton.setLayoutData(GridDataFactory.fillDefaults().span(3, 1)
				.create());
		dynamicButton.addListener(SWT.Selection, listener);

		new Label(parent, SWT.NONE);
		customButton = new Button(parent, SWT.RADIO);
		customButton.setText(OPT_CUSTOM);
		customButton.setLayoutData(GridDataFactory.fillDefaults().span(3, 1)
				.create());
		customButton.setToolTipText("Create new Part with XWTAbstractPart");
		customButton.addListener(SWT.Selection, listener);
	}

	public String getSuperClass() {
		if (superClassName == null || !isUsingXWT) {
			return "java.lang.Object";
		}
		return superClassName;
	}

	protected Class<?> getDataContextJavaType() {
		if (dataContext != null) {
			if (dataContext instanceof Class<?>) {
				return (Class<?>) dataContext;
			} else if (dataContext instanceof EClass) {
				throw new UnsupportedOperationException();
			} else {
				return dataContext.getClass();
			}
		}
		return null;
	}

	protected boolean isCreatingFiles() {
		return super.isCreatingFiles() && creatingFile;
	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(type, imports, monitor);
		Class<?> dataContextType = getDataContextJavaType();
		if (dataContextType != null) {
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			String comment = CodeGeneration
					.getMethodComment(
							type.getCompilationUnit(),
							type.getTypeQualifiedName('.'),
							"getDataContext", new String[0], new String[0], Signature.createTypeSignature(Object.class.getName(), true), null, lineDelim); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
			buf.append("public Object getDataContext() {"); //$NON-NLS-1$
			buf.append(lineDelim);
			final String content = "    return new "
					+ dataContextType.getSimpleName() + "();";
			imports.addImport(dataContextType.getName());
			if (content != null && content.length() != 0)
				buf.append(content);
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		}
	}

	public void validateDataContext(String dataContext) {
		String newMessage = "Invalid Java Type for initializing DataContext.";
		String errorMessage = getErrorMessage();
		if (dataContext != null) {
			ProjectLoader context = new ProjectLoader(getJavaProject());
			try {
				this.dataContext = context.loadClass(dataContext);
				if (newMessage.equals(errorMessage)) {
					setErrorMessage(null);
				} else {
					setErrorMessage(errorMessage);
				}
			} catch (ClassNotFoundException e) {
				setErrorMessage(newMessage);
			}
		} else {
			if (newMessage.equals(errorMessage)) {
				setErrorMessage(null);
			} else {
				setErrorMessage(errorMessage);
			}
		}
		setPageComplete(getErrorMessage() == null);
	}

	private class DataContextFieldAdapter
			implements
				IStringButtonAdapter,
				IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			IType type = chooseDataContext();
			if (type != null) {
				((StringButtonDialogField) field).setText(type
						.getFullyQualifiedName());
			}
		}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			validateDataContext(((StringButtonDialogField) field).getText());
		}
	}

}
