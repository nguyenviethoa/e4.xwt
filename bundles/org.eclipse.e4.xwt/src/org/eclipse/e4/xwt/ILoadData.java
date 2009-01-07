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
package org.eclipse.e4.xwt;

import org.eclipse.swt.widgets.Composite;

public interface ILoadData {

	ILoadData DefaultLoadData = new ILoadData() {
		public String getNamespace() {
			return IConstants.XWT_NAMESPACE;
		}

		public Composite getParent() {
			return null;
		}

		public int getStyles() {
			return -1;
		}

		public void setStyles(int arg0) {
		}

		public void setParent(Composite parent) {
			throw new IllegalStateException("Readonly Loaddata");
		}

		public ResourceDictionary getResourceDictionary() {
			return null;
		}

		public void setResourceDictionary(ResourceDictionary dico) {
			throw new IllegalStateException("Readonly Loaddata");
		}

		public Object getDataContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setDataContext(Object dataContext) {
			throw new IllegalStateException("Readonly Loaddata");
		}

	};

	Composite getParent();

	int getStyles();

	void setStyles(int styles);

	void setParent(Composite parent);

	ResourceDictionary getResourceDictionary();

	void setResourceDictionary(ResourceDictionary dico);

	Object getDataContext();

	void setDataContext(Object dataContext);

}