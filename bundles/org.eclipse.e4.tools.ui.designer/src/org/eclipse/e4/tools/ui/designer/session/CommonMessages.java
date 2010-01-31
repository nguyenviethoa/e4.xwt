/**
 * 
 * Copyright (c) 2009 Thales Corporate Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Thales Corporate Services S.A.S - initial API and implementation
 * 
 */
package org.eclipse.e4.tools.ui.designer.session;

import org.eclipse.osgi.util.NLS;

public class CommonMessages {

  private static final String BUNDLE_NAME = "org.eclipse.egf.common.l10n.messages"; //$NON-NLS-1$

  private CommonMessages() {
    // Do not instantiate
  }

  static {
    // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, CommonMessages.class);
  }

  public static String URI_errorInstance;

  public static String Exception_unexpectedException;

  public static String JavaHelper_AnalysingFailure;

  public static String TypeClassHelper_classHierachy_JobName;
  
  public static String ProjectBundleSession_URLFailure;
  
  public static String ProjectBundleSession_StoppingFailure;

  public static String ProjectBundleSession_UninstallationFailure;

  public static String ProjectBundleSession_LoadFailure;
  
  public static String ProjectBundleSession_IllegalBundleState;

  public static String ProjectBundleSession_InstallationFailure;

}
