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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.EMFCodegen;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.CodeGeneration;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDynamicFilePartWizardPage extends NewEObjectPartWizardPage {

	public NewDynamicFilePartWizardPage(PartDataContext dataContext) {
		super(dataContext, null, false);
	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		// createGetDataContextTypeMethos(type, imports, monitor);
		super.createTypeMembers(type, imports, monitor);
	}

	protected void createSetSelectionMethod(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		// do nothing here.
	}

	protected void createGetDataContextTypeMethos(IType type,
			ImportsManager imports, IProgressMonitor monitor) {
		try {
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			String comment = CodeGeneration
					.getMethodComment(
							type.getCompilationUnit(),
							type.getTypeQualifiedName('.'),
							"getDataContextType", new String[0], new String[0], Signature.createTypeSignature(Object.class.getName(), true), null, lineDelim); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
			imports.addImport(EClass.class.getName());
			buf.append("public EClass getDataContextType() {"); //$NON-NLS-1$
			buf.append(lineDelim);

			String content = EMFCodegen.genDynamicModel(imports, getEPackage(),
					(EObject) dataContext.getValue(), true, monitor);
			if (content != null && content.length() != 0)
				buf.append(content);

			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void createGetDataContextMethod(IType type,
			ImportsManager imports, IProgressMonitor monitor) {
		IFile source = dataContext.getSource();
		if (source == null) {
			return;
		}
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

			buf.append("\tResourceSet rs = new ResourceSetImpl();");
			buf.append(lineDelim);
			imports.addImport(ResourceSet.class.getName());
			imports.addImport(ResourceSetImpl.class.getName());

			buf.append("\tResource resource = null;");
			buf.append(lineDelim);
			imports.addImport(Resource.class.getName());

			buf.append("\ttry {");
			buf.append(lineDelim);

			buf.append("\t\tURI uri = URI.createPlatformPluginURI(\""
					+ source.getProjectRelativePath().toString() + "\", true);");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\tresource = rs.getResource(uri, true);");
			buf.append(lineDelim);

			buf.append("\t} catch (Exception e) {");
			buf.append(lineDelim);

			buf.append("\t\ttry {");
			buf.append(lineDelim);

			buf.append("\t\t\tURI uri = URI.createPlatformResourceURI(\""
					+ source.getFullPath().toString() + "\", true);");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\t\tresource = rs.getResource(uri, true);");
			buf.append(lineDelim);

			buf.append("\t\t} catch (Exception ex) {");
			buf.append(lineDelim);

			buf.append("\t\t\ttry {");
			buf.append(lineDelim);

			buf.append("\t\t\t\tURI uri = URI.createFileURI(\""
					+ source.getLocation().toString() + "\");");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\t\t\tresource = rs.getResource(uri, true);");
			buf.append(lineDelim);

			buf.append("\t\t\t} catch (Exception exc) {");
			buf.append(lineDelim);

			buf.append("\t\t\t\t}");

			buf.append("\t\t\t}");
			buf.append(lineDelim);

			buf.append("\t\t}");
			buf.append(lineDelim);

			imports.addImport(EList.class.getName());
			imports.addImport(EObject.class.getName());

			buf.append("\t\tif (resource != null && !resource.getContents().isEmpty()) {");
			buf.append(lineDelim);

			buf.append("\t\t\tfor (EObject object : resource.getContents()) {");
			buf.append(lineDelim);

			buf.append("\t\t\t\tif (\"" + dataContext.getDisplayName()
					+ "\".equals(object.eClass().getName())) {");
			buf.append(lineDelim);

			buf.append("\t\t\t\t\treturn object;");
			buf.append(lineDelim);

			buf.append("\t\t\t}");
			buf.append(lineDelim);
			buf.append("\t\t}");
			buf.append(lineDelim);
			buf.append("\t}");
			buf.append(lineDelim);

			buf.append("\treturn null;");
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
