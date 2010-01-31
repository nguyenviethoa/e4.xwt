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
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class E4DesignerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.e4.tools.ui.designer"; //$NON-NLS-1$

	// The shared instance
	private static E4DesignerPlugin plugin;

	/**
	 * The constructor
	 */
	public E4DesignerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static E4DesignerPlugin getDefault() {
		return plugin;
	}

	public void log(IStatus status) {
		log(status, 0, true);
	}

	public void log(IStatus status, boolean appendLogger) {
		log(status, 0, appendLogger);
	}

	public void log(IStatus status, int nesting) {
		log(status, nesting, true);
	}

	public void log(IStatus status, int nesting, boolean appendLogger) {
		getLog().log(status);
	}

	/**
	 * Log Message Info
	 */

	public void logInfo(String message) {
		logInfo(message, 0, true);
	}

	public void logInfo(String message, boolean appendLogger) {
		logInfo(message, 0, appendLogger);
	}

	public void logInfo(String message, int nesting) {
		logInfo(message, nesting, true);
	}

	public void logInfo(String message, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.INFO, message, null), nesting, appendLogger);
	}

	/**
	 * Log Message and Throwable Info
	 */

	public void logInfo(String message, Throwable t) {
		logInfo(message, t, 0, true);
	}

	public void logInfo(String message, Throwable t, boolean appendLogger) {
		logInfo(message, t, 0, appendLogger);
	}

	public void logInfo(String message, Throwable t, int nesting) {
		logInfo(message, t, nesting, true);
	}

	public void logInfo(String message, Throwable t, int nesting,
			boolean appendLogger) {
		log(newStatus(IStatus.INFO, message, t), nesting, appendLogger);
	}

	/**
	 * Log Throwable Info
	 */

	public void logInfo(Throwable t) {
		logInfo(t, 0, true);
	}

	public void logInfo(Throwable t, boolean appendLogger) {
		logInfo(t, 0, appendLogger);
	}

	public void logInfo(Throwable t, int nesting) {
		logInfo(t, nesting, true);
	}

	public void logInfo(Throwable t, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.INFO, t.getMessage(), t), nesting, appendLogger);
	}

	/**
	 * Log Message Error
	 */

	public void logError(String message) {
		logError(message, 0, true);
	}

	public void logError(String message, boolean appendLogger) {
		logError(message, 0, appendLogger);
	}

	public void logError(String message, int nesting) {
		logError(message, nesting, true);
	}

	public void logError(String message, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.ERROR, message, null), nesting, appendLogger);
	}

	/**
	 * Log Message and Throwable Error
	 */

	public void logError(String message, Throwable t) {
		logError(message, t, 0, true);
	}

	public void logError(String message, Throwable t, boolean appendLogger) {
		logError(message, t, 0, appendLogger);
	}

	public void logError(String message, Throwable t, int nesting) {
		logError(message, t, nesting, true);
	}

	public void logError(String message, Throwable t, int nesting,
			boolean appendLogger) {
		log(newStatus(IStatus.ERROR, message, t), nesting, appendLogger);
	}

	/**
	 * Log Throwable Error
	 */

	public void logError(Throwable t) {
		logError(t, 0, true);
	}

	public void logError(Throwable t, boolean appendLogger) {
		logError(t, 0, appendLogger);
	}

	public void logError(Throwable t, int nesting) {
		logError(t, nesting, true);
	}

	public void logError(Throwable t, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.ERROR, t.getMessage(), t), nesting, appendLogger);
	}

	/**
	 * Log Message Warning
	 */

	public void logWarning(String message) {
		logWarning(message, 0, true);
	}

	public void logWarning(String message, boolean appendLogger) {
		logWarning(message, 0, appendLogger);
	}

	public void logWarning(String message, int nesting) {
		logWarning(message, nesting, true);
	}

	public void logWarning(String message, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.WARNING, message, null), nesting, appendLogger);
	}

	/**
	 * Log Message and Throwable Warning
	 */

	public void logWarning(String message, Throwable t) {
		logWarning(message, t, 0, true);
	}

	public void logWarning(String message, Throwable t, boolean appendLogger) {
		logWarning(message, t, 0, appendLogger);
	}

	public void logWarning(String message, Throwable t, int nesting) {
		logWarning(message, t, nesting, true);
	}

	public void logWarning(String message, Throwable t, int nesting,
			boolean appendLogger) {
		log(newStatus(IStatus.WARNING, message, t), nesting, appendLogger);
	}

	/**
	 * Log Throwable Warning
	 */

	public void logWarning(Throwable t) {
		logWarning(t, 0, true);
	}

	public void logWarning(Throwable t, boolean appendLogger) {
		logWarning(t, 0, appendLogger);
	}

	public void logWarning(Throwable t, int nesting) {
		logWarning(t, nesting, true);
	}

	public void logWarning(Throwable t, int nesting, boolean appendLogger) {
		log(newStatus(IStatus.WARNING, t.getMessage(), t), nesting,
				appendLogger);
	}

	/**
	 * Create an IStatus
	 * 
	 * @return a new IStatus
	 */
	public IStatus newStatus(int severity, String message, Throwable exception) {
		return new Status(severity, PLUGIN_ID, 0, message, exception);
	}

}
