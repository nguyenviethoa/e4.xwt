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

public abstract class AnimationTimeline extends Timeline {
	protected boolean isDestinationDefault;
	protected String targetPropertyType;
	
	/**
	 * Getter of the property <tt>IsDestinationDefault</tt>
	 * 
	 * @return Returns the isDestinationDefault.
	 * @uml.property name="IsDestinationDefault"
	 */
	public boolean getIsDestinationDefault() {
		return isDestinationDefault;
	}

	/**
	 * Setter of the property <tt>IsDestinationDefault</tt>
	 * 
	 * @param IsDestinationDefaultProperty
	 *            The isDestinationDefault to set.
	 * @uml.property name="IsDestinationDefault"
	 */
	public void setIsDestinationDefault(boolean isDestinationDefault) {
		this.isDestinationDefault = isDestinationDefault;
	}

	/**
	 * Getter of the property <tt>TargetPropertyType</tt>
	 * 
	 * @return Returns the targetPropertyType.
	 * @uml.property name="TargetPropertyType"
	 */
	public String getTargetPropertyType() {
		return targetPropertyType;
	}

	/**
	 * Setter of the property <tt>TargetPropertyType</tt>
	 * 
	 * @param TargetPropertyTypeProperty
	 *            The targetPropertyType to set.
	 * @uml.property name="TargetPropertyType"
	 */
	public void setTargetPropertyType(String targetPropertyType) {
		this.targetPropertyType = targetPropertyType;
	}
}
