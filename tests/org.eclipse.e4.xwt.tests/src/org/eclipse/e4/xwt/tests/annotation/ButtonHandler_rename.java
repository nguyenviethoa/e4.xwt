/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.annotation;

import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.swt.widgets.Button;

/**
 * 
 * @author yyang
 */
public class ButtonHandler_rename {
	public static ButtonHandler_rename instance;

	@UI("my button")
	private Button myButton;

	public ButtonHandler_rename() {
		if (instance == null) {
			instance = this;
		}
		else {
			throw new IllegalStateException();
		}
	}
}
