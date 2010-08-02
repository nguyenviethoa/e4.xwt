/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt;

public interface ICLRFactory {
	/**
	 * This method is used to create a CLR instance to handle events. 
	 * 
	 * @param args null if the default CLRFactory comes from XWT.getCLRFactory() or CLASS_FACTORY. Otherwise, 
	 *      it is the value x:ClassFactory
	 * @return
	 */
	Object createCLR(String args);
}
