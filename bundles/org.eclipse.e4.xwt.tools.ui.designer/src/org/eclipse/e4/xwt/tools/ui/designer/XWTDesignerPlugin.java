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
package org.eclipse.e4.xwt.tools.ui.designer;

import org.eclipse.e4.xwt.tools.ui.designer.editor.XWTDesigner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XWTDesignerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.e4.xwt.tools.ui.designer";

	// The shared instance
	private static XWTDesignerPlugin plugin;

	/**
	 * The constructor
	 */
	public XWTDesignerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static XWTDesignerPlugin getDefault() {
		return plugin;
	}

	/**
	 * @param string
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String imageFilePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}

	public XWTDesigner getActiveDesigner() {
		IWorkbench workbench = getDefault().getWorkbench();
		if (workbench == null) {
			return null;
		}
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return null;
		}
		IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor instanceof XWTDesigner) {
			return (XWTDesigner) activeEditor;
		}
		return null;

	}

}
