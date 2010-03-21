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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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
public class NewFileInputPartWizardPage extends NewDataPartWizardPage {

	private IFile input;

	public NewFileInputPartWizardPage() {
		super(null, null);
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

			buf.append("\ttry {");
			buf.append(lineDelim);

			buf.append("\t\tResourceSet rs = new ResourceSetImpl();");
			buf.append(lineDelim);
			imports.addImport(ResourceSet.class.getName());
			imports.addImport(ResourceSetImpl.class.getName());

			buf.append("\t\tURI uri = URI.createPlatformResourceURI(\""
					+ input.getFullPath().toString() + "\", true);");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\tResource resource = rs.getResource(uri, true);");
			buf.append(lineDelim);
			imports.addImport(Resource.class.getName());

			buf.append("\t\tEList<EObject> contents = resource.getContents();");
			buf.append(lineDelim);
			imports.addImport(EList.class.getName());
			imports.addImport(EObject.class.getName());

			buf.append("\t\tif (!contents.isEmpty()) {");
			buf.append(lineDelim);

			buf.append("\t\t\treturn contents.get(0);");
			buf.append(lineDelim);

			buf.append("\t\t}");
			buf.append(lineDelim);

			buf.append("\t} catch (Exception e) {");
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

	public void setInput(IFile input) {
		this.input = input;
		if (input != null && input.exists()) {
			try {
				ResourceSet rs = new ResourceSetImpl();
				URI uri = URI.createPlatformResourceURI(input.getFullPath()
						.toString(), true);
				Resource resource = rs.getResource(uri, true);
				EList<EObject> contents = resource.getContents();
				if (!contents.isEmpty()) {
					EObject eObject = contents.get(0);
					EClass eClass = eObject.eClass();
					setDataContext(eObject);
					setEPackage(eClass.getEPackage());
				}
			} catch (Exception e) {
				setDataContext(null);
				setEPackage(null);
			}
		} else {
			setDataContext(null);
			setEPackage(null);
		}
	}

	public IFile getInput() {
		return input;
	}
}
