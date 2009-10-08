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

public class BeginStoryboard extends TriggerAction {
	protected HandoffBehavior handoffBehavior = HandoffBehavior.SnapshotAndReplace;
	protected String name;
	
	/**
	 * Getter of the property <tt>HandoffBehavior</tt>
	 * 
	 * @return Returns the handoffBehavior.
	 * @uml.property name="HandoffBehavior"
	 */
	public HandoffBehavior getHandoffBehavior() {
		return handoffBehavior;
	}

	/**
	 * Setter of the property <tt>HandoffBehavior</tt>
	 * 
	 * @param HandoffBehaviorProperty
	 *            The handoffBehavior to set.
	 * @uml.property name="HandoffBehavior"
	 */
	public void setHandoffBehavior(HandoffBehavior handoffBehavior) {
		this.handoffBehavior = handoffBehavior;
	}

	/**
	 * Getter of the property <tt>Name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="Name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter of the property <tt>Name</tt>
	 * 
	 * @param NameProperty
	 *            The name to set.
	 * @uml.property name="Name"
	 */
	public void setName(String name) {
		this.name = name;
	}
}
