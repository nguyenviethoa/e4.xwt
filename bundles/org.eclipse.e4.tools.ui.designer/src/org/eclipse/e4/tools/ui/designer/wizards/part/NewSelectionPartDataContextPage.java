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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartDataContextPage
		extends
			AbstractDataContextSelectionWizardPage {

	protected NewSelectionPartDataContextPage(PartDataContext dataContext) {
		super(dataContext, "DataContextSelectionPage");
		this.dataContext = dataContext;
		setTitle("Data Context Selection");
		setMessage("Choose a Model as data context to create part.");
	}

	protected TableViewer createDataContextViewer(Composite parent) {
		TableViewer tableViewer = super.createDataContextViewer(parent);
		return tableViewer;
	}
	protected Object computeInput(IFile file) {
		String fileExtension = file.getFileExtension();
		if ("java".equals(fileExtension)) {
			ICompilationUnit javaElement = (ICompilationUnit) JavaCore
					.create(file);
			return javaElement;
		} else {
			ResourceSet rs = new ResourceSetImpl();
			Resource resource = null;
			try {
				URI uri = URI.createPlatformPluginURI(file.getFullPath()
						.toString(), true);
				resource = rs.getResource(uri, true);
			} catch (Exception e) {
				try {
					URI uri = URI.createPlatformResourceURI(file.getFullPath()
							.toString(), true);
					resource = rs.getResource(uri, true);
				} catch (Exception e1) {
					try {
						URI uri = URI.createFileURI(file.getLocation()
								.toString());
						resource = rs.getResource(uri, true);
					} catch (Exception e2) {
					}
				}
			}
			return resource;
		}
	}

	protected Object[] computeDataContext(IFile source) {
		if (source == null) {
			return null;
		}
		String fileExtension = source.getFileExtension();
		if ("java".equals(fileExtension)) {
			ICompilationUnit javaElement = (ICompilationUnit) JavaCore
					.create(source);
			List<Class<?>> classes = new ArrayList<Class<?>>();
			try {
				IType[] types = javaElement.getTypes();
				for (IType iType : types) {
					Class<?> beanType = getBeanType(iType);
					if (beanType == null) {
						continue;
					}
					classes.add(beanType);
				}
			} catch (JavaModelException e) {
			}
			return classes.toArray(new Class<?>[classes.size()]);
		} else {
			return loadEObjects(source);
		}
	}

	private Class<?> getBeanType(IType type) {
		try {
			IJavaProject javaProject = type.getJavaProject();
			return ProjectContext.getContext(javaProject).loadClass(
					type.getFullyQualifiedName());
		} catch (Exception e) {
		}
		return null;
	}

}
