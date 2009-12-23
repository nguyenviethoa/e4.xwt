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
package org.eclipse.e4.xwt.core;

import org.eclipse.core.databinding.DataBindingContext;

/**
 *
 * @author yyang (yves.yang@soyatec.com)
 */
public interface IDynamicBinding extends IBinding {
	Object createBoundSource();

	void setControl(Object control);

	Object getControl();

	void setHost(Object control);

	Object getHost();


	void setType(String type);

	String getType();

	boolean isSourceControl();

	/**
	 * Returns the name of the {@link DataBindingContext} associated with this binding
	 *
	 * @return the name of the {@link DataBindingContext}
	 */
	String getContextName();

	/**
	 * Sets the name of the {@link DataBindingContext} this binding is associated with
	 *
	 * @param contextName
	 */
	void setContextName(String contextName);
}
