/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package ui;

import org.eclipse.e4.xwt.ICLRFactory;
import org.eclipse.e4.xwt.springframework.AbstractSpringCLRFactory;
import org.eclipse.e4.xwt.springframework.IArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ICLRFactory} Spring implementation which returns CLR class instance
 * {@link UI} by using Spring bean declared into "ui/ui-context.xml" Spring file
 * .
 * 
 * <p>
 * Example : x:ClassFactory="ui.MySpringCLRFactory.INSTANCE bean=myUI"
 * 
 * </p>
 * 
 */
public class MySpringCLRFactory extends AbstractSpringCLRFactory {

	// IMPORTANT : use this singleton factory into xwt file (x:ClassFactory) to
	// load one time
	// the XML Spring file ui/ui-context.xml
	public static final ICLRFactory INSTANCE = new MySpringCLRFactory();

	@Override
	protected ApplicationContext createApplicationContext(
			IArguments arguments) {
		return new ClassPathXmlApplicationContext("ui/ui-context.xml");
	}

}
