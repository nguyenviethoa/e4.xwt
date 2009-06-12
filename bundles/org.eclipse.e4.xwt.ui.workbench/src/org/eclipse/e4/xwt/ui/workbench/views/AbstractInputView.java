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

/**
 * The abstract class to handle the connection with e4 workbench.
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public abstract class AbstractInputView extends AbstractRootView {
	protected Object input;

	public AbstractInputView() {
	}

	abstract public Class<?> getInputType();

	public void setInput(Object input) {
		if (this.input == input) {
			return;
		}
		Class<?> inputType = getInputType();
		if (inputType == null || inputType.isInstance(input)) {
			refresh(input, Collections.EMPTY_MAP);
		}
		this.input = input;
	}
}
