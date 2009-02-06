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
package org.eclipse.e4.xwt.javabean.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.IMetaclassFactory;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.utils.ClassLoaderUtil;
import org.eclipse.e4.xwt.utils.LoggerManager;

/**
 * @author yyang (yves.yang@soyatec.com)
 */
public class MetaclassManager {
	protected Map<String, IMetaclass> nameRegister = new HashMap<String, IMetaclass>();
	protected Collection<Class<?>> classRegister = new HashSet<Class<?>>();
	protected MetaclassManager parent;
	protected MetaclassService service;

	public MetaclassManager(MetaclassService service, MetaclassManager parent) {
		this.parent = parent;
		this.service = service;
	}

	public Collection<IMetaclass> getAllMetaclasses() {
		return nameRegister.values();
	}

	public void register(IMetaclass metaclass) {
		Class<?> type = metaclass.getType();
		if (classRegister.contains(type)) {
			return;
		}
		String key = type.getSimpleName();
		nameRegister.put(key, metaclass);
		classRegister.add(type);
	}

	public IMetaclass register(Class<?> javaClass) {
		IMetaclass metaclass = getMetaclass(javaClass);
		if (metaclass != null) {
			return metaclass;
		}
		Class<?> superclass = javaClass.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			register(superclass);
		}
		IMetaclass superMetaclass = getMetaclass(superclass);

		IMetaclass thisMetaclass = createMetaclass(javaClass, superMetaclass);
		register(thisMetaclass);
		return thisMetaclass;
	}

	protected IMetaclass createMetaclass(Class<?> javaClass, IMetaclass superMetaclass) {
		if (service != null) {
			IMetaclassFactory factory = service.findFactory(javaClass);
			if (factory != null) {
				return factory.create(javaClass, superMetaclass);
			}
		}
		return new Metaclass(javaClass, superMetaclass);
	}

	public static String normalizePropertyName(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public IMetaclass getMetaclass(ILoadingContext context, String name, String namespace) {
		IMetaclass metaclass = nameRegister.get(name);
		if (metaclass != null) {
			return metaclass;
		}
		if (namespace == null || !namespace.startsWith(IConstants.XAML_CLR_NAMESPACE_PROTO)) {
			LoggerManager.log(new IllegalArgumentException("Wrong namespace: " + namespace + " for " + name));
		}
		String packageName = namespace.substring(IConstants.XAML_CLR_NAMESPACE_PROTO.length());
		int index = packageName.indexOf('=');
		if (index != -1) {
			packageName = packageName.substring(0, index);
		}
		// if using default package(null), use only name as class name, else use package.class as class name
		String className = packageName.length() == 0 ? name : (packageName + "." + name);
		// try {
		Class type = ClassLoaderUtil.loadClass(context, className);
		if (type == null) {
			LoggerManager.log(new IllegalStateException("Cannot load " + className));
		}
		metaclass = register(type);
		// There is no need to mapping a CLR class, since the ClassLoader will be changed.
		nameRegister.remove(type.getSimpleName());
		return metaclass;
	}

	public IMetaclass getMetaclass(Object object) {
		if (object instanceof Class) {
			return getMetaclass((Class<?>) object);
		} else if (object instanceof String) {
			return getMetaclass((String) object);
		}
		Class<?> type = object.getClass();
		return getMetaclass(type);
	}

	public IMetaclass getMetaclass(Class<?> type) {
		if (classRegister.contains(type)) {
			IMetaclass metaclass = nameRegister.get(type.getSimpleName());
			if (metaclass != null) {
				return metaclass;
			}
		}
		if (parent != null) {
			IMetaclass metaclass = parent.getMetaclass(type);
			if (metaclass != null) {
				return metaclass;
			}
		}
		return null;
	}
}
