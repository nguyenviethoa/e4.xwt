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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.EMFCodegen;
import org.eclipse.e4.xwt.emf.EMFBinding;
import org.eclipse.e4.xwt.ui.utils.ProjectUtil;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.CodeGeneration;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDataPartWizardPage extends WizardCreatePartPage {

	private EPackage ePackage;

	public NewDataPartWizardPage(EPackage ePackage, EObject dataContext) {
		this.ePackage = ePackage;
		setDataContext(dataContext);
	}

	protected void checkDependencies() {
		super.checkDependencies();
		try {
			IProject project = getJavaProject().getProject();
			ProjectUtil.updateXWTEMFDependencies(project);
		} catch (Exception e) {
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
			buf.append("public EClass getDataContextType() {"); //$NON-NLS-1$
			buf.append(lineDelim);
			
			String content = EMFCodegen.genDynamicModel(imports, ePackage,
					(EObject) getDataContext(), true, monitor);
			if (content != null && content.length() != 0)
				buf.append(content);
			
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			buf.append(lineDelim);
			
			buf.append("public Object getDataContext() {"); //$NON-NLS-1$
			buf.append(lineDelim);
			// final String content = "    return new "
			// + dataContextType.getSimpleName() + "();";

			content = EMFCodegen.genDynamicContents(imports, ePackage,
					(EObject) getDataContext(), true, monitor);

			if (content != null && content.length() != 0)
				buf.append(content);
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSuperClass() {
		return XWTStaticPart.class.getName();
	}
}
