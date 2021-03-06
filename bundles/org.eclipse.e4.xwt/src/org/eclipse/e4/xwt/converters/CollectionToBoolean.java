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
package org.eclipse.e4.xwt.converters;

import java.util.Collection;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * String to Boolean converter
 * 
 * @author yyang
 */
public class CollectionToBoolean implements IConverter {
	public static CollectionToBoolean instance = new CollectionToBoolean();

	public Object convert(Object fromObject) {
		Collection<?> collection = (Collection<?>) fromObject;
		return !collection.isEmpty();
	}

	public Object getFromType() {
		return Collection.class;
	}

	public Object getToType() {
		return Boolean.class;
	}
}
