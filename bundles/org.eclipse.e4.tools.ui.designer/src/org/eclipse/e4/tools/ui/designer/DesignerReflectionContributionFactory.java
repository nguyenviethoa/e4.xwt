/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.ui.internal.workbench.ReflectionContributionFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.tools.ui.designer.session.ProjectBundleSession;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

public class DesignerReflectionContributionFactory extends
		ReflectionContributionFactory {
	private ProjectBundleSession projectBundleSession;

	public DesignerReflectionContributionFactory(IExtensionRegistry registry,
			ProjectBundleSession projectBundleSession) {
		super(registry);
		this.projectBundleSession = projectBundleSession;
	}

	@Override
	protected Bundle getBundle(URI platformURI) {
		String id = platformURI.segment(1);
		try {
			return projectBundleSession.getBundle(id);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return super.getBundle(platformURI);
	}

	public Bundle getBundle(String uriString) {
		Bundle bundle = null;
		if (uriString != null) {
			try {
				bundle = super.getBundle(uriString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (bundle == null) {
			bundle = this.projectBundleSession.getBundleContext().getBundle();
		}
		return bundle;
	}
}
