/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.palette.contribution;

import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Initializer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class CreationCommand extends Command {

	private CreateRequest createReq;
	private Object newObject;

	public CreationCommand(CreateRequest createRequest) {
		this.createReq = createRequest;
	}

	public boolean canExecute() {
		if (createReq == null) {
			return false;
		}
		return (newObject = createReq.getNewObject()) != null;
	}

	final public void execute() {
		doCreate(preExecute());
	}

	/**
	 * Doing command here.
	 */
	protected void doCreate(Object newObject) {

	}

	/**
	 * Doing something before command execute.
	 */
	protected Object preExecute() {
		if (newObject instanceof Entry) {
			Initializer initializer = ((Entry) newObject).getInitializer();
			if (initializer != null) {
				return initializer.initialize(initializer.parse());
			}
		}
		return newObject;
	}
}
