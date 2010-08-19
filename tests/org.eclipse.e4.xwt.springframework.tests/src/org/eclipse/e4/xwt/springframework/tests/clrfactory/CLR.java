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
import org.eclipse.e4.xwt.springframework.IArguments;
import org.eclipse.e4.xwt.springframework.ICLRFactoryAware;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

/**
 * @author jliu
 */
public class CLR implements ICLRFactoryAware {

	private ICLRFactory factory;

	private IArguments args;

	public IArguments getArgs() {
		return args;
	}

	public void setArgs(IArguments args) {
		this.args = args;
	}

	// public CLR(CLRFactory factory, String args) {
	// this.args = args;
	// this.factory = factory;
	// }

	public CLR() {

	}

	public void setCLRFactory(ICLRFactory factory, IArguments args) {
		this.factory = factory;
		this.args = args;
	}

	public void select(Button sender, Event event) {
		if (args == null) {
			sender.setText("");
		} else {
			sender.setText(args.getSource());
		}
		sender.setData("CLR", this);
	}

	public ICLRFactory getFactory() {
		return factory;
	}

	public void setFactory(ICLRFactory factory) {
		this.factory = factory;
	}
}
