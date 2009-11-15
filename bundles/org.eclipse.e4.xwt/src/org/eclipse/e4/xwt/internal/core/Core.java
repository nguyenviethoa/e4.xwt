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
package org.eclipse.e4.xwt.internal.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.ILogger;
import org.eclipse.e4.xwt.IMetaclassFactory;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.core.IElementLoaderFactory;
import org.eclipse.e4.xwt.core.IRenderingContext;
import org.eclipse.e4.xwt.core.IVisualElementLoader;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.internal.xml.DocumentObject;
import org.eclipse.e4.xwt.internal.xml.DocumentRoot;
import org.eclipse.e4.xwt.internal.xml.Element;
import org.eclipse.e4.xwt.internal.xml.ElementManager;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class Core {
	static public final Object[] EMPTY_ARRAY = new Object[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	static public final String DEFAULT_STYLES_KEY = "XWT.DefaultStyles";

	static public boolean TRACE_BENCH = false;

	private HashMap<Class<?>, Object> registrations;

	private HashMap<DocumentObject, IVisualElementLoader> elementsLoaders = new HashMap<DocumentObject, IVisualElementLoader>();

	MetaclassService metaclassService;

	IElementLoaderFactory loaderFactory;

	private IXWTLoader xwtLoader;

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

	public Core(IElementLoaderFactory loaderFactory, IXWTLoader xwtLoader) {
		this.loaderFactory = loaderFactory;
		this.registrations = new HashMap<Class<?>, Object>();
		this.xwtLoader = xwtLoader;
		this.metaclassService = new MetaclassService(xwtLoader);
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

	public IMetaclass getMetaclass(Object object, String namespace) {
		if (object instanceof Class) {
			return getMetaclassService().getMetaclass((Class<?>) object, namespace);
		}
		return getMetaclassService().getMetaclass(object.getClass(), namespace);
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
		IVisualElementLoader creator = loaderFactory.createElementLoader(context, xwtLoader);
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
		Control control = null;
		ElementManager manager = new ElementManager();
		if (input != null) {
			Element element = (stream == null ? manager.load(input) : manager
					.load(stream, input));
			IRenderingContext context = new ExtensionContext(loadingContext,
					manager, manager.getRootElement().getNamespace());
			Object visual = createCLRElement(context, element, options);
			if (TRACE_BENCH) {
				System.out.println("Loaded: "
						+ (System.currentTimeMillis() - start) + "  "
						+ input.toString());
			}
			if (visual instanceof Control) {
				control = (Control) visual;
			} else if (visual instanceof Viewer) {
				control = ((Viewer) visual).getControl();
			} else {
				Class<?> jfaceWindow = Class
						.forName("org.eclipse.jface.window.Window");
				if (jfaceWindow != null && jfaceWindow.isInstance(visual)) {
					Method createMethod = jfaceWindow
							.getDeclaredMethod("create");
					createMethod.invoke(visual);
					Method method = jfaceWindow.getDeclaredMethod("getShell");
					control = (Control) method.invoke(visual);
				}
			}

			if (control instanceof Composite) {
				Object parent = options.get(IXWTLoader.CONTAINER_PROPERTY);
				Object designMode = options.get(IXWTLoader.DESIGN_MODE_ROPERTY);
				if (parent instanceof Composite) {
					Composite parentComposite = (Composite) parent;
					if (parentComposite.getLayout() == null
							|| designMode == Boolean.TRUE) {
						autoLayout(parentComposite, element);
					}
				} else if (parent == null || designMode == Boolean.TRUE) {
					if (control instanceof Shell) {
						autoLayout((Shell)control, element);
					}
					else {
						autoLayout(control.getShell(), element);						
					}
				}
			}
		}
		return control;
	}

	protected void autoLayout(Composite composite, Element element) {
		if (element == null) {
			return;
		}
		Attribute bounds = element.getAttribute("bounds");
		if (bounds == null) {
			bounds = element.getAttribute("bounds", IConstants.XWT_NAMESPACE);
		}
		Attribute size = element.getAttribute("size");
		if (size == null) {
			size = element.getAttribute("size", IConstants.XWT_NAMESPACE);
		}
		if (bounds == null && size == null) {
			composite.pack();
		}
	}

	private class ExtensionContext implements IRenderingContext {

		private Map<String, Object> properties = new HashMap<String, Object>();

		private URL resourcePath;

		private DocumentRoot documentRoot;

		private String namespace;

		private String encoding;

		protected ILoadingContext loadingContext;

		public ExtensionContext(ILoadingContext loadingContext,
				ElementManager elementManager, String namespace) {
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.soyatec.xaml.IExtensionContext#getEncoding()
		 */
		public String getEncoding() {
			return encoding;
		}

		public Object getProperty(String name) {
			return properties.get(name);
		}

		public void setProperty(String name, Object value) {
			properties.put(name, value);
		}

		public ILoadingContext getLoadingContext() {
			return loadingContext;
		}
	}

	public MetaclassService getMetaclassService() {
		return metaclassService;
	}

}