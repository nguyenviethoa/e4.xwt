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
package org.eclipse.e4.xwt.springframework.tests.clrfactory;

import org.eclipse.e4.xwt.ICLRFactory;
import org.eclipse.e4.xwt.springframework.AbstractSpringCLRFactory;
import org.eclipse.e4.xwt.springframework.IArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jliu
 */
public class CLRFactory extends AbstractSpringCLRFactory {

	// IMPORTANT : use this singleton factory into xwt file (x:ClassFactory) to
	// load one time
	// the XML Spring file ui/ui-context.xml
	public static final ICLRFactory INSTANCE = new CLRFactory();

	@Override
	protected ApplicationContext createApplicationContext(IArguments arguments) {
		return new ClassPathXmlApplicationContext(
				"org/eclipse/e4/xwt/springframework/tests/clrfactory/ui-context.xml");
	}
}