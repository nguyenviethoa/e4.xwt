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
package org.eclipse.e4.xwt.impl;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ILoadingContext;

public class LoadingContext implements ILoadingContext {
	public static final LoadingContext defaultLoadingContext = new LoadingContext();

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public String getNamespace() {
		return IConstants.XWT_NAMESPACE;
	}
}
