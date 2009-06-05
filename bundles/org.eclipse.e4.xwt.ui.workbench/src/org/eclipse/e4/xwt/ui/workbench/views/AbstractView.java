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

import java.util.Collections;

import org.eclipse.swt.widgets.Composite;

/**
 * The abstract class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractView extends AbstractRootView {
	protected Object input;

	public AbstractView(Composite parent) {
		super(parent);
	}

	public void setInput(Object input) {
		if (this.input == input) {
			return;
		}
		doSetInput(input, Collections.EMPTY_MAP);
		this.input = input;
	}
}
