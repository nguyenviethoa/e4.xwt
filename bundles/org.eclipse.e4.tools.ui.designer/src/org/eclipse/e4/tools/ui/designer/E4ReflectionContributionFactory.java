/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Soyatec - adapt for e4 Designer
 ******************************************************************************/
package org.eclipse.e4.tools.ui.designer;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.tools.ui.designer.utils.ProjectLoader;
import org.eclipse.e4.ui.internal.workbench.Activator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class E4ReflectionContributionFactory implements IContributionFactory {

	private IExtensionRegistry registry;
	private Map<String, Object> languages;
	private IJavaProject project;
	private ProjectLoader projectLoader;
	
	/**
	 * Create a reflection factory.
	 * 
	 * @param registry
	 *            to read languages.
	 */
	public E4ReflectionContributionFactory(IProject project, IExtensionRegistry registry) {
		this.registry = registry;
		this.project = JavaCore.create(project);
		this.projectLoader = new ProjectLoader(this.project);
		processLanguages();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.core.services.IContributionFactory#create(java.lang.String
	 * , org.eclipse.e4.core.services.context.IEclipseContext)
	 */
	public Object create(String uriString, IEclipseContext context) {
//		Object contribution = contributionFactory.create(uriString, context);
//		if (contribution != null) {
//			return contribution;
//		}
		
		Object contribution;
		try {
			Class<?> targetClass = projectLoader.loadClass(uriString);
			contribution = ContextInjectionFactory.make(targetClass,
					context);
		} catch (ClassNotFoundException e) {
			contribution = null;
//			String message = "Unable to load class '" + clazz + "' from bundle '" //$NON-NLS-1$ //$NON-NLS-2$
//					+ bundle.getBundleId() + "'"; //$NON-NLS-1$
//			Activator.log(LogService.LOG_ERROR, message, e);
		} catch (Throwable e) {
			e.printStackTrace();
			contribution = null;
		}
		return contribution;
	}

	private void processLanguages() {
		languages = new HashMap<String, Object>();
		String extId = "org.eclipse.e4.languages"; //$NON-NLS-1$
		IConfigurationElement[] languageElements = registry
				.getConfigurationElementsFor(extId);
		for (int i = 0; i < languageElements.length; i++) {
			IConfigurationElement languageElement = languageElements[i];
			try {
				languages
						.put(
								languageElement.getAttribute("name"), //$NON-NLS-1$
								languageElement
										.createExecutableExtension("contributionFactory")); //$NON-NLS-1$
			} catch (InvalidRegistryObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected Bundle getBundle(URI platformURI) {
		return Activator.getDefault().getBundleForName(platformURI.segment(1));
	}

	public Bundle getBundle(String uriString) {
		URI uri = URI.createURI(uriString);
		return getBundle(uri);
	}
}
