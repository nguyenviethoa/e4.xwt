/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *     Anyware-tech - add multiple loaders
 *******************************************************************************/
package org.eclipse.e4.xwt;

/**
 * Class allowing to keep the reference on the XWT loader active
 */
public class XWTLoaderManager {
	/** Default XWT loader */
	private static XWTLoader defaultXWTLoader = new XWTLoader();

	/** Active XWT loader */
	private static XWTLoader activeXWTLoader = null;

	/**
	 * Returns the default instance of the XWT loader
	 * 
	 * @return the default instance of the XWT loader
	 */
	public static XWTLoader getDefault() {
		return defaultXWTLoader;
	}

	/**
	 * Returns the instance of the XWT loader active. If no XWT loader are active, returns the default XWT loader
	 * 
	 * @return the instance of the XWT loader active
	 */
	public static XWTLoader getActive() {
		XWTLoader xwtLoader = activeXWTLoader;
		if (xwtLoader == null) {
			xwtLoader = getDefault();
		}
		return xwtLoader;
	}

	/**
	 * Sets the active XWT loader
	 * 
	 * @param xwtLoader
	 *            the XWT loader
	 * @param active
	 *            true if the XWT loader is active, otherwise false
	 */
	public static void setActive(XWTLoader xwtLoader, boolean active) {
		if (active) {
			activeXWTLoader = xwtLoader;
		} else if (xwtLoader != null && xwtLoader.equals(activeXWTLoader)) {
			activeXWTLoader = null;
		}
	}
}
