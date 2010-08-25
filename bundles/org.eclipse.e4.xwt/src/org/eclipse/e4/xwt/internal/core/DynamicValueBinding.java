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
package org.eclipse.e4.xwt.internal.core;

import org.eclipse.e4.xwt.core.IDynamicValueBinding;

public class DynamicValueBinding<T> extends DynamicBinding implements
		IDynamicValueBinding {
	private T sourceValue;

	public T getSourceValue() {
		return sourceValue;
	}

	public DynamicValueBinding(T sourceValue) {
		this.sourceValue = sourceValue;
	}

	public Object getValue(Class<?> type) {
		return sourceValue;
	}

	public void reset() {
	}

	public Object createBoundSource() {
		return null;
	}

	public boolean isSourceControl() {
		return false;
	}
}
