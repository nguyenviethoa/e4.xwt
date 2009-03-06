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
package org.eclipse.e4.xwt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.ILogger;
import org.eclipse.e4.xwt.IMetaclassFactory;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.javabean.metadata.MetaclassManager;
import org.eclipse.e4.xwt.javabean.metadata.MetaclassService;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.xml.DocumentObject;
import org.eclipse.e4.xwt.xml.DocumentRoot;
import org.eclipse.e4.xwt.xml.Element;
import org.eclipse.e4.xwt.xml.ElementManager;
import org.eclipse.swt.widgets.Control;

public class Core {
	static public final String DEFAULT_STYLES_KEY = "XWT.DefaultStyles";

	static public boolean TRACE_BENCH = false;

	private HashMap<Class<?>, Object> registrations;

	private HashMap<DocumentObject, IVisualElementLoader> elementsLoaders = new HashMap<DocumentObject, IVisualElementLoader>();

	MetaclassService metaclassService = new MetaclassService();

	IElementLoaderFactory loaderFactory;

	static public ILogger nullLog = new ILogger() {
		private Map<Tracking, String> messageMap = new HashMap<Tracking, String>();

		public void error(Throwable e) {
		}

		public void error(Throwable e, String message) {
		}

		public void message(String message) {
		}

		public void warning(String message) {
		}

		public void printInfo(String message, Tracking tracking, Set trackType) {
			String printMessage = "";

			if (trackType != null && trackType.size() > 0) {
				if (trackType.contains(tracking)) {
					printMessage = (String) messageMap.get(tracking);
				}
			}
			System.out.println(printMessage);
		}

		public void addMessage(String message, Tracking tracking) {
			if (messageMap.containsKey(tracking)) {
				messageMap.remove(tracking);
			}
			messageMap.put(tracking, message);
		}

		public void removeMessage(Tracking tracking) {
			if (messageMap.containsKey(tracking)) {
				messageMap.remove(tracking);
			}
		}
	};

	public Core(IElementLoaderFactory loaderFactory) {
		this.loaderFactory = loaderFactory;
		this.registrations = new HashMap<Class<?>, Object>();
	}

	public IMetaclass getMetaclass(ILoadingContext context, String name, String namespace) {
		return getMetaclassService().getMetaclass(context, name, namespace);
	}

	public IMetaclass getMetaclass(Object object) {
		if (object instanceof Class) {
			return getMetaclassService().getMetaclass((Class<?>) object);
		}
		return getMetaclassService().getMetaclass(object.getClass());
	}

	public Collection<IMetaclass> getAllMetaclasses(String namespace) {
		return getMetaclassService().getAllMetaclasses(namespace);
	}

	public void registerMetaclass(IMetaclass metaclass, String namespace) {
		getMetaclassService().register(metaclass, namespace);
	}

	public void registerMetaclassFactory(IMetaclassFactory metaclassFactory) {
		getMetaclassService().registerFactory(metaclassFactory);
	}

	public IMetaclass registerMetaclass(Class<?> metaclass, String namespace) {
		return getMetaclassService().register(metaclass, namespace);
	}

	public void registerMetaclassManager(String namespace, MetaclassManager manager) {
		getMetaclassService().register(namespace, manager);
	}

	public Object getService(Class<?> type) {
		return this.registrations.get(type);
	}

	public void registerService(Class<?> serviceType, Object service) {
		registrations.put(serviceType, service);
	}

	protected Object createCLRElement(IRenderingContext context, Element element, Map<String, Object> options) {
		IVisualElementLoader loader = findElementLoader(element);
		if (loader != null) {
			return loader.createCLRElement(element, options);
		}
		loader = createElementLoader(context, element);
		Object visualObject = loader.createCLRElement(element, options);
		removeElementLoader(element);
		return visualObject;
	}

	protected IVisualElementLoader findElementLoader(DocumentObject element) {
		IVisualElementLoader loader = elementsLoaders.get(element);
		if (loader != null) {
			return loader;
		}
		if (element.getParent() != null) {
			return findElementLoader(element.getParent());
		}
		return null;
	}

	protected IVisualElementLoader createElementLoader(IRenderingContext context, DocumentObject element) {
		IVisualElementLoader creator = loaderFactory.createElementLoader(context);
		elementsLoaders.put(element, creator);
		return creator;
	}

	protected void removeElementLoader(DocumentObject element) {
		elementsLoaders.remove(element);
	}

	public Control load(ILoadingContext loadingContext, URL input, Map<String, Object> options) throws Exception {
		return load(loadingContext, null, input, options);
	}

	public Control load(ILoadingContext loadingContext, InputStream stream, URL input, Map<String, Object> options) throws Exception {
		// Detect from url or file path.
		long start = System.currentTimeMillis();
		ElementManager manager = new ElementManager();
		if (input != null) {
			Element element = (stream == null ? manager.load(input) : manager.load(stream, input));
			IRenderingContext context = new ExtensionContext(loadingContext, manager, manager.getRootElement().getNamespace());
			Object visual = createCLRElement(context, element, options);
			if (TRACE_BENCH) {
				System.out.println("Loaded: " + (System.currentTimeMillis() - start) + "  " + input.toString());
			}
			if (visual instanceof Control) {
				return (Control) visual;
			}
			Class<?> jfaceWindow = Class.forName("org.eclipse.jface.window.Window");
			if (jfaceWindow != null && jfaceWindow.isInstance(visual)) {
				Method createMethod = jfaceWindow.getDeclaredMethod("create");
				createMethod.invoke(visual);
				Method method = jfaceWindow.getDeclaredMethod("getShell");
				return (Control) method.invoke(visual);
			}
		}
		return null;
	}

	static private class ExtensionContext implements IRenderingContext {

		private Map<String, Object> properties = new HashMap<String, Object>();

		private URL resourcePath;

		private DocumentRoot documentRoot;

		private String namespace;

		private String encoding;

		protected ILoadingContext loadingContext;

		public ExtensionContext(ILoadingContext loadingContext, ElementManager elementManager, String namespace) {
			documentRoot = elementManager.getDocumentRoot();
			resourcePath = documentRoot.getPath();
			this.namespace = namespace;
			this.loadingContext = loadingContext;
			encoding = elementManager.getEncoding();
		}

		public String getNamespace() {
			return namespace;
		}

		public InputStream openStream(String path) throws IOException {
			return documentRoot.openStream(path);
		}

		public URL getResourcePath() {
			return resourcePath;
		}

		// public Object getService(Class<?> type) {
		// ServiceReference serviceReference =
		// context.getServiceReference(type.getName());
		// return context.getService(serviceReference);
		// }

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.soyatec.xaml.IExtensionContext#getEncoding()
		 */
		public String getEncoding() {
			return encoding;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.soyatec.xaml.IExtensionContext#getExtension(java.lang.String)
		 */
		// public IUElement getExtension(Element element) {
		// if (extensionManager == null) {
		// return null;
		// }
		// return extensionManager.getExtension(element, this);
		// }
		public Object getProperty(String name) {
			return properties.get(name);
		}

		public void setProperty(String name, Object value) {
			properties.put(name, value);
		}

		public ILoadingContext getLoadingContext() {
			return loadingContext;
		}

		public void setLoadingContext(ILoadingContext loadingContext) {
			this.loadingContext = loadingContext;
		}
	}

	public MetaclassService getMetaclassService() {
		return metaclassService;
	}

}