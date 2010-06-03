/*******************************************************************************
 * Copyright (c) 2008, 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     IBM Corporation - ongoing development
 *     Soyatec - adapt for e4 Designer
 ******************************************************************************/
package org.eclipse.e4.tools.ui.designer.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.designer.session.ProjectBundleSession;
import org.eclipse.e4.ui.workbench.swt.util.ISWTResourceUtilities;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;

public class ResourceUtilities implements ISWTResourceUtilities {

	private final IProject project;
	
	public ResourceUtilities(IProject project, ProjectBundleSession projectBundleSession) {
		this.project = project;
	}

	public ImageDescriptor imageDescriptorFromURI(URI iconPath) {
		try {
			ImageDescriptor descriptor = null;
			URL url = ClassLoaderHelper.getResourceAsURL(project, iconPath.toString());
			if (url != null) {
				// find in the current workspace
				descriptor = ImageDescriptor.createFromURL(url);					
			}
			if (descriptor == null) {
				descriptor = ImageDescriptor.createFromURL(new URL(iconPath.toString()));
			}
			return descriptor;
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
