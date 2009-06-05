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
package org.eclipse.e4.xwt.ui.workbench.views;

import org.eclipse.e4.core.services.IDisposable;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.swt.widgets.Composite;

/**
 * The default implementation of e4 view of IEclipseContext aware
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractContextView extends AbstractRootView implements IDisposable {
	protected IEclipseContext outputContext;

	public AbstractContextView(Composite parent, IEclipseContext outputContext) {
		super(parent);
		this.outputContext = outputContext;
	}

	public Class<?> getInputType() {
		return null;
	}
}
