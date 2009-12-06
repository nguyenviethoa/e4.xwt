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

import org.eclipse.e4.xwt.IMultiValueConverter;

/**
 * Default implementation class of IMultiValueConverter
 * 
 * @author yyang <yesc.yang@soyatec.com>
 */
public abstract class AbstractMultiValueConverter implements IMultiValueConverter {
	
	public Object convert(Object fromObject) {
		if (!fromObject.getClass().isArray()) {
			fromObject = new Object[]{fromObject};
		}
		return convert((Object[])fromObject);
	}
}
