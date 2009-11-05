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

import java.util.List;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.xwt.XWT;

/**
 * String to Boolean converter
 * 
 * @author yyang
 */
public class SetToObservableCollection implements IConverter {
	public static SetToObservableCollection instance = new SetToObservableCollection();

	public Object convert(Object fromObject) {
		List<?> list = (List<?>) fromObject;
		return new WritableList(XWT.getRealm(), (List<?>)list, Object.class);
	}

	public Object getFromType() {
		return List.class;
	}

	public Object getToType() {
		return IObservableCollection.class;
	}
}
