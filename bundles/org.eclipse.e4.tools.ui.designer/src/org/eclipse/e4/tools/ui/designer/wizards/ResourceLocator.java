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
package org.eclipse.e4.tools.ui.designer.wizards;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;

public class ResourceLocator {
	private static final String TEMPLATE_FOLDER = "templates";

	private static ResourceLocator instance = new ResourceLocator();

	private ResourceLocator() {
	}

	public static ResourceLocator getInstance() {
		return instance;
	}

	public static URL getProjectTemplateFiles(String folder)
			throws IOException {
		Plugin plugin = getResorucePlugin();
		URL installURL = plugin.getBundle().getEntry(
				"/" + TEMPLATE_FOLDER + "/" + folder);
		// FileLocator.toFileURL(installURL);
		URL corePath = FileLocator.resolve(installURL);
		return corePath;
	}

	public static URL getFile(String relativePath) throws IOException {
		Plugin plugin = getResorucePlugin();
		URL installURL = plugin.getBundle().getEntry(relativePath);
		URL corePath = FileLocator.resolve(installURL);
		return corePath;
	}

	public static Plugin getResorucePlugin() {
		return E4DesignerPlugin.getDefault();
	}
}
