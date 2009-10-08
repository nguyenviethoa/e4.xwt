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
package org.eclipse.e4.xwt.animation;

import org.eclipse.e4.xwt.core.TriggerAction;

public class ControllableStoryboardAction extends TriggerAction {
	protected String beginStoryboardName;

	/**
	 * Getter of the property <tt>BeginStoryboardName</tt>
	 * 
	 * @return Returns the beginStoryboardName.
	 * @uml.property name="BeginStoryboardName"
	 */
	public String getBeginStoryboardName() {
		return beginStoryboardName;
	}

	/**
	 * Setter of the property <tt>BeginStoryboardName</tt>
	 * 
	 * @param BeginStoryboardNameProperty
	 *            The beginStoryboardName to set.
	 * @uml.property name="BeginStoryboardName"
	 */
	public void setBeginStoryboardName(String beginStoryboardName) {
		this.beginStoryboardName = beginStoryboardName;
	}
}
