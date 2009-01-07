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

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass;

public class BindingToObject implements IConverter {
	public static BindingToObject instance = new BindingToObject();

	public Object convert(Object fromObject) {
		BindingMetaclass.Binding binding = (BindingMetaclass.Binding) fromObject;
		return binding.getValue();
	}

	public Object getFromType() {
		return BindingMetaclass.Binding.class;
	}

	public Object getToType() {
		return Object.class;
	}
}
