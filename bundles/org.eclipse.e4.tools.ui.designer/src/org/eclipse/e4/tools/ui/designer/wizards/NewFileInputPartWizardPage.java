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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.utils.EMFCodegen;
import org.eclipse.e4.tools.ui.designer.wizards.NewFileInputPartWizard.DataContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewFileInputPartWizardPage extends NewDataPartWizardPage {

	private DataContext inputData;

	public NewFileInputPartWizardPage(DataContext inputData) {
		super(null, null);
		this.inputData = inputData;
		inputData.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				handleDataContextChanged();
			}
		});
	}

	protected void handleDataContextChanged() {
		EObject eObject = inputData.getEObject();
		setDataContext(eObject);
	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		// createGetDataContextTypeMethos(type, imports, monitor);
		super.createTypeMembers(type, imports, monitor);
		if (!inputData.getMasterFeatures().isEmpty()) {
			createEventHandlers(type, imports, monitor);
		}
	}

	protected void createSetSelectionMethod(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		// do nothing here.
	}

	private void createEventHandlers(IType type, ImportsManager imports,
			IProgressMonitor monitor) {
		try {
			final String lineDelim = "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			buf.append("//Handle Selection Event.");
			buf.append(lineDelim);
			imports.addImport(Event.class.getName());
			imports.addImport(TreeViewer.class.getName());
			imports.addImport(IStructuredSelection.class.getName());
			imports.addImport(IServiceConstants.class.getName());
			buf.append("protected void handleSelectionEvent(Object object, Event event) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\tViewer localViewer = UserData.getLocalViewer(object);"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\tif (localViewer != null) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t\tIStructuredSelection selection = (IStructuredSelection) localViewer.getSelection();"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t\tgetContext().modify(IServiceConstants.SELECTION, selection.size() == 1 ? selection.getFirstElement() : selection.toArray());"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("\t}");
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			imports.addImport(Viewer.class.getName());
			imports.addImport(UserData.class.getName());
			type.createMethod(buf.toString(), null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

			buf.append("\tResourceSet rs = new ResourceSetImpl();");
			buf.append(lineDelim);
			imports.addImport(ResourceSet.class.getName());
			imports.addImport(ResourceSetImpl.class.getName());

			buf.append("\tResource resource = null;");
			buf.append(lineDelim);
			imports.addImport(Resource.class.getName());

			String filePath = inputData.getInput().getFullPath().toString();

			buf.append("\ttry {");
			buf.append(lineDelim);

			buf.append("\t\tURI uri = URI.createPlatformPluginURI(\""
					+ filePath + "\", true);");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\tresource = rs.getResource(uri, true);");
			buf.append(lineDelim);

			buf.append("\t} catch (Exception e) {");
			buf.append(lineDelim);

			buf.append("\t\ttry {");
			buf.append(lineDelim);

			buf.append("\t\t\tURI uri = URI.createPlatformResourceURI(\""
					+ filePath + "\", true);");
			buf.append(lineDelim);
			imports.addImport(URI.class.getName());

			buf.append("\t\t\tresource = rs.getResource(uri, true);");
			buf.append(lineDelim);

			buf.append("\t\t} catch (Exception ex) {");
			buf.append(lineDelim);

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

			buf.append("\t\t\t\tif (\""
					+ inputData.getEObject().eClass().getName()
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

	public EPackage getEPackage() {
		EObject eObject = inputData.getEObject();
		if (eObject != null) {
			return eObject.eClass().getEPackage();
		}
		return super.getEPackage();
	}
	protected List<String> getDataContextProperties() {
		if (inputData != null) {
			List<EStructuralFeature> features = inputData.getFeatures();
			if (!features.isEmpty()) {
				List<String> dataProperties = new ArrayList<String>();
				for (EStructuralFeature sf : features) {
					dataProperties.add(sf.getName());
				}
				return dataProperties;
			}
		}
		return super.getDataContextProperties();
	}
}
