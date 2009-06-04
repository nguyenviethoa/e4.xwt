/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt;


public class LoadingContext implements ILoadingContext {
	public static final LoadingContext defaultLoadingContext = new LoadingContext();

	protected ClassLoader classLoader;

	public LoadingContext() {
	}

	public LoadingContext(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			return Thread.currentThread().getContextClassLoader();
		}
		return classLoader;
	}

	public String getNamespace() {
		return IConstants.XWT_NAMESPACE;
	}
}
