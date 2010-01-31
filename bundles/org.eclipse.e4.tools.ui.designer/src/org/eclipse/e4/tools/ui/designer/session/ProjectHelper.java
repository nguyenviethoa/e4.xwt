/**
 * Copyright (c) 2009 Thales Corporate Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Thales Corporate Services S.A.S - initial API and implementation
 */
package org.eclipse.e4.tools.ui.designer.session;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

/**
 * Workspace projects helper.
 * 
 * @author brocard
 */
public class ProjectHelper {

  private ProjectHelper() {
    // Prevent Instantiation
  }

  /**
   * Required plug-ins class path entry path identifier.
   */
  protected static final String CLASS_PATH_ENTRY_REQUIRED_PLUGINS_PATH_ID = "requiredPlugins"; //$NON-NLS-1$

  /**
   * Project existence status after check.<br>
   * The project already exists (including default structure), or it has just
   * been created, or creation process failed.<br>
   * Internal purpose only.
   */
  public enum ProjectExistenceStatus {
    ALREADY_EXISTS, CREATED, CREATION_FAILED
  }

  /**
   * Get project from its name.<br>
   * It is assumed that this project name refers to a plug-in.<br>
   * If not, the method
   * <code>ResourcesPlugin.getWorkspace().getRoot().getProject(projectName_p)</code>
   * is invoked as result.
   * 
   * @param pluginId
   *          A project name that points to a plug-in in the workspace.
   * @return
   */
  public static IProject getProject(String pluginId) {
    // Precondition.
    if (pluginId == null) {
      return null;
    }
    // Get model base from project name.
    IPluginModelBase modelBase = PluginRegistry.findModel(pluginId);
    // Precondition.
    // Warning : fix for the Eclipse platform bug that consists in having PDE in
    // a weird state
    // regarding in-development plug-ins from launching platform as deployed
    // ones in current one.
    if (modelBase == null) {
      return ResourcesPlugin.getWorkspace().getRoot().getProject(pluginId);
    }
    return getProject(modelBase);
  }

  /**
   * Get the IProject for specified plug-in model.
   * 
   * @param base
   * @return null if the plug-in is not in the workspace.
   */
  public static IProject getProject(IPluginModelBase base) {
    // Precondition.
    if (base == null) {
      return null;
    }
    IResource underlyingResource = base.getUnderlyingResource();
    if (underlyingResource != null) {
      return underlyingResource.getProject();
    }
    return null;
  }

  /**
   * Refresh given project in the workspace.
   * 
   * @param project_p
   * @param monitor_p
   */
  public static void refreshProject(IProject project_p, IProgressMonitor monitor_p) {
    refreshProject(project_p, IResource.DEPTH_INFINITE, monitor_p);
  }

  /**
   * Refresh a project in the workspace.
   * 
   * @param projectToRefresh_p
   * @param depth_p
   * @param monitor_p
   * @see {@link IResource#refreshLocal(int, IProgressMonitor)}
   */
  public static void refreshProject(IProject projectToRefresh_p, int depth_p, IProgressMonitor monitor_p) {
    try {
      projectToRefresh_p.refreshLocal(depth_p, monitor_p);
    } catch (CoreException ce) {
      StringBuilder msg = new StringBuilder("ProjectHelper.refreshProject(..) _ "); //$NON-NLS-1$
      E4DesignerPlugin.getDefault().logError(msg.toString(), ce);
    }
  }

  /**
   * Create a folder with given name in given project.
   * 
   * @param folderName_p
   * @param project_p
   * @param monitor_p
   * @return <code>null</code> if creation failed.
   */
  public static IFolder createFolder(String folderName_p, IProject project_p, IProgressMonitor monitor_p) {
    IFolder folder = project_p.getFolder(folderName_p);
    // Create the physical resource.
    if (folder.exists() == false) {
      // Get parent path.
      IPath parentPath = new Path(folderName_p);
      if (parentPath.segmentCount() > 1) {
        parentPath = parentPath.removeLastSegments(1);
        // Make sure parent exists first.
        createFolder(parentPath.toString(), project_p, monitor_p);
      }
      // Then try and create given folder.
      try {
        folder.create(true, true, monitor_p);
      } catch (CoreException ce) {
        folder = null;
        StringBuilder msg = new StringBuilder("ProjectHelper.createFolder(..) _ "); //$NON-NLS-1$
        E4DesignerPlugin.getDefault().logError(msg.toString(), ce);
      }
    }
    return folder;
  }
}
