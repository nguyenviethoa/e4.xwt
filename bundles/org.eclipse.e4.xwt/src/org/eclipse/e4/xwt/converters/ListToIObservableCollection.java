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
package org.eclipse.e4.xwt.converters;

import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.xwt.XWT;

/**
 * List to IObservableCollection converter
 * 
 * @author yyang
 */
public class ListToIObservableCollection implements IConverter {
	public static ListToIObservableCollection instance = new ListToIObservableCollection();

	public Object convert(Object fromObject) {
		Set<?> list = (Set<?>) fromObject;
		return new WritableList(XWT.getRealm(), (Set<?>)list, Object.class);
	}

	public Object getFromType() {
		return Set.class;
	}

	public Object getToType() {
		return IObservableCollection.class;
	}
}
