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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.ProjectLoader;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tools.ui.designer.core.util.XWTProjectUtil;
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
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.ui.PlatformUI;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewPartWizardPage extends NewClassWizardPage {
	private String superClassName = null;
	// private static final List<Class> SUPER_CLASSES = new ArrayList<Class>();
	// static {
	// SUPER_CLASSES.add(XWTStaticPart.class);
	// SUPER_CLASSES.add(XWTDynamicPart.class);
	// SUPER_CLASSES.add(XWTSaveablePart.class);
	// SUPER_CLASSES.add(XWTSelectionStaticPart.class);
	// SUPER_CLASSES.add(XWTAbstractPart.class);
	// // SUPER_CLASSES.add(XWTInputPart.class);
	// }

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
	private boolean usingXWT = false;
	private Object dataContext = null;
	private StringButtonDialogField dataContextField;
	private Button xwtOptionButton;

	public NewPartWizardPage(String superClass, Object dataContext) {
		this.superClassName = superClass;
		this.dataContext = dataContext;
		if (dataContext != null || superClassName != null) {
			usingXWT = true;
		}
		setTitle("New Part Creation");
		setDescription("This wizard creates a Part");
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

		// TODO should be separated in an extension point
		createXWTOptionsControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		createSeparator(composite, nColumns);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
				IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	private void createXWTOptionsControls(Composite composite, int nColumns) {
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
				usingXWT = xwtOptionButton.getSelection();
				setOptionsEnabled(usingXWT);
			}
		});
		xwtOptionButton.setSelection(usingXWT);
		
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

		setOptionsEnabled(usingXWT);
	}

	public boolean isUsingXWT(){
		return usingXWT;
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

				return new Control[] { label, text, button };
			}

		};
		dataContextField.setDialogFieldListener(adapter);
		dataContextField.setButtonLabel("Browser...");
		dataContextField.setLabelText("DataContext:");
		dataContextField.doFillIntoGrid(composite, nColumns - 1);

		if (dataContext != null) {
			if (dataContext instanceof Class<?>) {
				setDataContext((Class<?>) dataContext);
			} else if (dataContext instanceof EClass) {
				EClass dataContextType = (EClass) dataContext;
				dataContextField.setText(dataContextType.getInstanceTypeName());
			} else {
				setDataContext(dataContext.getClass());
			}
		}
	}

	protected void setDataContext(Class<?> dataContextType) {
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

		IJavaElement[] elements = new IJavaElement[] { project };
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#getSuperClass()
	 */
	public String getSuperClass() {
		if (superClassName == null || !usingXWT) {
			return "java.lang.Object";
		}
		return superClassName;
	}

	public int getModifiers() {
		return F_PUBLIC;
	}

	/**
	 * Returns the chosen super interfaces.
	 * 
	 * @return a list of chosen super interfaces. The list's elements are of
	 *         type <code>String</code>
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

	public void createType(IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		if (usingXWT){
			IProject project = getJavaProject().getProject();
			XWTProjectUtil.updateXWTWorkbenchDependencies(project);
		}
		super.createType(monitor);

		if (usingXWT && creatingFile) {
			IResource resource = getModifiedResource();
			IPath resourcePath = resource.getProjectRelativePath()
					.removeFileExtension();
			resourcePath = resourcePath
					.addFileExtension(IConstants.XWT_EXTENSION);
			try {
				IFile file = resource.getProject().getFile(resourcePath);
				file.create(getContentStream(), IResource.FORCE
						| IResource.KEEP_HISTORY, monitor);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.wizards.NewClassWizardPage#createTypeMembers(org.eclipse
	 * .jdt.core.IType,
	 * org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
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

	private InputStream getContentStream() {
		IType type = getCreatedType();
		String hostClassName = type.getFullyQualifiedName();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);

		printStream.println("<Composite xmlns=\"" + IConstants.XWT_NAMESPACE
				+ "\"");

		printStream
				.println("\t xmlns:x=\"" + IConstants.XWT_X_NAMESPACE + "\"");
		String packageName = type.getPackageFragment().getElementName();
		if (packageName != null/* && packageName.length() > 0 */) {
			printStream.println("\t xmlns:c=\""
					+ IConstants.XAML_CLR_NAMESPACE_PROTO + packageName + "\"");
		}
		printStream.println("\t xmlns:j=\""
				+ IConstants.XAML_CLR_NAMESPACE_PROTO + "java.lang\"");
		printStream.println("\t x:Class=\"" + hostClassName + "\">");
		printStream.println("\t <Composite.layout>");
		printStream.println("\t\t <GridLayout " + " numColumns=\"4\" />");
		printStream.println("\t </Composite.layout>");

		if (dataContext != null) {
			appendBeanContent(printStream);
		} else {
			printStream.println("\t <Label text=\" New "
					+ type.getElementName() + " Part\"/>");
		}

		printStream.println("</Composite>");

		try {
			byte[] content = arrayOutputStream.toByteArray();
			printStream.close();
			arrayOutputStream.close();
			return new ByteArrayInputStream(content);
		} catch (Exception e) {
		}
		return new ByteArrayInputStream(new byte[] {});
	}

	private void appendBeanContent(PrintStream printStream) {
		Class<?> type = getDataContextJavaType();
		if (type == null) {
			return;
		}
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				String name = pd.getName();
				if (name == null || "class".equals(name)) {
					continue;
				}
				Class<?> propertyType = pd.getPropertyType();
				if (propertyType.isPrimitive() || propertyType == String.class
						|| propertyType == URL.class) {
					printStream.println("\t <Label text=\""
							+ pd.getDisplayName() + "\"/>");
					printStream
							.println("\t <Text x:Style=\"Border\" text=\"{Binding path="
									+ pd.getName() + "}\">");
					printStream.println("\t\t <Text.layoutData>");
					printStream
							.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\"");
					printStream
							.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"100\"/>");
					printStream.println("\t\t </Text.layoutData>");
					printStream.println("\t </Text>");
				} else if (propertyType.isEnum()) {
					printStream.println("\t <Label text=\""
							+ pd.getDisplayName() + "\"/>");
					printStream.println("\t <Combo text=\"{Binding path="
							+ pd.getName() + "}\">");
					printStream.println("\t\t <Combo.layoutData>");
					printStream
							.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\"");
					printStream
							.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"100\"/>");
					printStream.println("\t\t </Combo.layoutData>");

					printStream.println("\t\t <Combo.items>");
					for (Object object : propertyType.getEnumConstants()) {
						printStream.println("\t\t\t <j:String>"
								+ object.toString() + "</j:String>");
					}
					printStream.println("\t\t </Combo.items>");
					printStream.println("\t </Combo>");

				} else {
					printStream.println("\t <Group text=\""
							+ pd.getDisplayName() + "\">");
					printStream.println("\t\t <Group.layout>");
					printStream.println("\t\t\t <FillLayout/>");
					printStream.println("\t\t </Group.layout>");

					String elementType = propertyType.getSimpleName();
					printStream.println("\t\t <c:" + elementType
							+ " DataContext=\"{Binding path=" + pd.getName()
							+ "}\"/>");

					printStream.println("\t\t <Group.layoutData>");
					printStream
							.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\" horizontalSpan=\"4\"");
					printStream
							.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"200\"/>");
					printStream.println("\t\t </Group.layoutData>");

					printStream.println("\t </Group>");
				}
			}
		} catch (IntrospectionException e) {
		}
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
		 * Make sure the first character of the new Class name is a upperCase
		 * one. Because the Element parser of the XWT file convert the top
		 * element to this format.
		 */
		return Character.toUpperCase(typeName.charAt(0))
				+ typeName.substring(1);
	}

	/**
	 * Returns the current selection state of the 'Create inherited abstract
	 * methods' checkbox.
	 * 
	 * @return the selection state of the 'Create inherited abstract methods'
	 *         checkbox
	 */
	public boolean isCreateInherited() {
		return true;
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

	private class DataContextFieldAdapter implements IStringButtonAdapter,
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
