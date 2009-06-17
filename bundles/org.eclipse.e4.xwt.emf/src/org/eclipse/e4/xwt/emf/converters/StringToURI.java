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
package org.eclipse.e4.xwt.emf.converters;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.emf.common.util.URI;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class StringToURI implements IConverter {

	public Object convert(Object fromObject) {
		return URI.createURI(fromObject.toString());
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return URI.class;
	}
}