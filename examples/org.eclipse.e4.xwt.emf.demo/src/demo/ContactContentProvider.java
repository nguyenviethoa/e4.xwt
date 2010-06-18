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
package demo;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import demo_model.Company;

public class ContactContentProvider implements IStructuredContentProvider {
	static Object[] EMPTY = new Object[] {};

	public ContactContentProvider() {
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Collection) {
			Collection collection = (Collection) inputElement;
			return collection.toArray();
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		} else if (inputElement instanceof Company) {
			return ((Company) inputElement).getEmployees().toArray();
		}
		return EMPTY;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
