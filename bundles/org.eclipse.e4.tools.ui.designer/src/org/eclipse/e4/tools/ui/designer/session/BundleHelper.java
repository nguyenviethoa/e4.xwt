/**
 * Copyright (c) 2009, 2010 Thales Corporate Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Thales Corporate Services S.A.S - initial API and implementation
 */
package org.eclipse.e4.tools.ui.designer.session;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;

/**
 * This helper provides high-level services to deal with class loading.
 * 
 * @author Xavier Maysonnave
 */
public class BundleHelper {

  private BundleHelper() {
    // Prevent Instantiation
  }

  /**
   * Instantiate given fully qualified class name using given bundle.
   * 
   * @param fqcn
   *          the fully qualified class name with its package name (dot separated syntax).
   * @param bundle
   *          which can load given class name.
   * @return an instance of given class name
   */
  public static Object instantiate(String fqcn, Bundle bundle) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    // Precondition.
    if (fqcn == null || fqcn.trim().length() == 0 || bundle == null) {
      return null;
    }
    // Try loading a class according to the class name.
    Class<?> clazz = loadClass(fqcn, bundle);
    // Try instantiating an object of loaded class.
    if (clazz != null) {
      return clazz.newInstance();
    }
    return null;
  }

  /**
   * Load given fully qualified class name using given bundle.
   * 
   * @param fqcn
   *          the fully qualified class name with its package name (dot separated syntax).
   * @param bundle
   *          bundle which can load given class name.
   * @return loaded Class according to given class name
   */
  public static Class<?> loadClass(String fqcn, Bundle bundle) throws ClassNotFoundException {
    Class<?> clazz = null;
    // Precondition.
    if (fqcn == null || fqcn.trim().length() == 0 || bundle == null) {
      return clazz;
    }
    return bundle.loadClass(fqcn.trim());
  }

  /**
   * Unique ID based on bundle symbolic name
   */
  public static String getBundleId(ModelEntry entry) {
    if (entry == null) {
      return null;
    }
    String id = entry.getId();
    if (id == null || id.trim().length() == 0) {
      return null;
    }
    return id.trim();
  }

  /**
   * Unique ID based on bundle symbolic name
   */
  public static String getBundleId(IPluginModelBase model) {
    if (model == null || model.getPluginBase() == null) {
      return null;
    }
    if (model.getUnderlyingResource() != null && model.getUnderlyingResource().getProject() != null) {
      return getBundleId(model.getUnderlyingResource().getProject());
    }
    String id = model.getPluginBase().getId();
    if (id == null || id.trim().length() == 0) {
      return null;
    }
    return id.trim();
  }

  /**
   * Unique ID based on project
   */
  public static String getBundleId(IProject project) {
    if (project == null) {
      return null;
    }
    String id = null;
    IPluginModelBase base = getPluginModelBase(project);
    if (base != null) {
      id = base.getPluginBase().getId();
    }
    if (id == null) {
      id = project.getName();
    }
    if (id == null || id.trim().length() == 0) {
      return null;
    }
    return id.trim();
  }

  /**
   * Unique ID based on resource
   */
  public static String getBundleId(IResource resource) {
    if (resource == null) {
      return null;
    }
    return getBundleId(resource.getProject());
  }

  /**
   * Get the plug-in model base for given path.
   * 
   * @param path
   * @return an {@link IPluginModelBase} instance or null if the project is not
   *         a plug-in.
   */
  public static IPluginModelBase getPluginModelBase(IPath path) {
    if (path == null || path.segmentCount() < 2) {
      return null;
    }
    return getPluginModelBase(ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0)));
  }

  /**
   * Get the plug-in model base for given resource.
   * 
   * @param resource
   * @return an {@link IPluginModelBase} instance or null if the project is not
   *         a plug-in.
   */
  public static IPluginModelBase getPluginModelBase(IResource resource) {
    if (resource == null) {
      return null;
    }
    return getPluginModelBase(resource.getProject());
  }

  /**
   * Get the plug-in model base for given project.
   * 
   * @param project
   * @return an {@link IPluginModelBase} instance or null if the project is not
   *         a plug-in.
   */
  public static IPluginModelBase getPluginModelBase(IProject project) {
    if (project == null) {
      return null;
    }
    return PluginRegistry.findModel(project);
  }

}