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

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.internal.utils.UserData;

public abstract class AnimationTimeline extends Timeline {
	private boolean isDestinationDefault;
	private String targetName;
	private String targetProperty;

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetProperty() {
		return targetProperty;
	}

	public void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}	
	
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
	
	@Override
	protected Object findTarget(Object target) {
		String targetName = getTargetName();
		if (targetName == null) {
			return target;
		}
		Object newTarget = UserData.findElementByName(target, targetName);
		if (newTarget == null) {
			throw new XWTException("Name element " + targetName + " is not found in animation.");
		}
		return super.findTarget(newTarget);
	}
}
