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
package org.eclipse.e4.xwt.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.internal.databinding.viewers.ViewerObservableValueDecorator;
import org.eclipse.jface.viewers.Viewer;

public class TypedViewerObservableValueDecorator extends
		ViewerObservableValueDecorator {
	protected Object elementType;

	public TypedViewerObservableValueDecorator(IObservableValue decorated,
			Viewer viewer) {
		super(decorated, viewer);
	}

	public Object getElementType() {
		return elementType;
	}

	public void setElementType(Object elementType) {
		this.elementType = elementType;
	}

	@Override
	public Object getValueType() {
		Object elementType = getElementType();
		if (elementType != null) {
			return elementType;
		}
		return super.getValueType();
	}
}
