/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.javabean.metadata.properties;

/**
 * A property to hold the event state
 *
 * @author yyang (yves.yang@soyatec.com)
 */
public class EventProperty extends DataProperty {

	public EventProperty(String name, Class<?> propertyType, String key,
			Object defaultValue) {
		super(name, propertyType, key, defaultValue);
	}

	public EventProperty(String name, Class<?> propertyType, String key) {
		super(name, propertyType, key);
	}

	public EventProperty(String name, String key) {
		super(name, key);
	}

}
