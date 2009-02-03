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
package org.eclipse.e4.xwt.impl;

import java.util.HashMap;

public class NameContext extends HashMap<String, Object> {
	private final NameContext parent;

	public NameContext(NameContext parent) {
		super();
		this.parent = parent;
	}

	public void addObject(String name, Object object) {
		put(name, object);
	}

	public Object getObject(String name) {
		Object object = get(name);
		if (object != null)
			return object;
		return parent == null ? null : parent.getObject(name);
	}

	public boolean contains(String name) {
		if (containsKey(name))
			return true;
		return parent == null ? false : parent.contains(name);
	}
}
