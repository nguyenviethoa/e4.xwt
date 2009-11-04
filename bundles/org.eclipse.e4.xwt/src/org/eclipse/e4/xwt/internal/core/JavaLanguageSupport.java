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
package org.eclipse.e4.xwt.internal.core;

import org.eclipse.e4.xwt.ILanguageSupport;
import org.eclipse.e4.xwt.core.IEventHandler;
import org.eclipse.e4.xwt.javabean.Controller;

public class JavaLanguageSupport implements ILanguageSupport {

	public IEventHandler createEventHandler() {
		return new Controller();
	}

	public String getName() {
		return "Java";
	}
}
