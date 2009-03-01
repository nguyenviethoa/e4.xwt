/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.javabean.metadata.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.e4.xwt.javabean.metadata.Behavior;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.metadata.ISetPostAction;

public abstract class AbstractProperty extends Behavior implements IProperty {
	protected Collection<ISetPostAction> setPostActions = Collections.EMPTY_LIST;
	private Class<?> type;

	public AbstractProperty(String name, Class<?> type) {
		super(name);
		this.type = type;
	}

	public void addSetPostAction(ISetPostAction setPostAction) {
		if (setPostActions == Collections.EMPTY_LIST) {
			setPostActions = new ArrayList<ISetPostAction>();
		}
		if (!setPostActions.contains(setPostAction)) {
			setPostActions.add(setPostAction);
		}
	}

	public void removeSetPostAction(ISetPostAction setPostAction) {
		setPostActions.remove(setPostAction);
	}

	protected void fireSetPostAction(Object target, IProperty property, Object value) {
		for (ISetPostAction setPostAction : setPostActions) {
			setPostAction.action(target, property, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.metadata.IProperty#setType(java.lang.Class)
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.metadata.IProperty#getType()
	 */
	public Class<?> getType() {
		return type;
	}
}