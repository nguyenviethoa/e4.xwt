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

import org.eclipse.e4.xwt.ILoadData;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.swt.widgets.Composite;

public class LoadData implements ILoadData {
	protected Composite parent;
	protected int styles = -1;

	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	private ResourceDictionary dico = new ResourceDictionary();
	private Object dataContext;

	public LoadData() {
	}

	public Composite getParent() {
		return parent;
	}

	public void setParent(Composite parent) {
		this.parent = parent;
	}

	public int getStyles() {
		return styles;
	}

	public void setStyles(int styles) {
		this.styles = styles;
	}

	public ResourceDictionary getResourceDictionary() {
		return dico;
	}

	public void setResourceDictionary(ResourceDictionary dico) {
		this.dico = dico;
	}

	public Object getDataContext() {
		return dataContext;
	}

	public void setDataContext(Object dataContext) {
		this.dataContext = dataContext;
	}
}
