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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.CodeGeneration;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartWizardPage extends WizardCreatePartPage {

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(type, imports, monitor);
		if (getDataContext() != null) {
			// createGetDataContextMethod(type, imports, monitor);
			createSetSelectionMethod(type, imports, monitor);
		}
	}

	private void createSetSelectionMethod(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		Class<?> dataContextType = (Class<?>) getDataContext();
		try {
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			String dataContextName = dataContextType.getSimpleName();
			StringBuffer buf = new StringBuffer();

			buf.append("@Inject");
			buf.append(lineDelim);
			buf.append("public void setSelection(@Optional @Named(IServiceConstants.SELECTION) "
					+ dataContextName + " dataContext) {");
			buf.append(lineDelim);
			buf.append("	setDataContext(dataContext);");
			buf.append(lineDelim);
			buf.append("}");

			imports.addImport(Inject.class.getName());
			imports.addImport(Optional.class.getName());
			imports.addImport(Named.class.getName());
			imports.addImport(IServiceConstants.class.getName());
			imports.addImport(dataContextType.getName());
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
		}
	}

	protected void createGetDataContextMethod(IType type,
			ImportsManager imports, IProgressMonitor monitor) {
		try {
			Class<?> dataContextType = (Class<?>) getDataContext();
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

			String dataContextName = dataContextType.getSimpleName();

			imports.addImport(IServiceConstants.class.getName());
			imports.addImport(dataContextType.getName());

			buf.append("\tObject object = getContext().get(IServiceConstants.SELECTION);");
			buf.append(lineDelim);
			buf.append("\tif (object instanceof " + dataContextName + "){");
			buf.append(lineDelim);
			buf.append("\t\treturn (" + dataContextName + ")object;");
			buf.append(lineDelim);
			buf.append("\t}");
			buf.append(lineDelim);
			buf.append("\treturn null;");
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$

			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
		}
	}
	
	public String getSuperClass() {
		return XWTStaticPart.class.getName();
	}
}
