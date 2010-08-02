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
package org.eclipse.e4.xwt.tests.clrfactory;

import org.eclipse.e4.xwt.ICLRFactory;

/**
 * @author jliu
 */
public class CLRFactory implements ICLRFactory {
	public static ICLRFactory INSTANCE = new CLRFactory();
	
	public Object createCLR(String args) {
		return new CLR(this, args);
	}
}
