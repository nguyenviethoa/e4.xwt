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
import org.eclipse.e4.tools.ui.designer.utils.EMFCodegen;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.emf.EMFBinding;
import org.eclipse.e4.xwt.ui.utils.ProjectUtil;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDataPartWizardPage extends NewClassWizardPage {

	private EPackage ePackage;
	private EObject dataContext;
	private EClass dataContextType;

	public NewDataPartWizardPage(EPackage ePackage) {
		this.ePackage = ePackage;
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
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

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
				IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	public void setDataContext(EObject dataContext) {
		this.dataContext = dataContext;
		this.dataContextType = dataContext.eClass();
		if (dataContextType != null && dataContextType.getName() != null) {
			setTypeName(dataContextType.getName() + "Part", true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createType(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void createType(IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		try {
			IProject project = getJavaProject().getProject();
			ProjectUtil.updateXWTWorkbenchDependencies(project);
			ProjectUtil.updateXWTEMFDependencies(project);
		} catch (Exception e) {
		}
		super.createType(monitor);

		IResource resource = getModifiedResource();
		IPath resourcePath = resource.getProjectRelativePath()
				.removeFileExtension();
		resourcePath = resourcePath.addFileExtension(IConstants.XWT_EXTENSION);
		try {
			IFile file = resource.getProject().getFile(resourcePath);
			file.create(getContentStream(), IResource.FORCE
					| IResource.KEEP_HISTORY, monitor);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(type, imports, monitor);
		createGetDataContextMethod(type, imports, monitor);
		overrideRefreshMethod(type, imports, monitor);
	}

	private void overrideRefreshMethod(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		try {
			final String lineDelim = "\n";
			StringBuffer buf = new StringBuffer();
			imports.addImport(URL.class.getName());
			buf.append("protected void refresh(URL url, Object dataContext, ClassLoader loader) {");
			buf.append(lineDelim);
			buf.append("EMFBinding.initialze();");
			imports.addImport(EMFBinding.class.getName());
			buf.append(lineDelim);
			buf.append("	super.refresh(url, dataContext, loader);");
			buf.append(lineDelim);
			buf.append("}");
			type.createMethod(buf.toString(), null, false, null);
		} catch (JavaModelException e) {
		}
	}

	protected void createGetDataContextMethod(IType type,
			ImportsManager imports, IProgressMonitor monitor) {
		try {
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
			// final String content = "    return new "
			// + dataContextType.getSimpleName() + "();";

			String content = EMFCodegen.genDynamicContents(imports, ePackage,
					dataContext, true, monitor);

			if (content != null && content.length() != 0)
				buf.append(content);
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
		}
	}

	protected void appendLine(StringBuffer buf, String content, String lineDelim) {
		buf.append(content);
		buf.append(lineDelim);
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
		return new ByteArrayInputStream(new byte[]{});
	}

	private void appendBeanContent(PrintStream printStream) {
		if (dataContextType == null) {
			return;
		}
		for (EStructuralFeature feature : dataContextType
				.getEStructuralFeatures()) {
			String name = feature.getName();
			if (name == null) {
				continue;
			}
			EClassifier propertyType = feature.getEType();
			if (propertyType instanceof EEnum) {
				printStream.println("\t <Label text=\"" + name + "\"/>");
				printStream.println("\t <Combo text=\"{Binding path=" + name
						+ "}\">");
				printStream.println("\t\t <Combo.layoutData>");
				printStream
						.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\"");
				printStream
						.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"100\"/>");
				printStream.println("\t\t </Combo.layoutData>");

				printStream.println("\t\t <Combo.items>");
				for (EEnumLiteral object : ((EEnum) propertyType)
						.getELiterals()) {
					printStream.println("\t\t\t <j:String>" + object.toString()
							+ "</j:String>");
				}
				printStream.println("\t\t </Combo.items>");
				printStream.println("\t </Combo>");

			}
			if (propertyType instanceof EDataType) {
				printStream.println("\t <Label text=\"" + name + "\"/>");
				printStream
						.println("\t <Text x:Style=\"Border\" text=\"{Binding path="
								+ name + "}\">");
				printStream.println("\t\t <Text.layoutData>");
				printStream
						.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\"");
				printStream
						.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"100\"/>");
				printStream.println("\t\t </Text.layoutData>");
				printStream.println("\t </Text>");
			} else {
				printStream.println("\t <Group text=\"" + name + "\">");
				printStream.println("\t\t <Group.layout>");
				printStream.println("\t\t\t <FillLayout/>");
				printStream.println("\t\t </Group.layout>");

				String elementType = propertyType.getName();
				printStream.println("\t\t <c:" + elementType
						+ " DataContext=\"{Binding path=" + name + "}\"/>");

				printStream.println("\t\t <Group.layoutData>");
				printStream
						.println("\t\t\t <GridData grabExcessHorizontalSpace=\"true\" horizontalSpan=\"4\"");
				printStream
						.println("\t\t\t\t horizontalAlignment=\"GridData.FILL\" widthHint=\"200\"/>");
				printStream.println("\t\t </Group.layoutData>");

				printStream.println("\t </Group>");
			}
		}
	}

	public String getSuperClass() {
		return XWTStaticPart.class.getName();
	}

	public int getModifiers() {
		return F_PUBLIC;
	}

	public List getSuperInterfaces() {
		return Collections.EMPTY_LIST;
	}

	public boolean isCreateMain() {
		return false;
	}
}
