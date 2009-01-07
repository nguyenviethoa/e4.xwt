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
package org.eclipse.e4.xwt.metadata;

public interface IMetaclass {
	public String getName();

	public IProperty[] getProperties();

	public IMetaclass getSuperClass();

	public IEvent[] getEvents();

	public IProperty findProperty(String name);

	public IProperty findDefaultProperty();

	public IEvent findEvent(String name);

	public Object newInstance(Object[] parameters);

	public boolean isAbstract();

	public boolean isInstance(Object object);

	public boolean isSubclassOf(IMetaclass metaclass);

	public boolean isSuperclassOf(IMetaclass metaclass);

	public boolean isAssignableFrom(IMetaclass metaclass);

	public Class<?> getType();
}