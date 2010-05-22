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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IDataProviderFactory;
import org.eclipse.e4.xwt.ILanguageSupport;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.ILogger;
import org.eclipse.e4.xwt.IMetaclassFactory;
import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.callback.IBeforeParsingCallback;
import org.eclipse.e4.xwt.converters.ObjectToObject;
import org.eclipse.e4.xwt.converters.StringToEnum;
import org.eclipse.e4.xwt.core.IElementLoaderFactory;
import org.eclipse.e4.xwt.core.IRenderingContext;
import org.eclipse.e4.xwt.core.IVisualElementLoader;
import org.eclipse.e4.xwt.dataproviders.ObjectDataProvider;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.internal.xml.DocumentObject;
import org.eclipse.e4.xwt.internal.xml.DocumentRoot;
import org.eclipse.e4.xwt.internal.xml.Element;
import org.eclipse.e4.xwt.internal.xml.ElementManager;
import org.eclipse.e4.xwt.javabean.ValueConvertorRegister;
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

	private MetaclassService metaclassService;

	private IElementLoaderFactory loaderFactory;
	
	private ILoadingContext _loadingContext = null;

	private Set<Tracking> trackingSet = new HashSet<Tracking>();
	private Map<String, ICommand> commands = new HashMap<String, ICommand>();
	private Map<String, INamespaceHandler> nsHandlers = new HashMap<String, INamespaceHandler>();
	private ILogger logger;
	private Collection<IStyle> defaultStyles = new ArrayList<IStyle>();
	private ILanguageSupport languageSupport;
	
	private static LinkedHashMap<String, IDataProviderFactory> dataProviderFactories = new LinkedHashMap<String, IDataProviderFactory>();

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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getLogger()
	 */
	public ILogger getLogger() {
		if (logger == null) {
			return Core.nullLog;
		}
		return logger;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#setLogger(org.eclipse.e4.xwt.ILogger)
	 */
	public void setLogger(ILogger log) {
		logger = log;
	}

	/**
	 * 
	 * @param nsmapace
	 * @param handler
	 */
	public void registerNamespaceHandler(String nsmapace,
			INamespaceHandler handler) {
		nsHandlers.put(nsmapace, handler);
	}

	/**
	 * 
	 * @param nsmapace
	 */
	public void unregisterNamespaceHandler(String nsmapace) {
		nsHandlers.remove(nsmapace);
	}

	/**
	 * 
	 * @param nsmapace
	 * @return
	 */
	public INamespaceHandler getNamespaceHandler(String nsmapace) {
		return nsHandlers.get(nsmapace);
	}

	class ConverterService {
		protected Map<Class<?>, IConverter> converters = new HashMap<Class<?>, IConverter>();

		public IConverter getConverter(Class<?> type) {
			IConverter converter = converters.get(type);
			if (converter != null) {
				return converter;
			}

			return null;
		}

		public void register(Class<?> type, IConverter converter) {
			converters.put(type, converter);
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findConvertor(java.lang.Class,
	 * java.lang.Class)
	 */
	public IConverter findConvertor(Class<?> source, Class<?> target) {
		if (source == target
				|| (source != Object.class && source.isAssignableFrom(target))) {
			return ObjectToObject.instance;
		}
		if (String.class == source && target.isEnum()) {
			return new StringToEnum(target);
		}
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) 
				getService(ValueConvertorRegister.class);
		if (convertorRegister == null) {
			return null;
		}
		return convertorRegister.findConverter(source, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#registerConvertor(org.eclipse.core.databinding
	 * .conversion.IConverter)
	 */
	public void registerConvertor(IConverter converter) {
		Class<?> source = (Class<?>) converter.getFromType();
		Class<?> target = (Class<?>) converter.getToType();
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) 
				getService(ValueConvertorRegister.class);
		convertorRegister.register(source, target, converter);
	}

	public void registerConvertor(Class<?> converter, String methodName) {
		try {
			Method method = converter.getDeclaredMethod(methodName);
			Object object = method.invoke(null);
			if (object instanceof IConverter) {
				registerConvertor((IConverter) object);
			}
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	public void registerConvertor(Class<?> converterType, String methodName,
			boolean value) {
		IConverter converter = loadConvertor(converterType, methodName, value);
		if (converter != null) {
			registerConvertor(converter);
		}
	}

	public void registerConvertor(ValueConvertorRegister convertorRegister,
			Class<?> source, Class<?> target, Class<?> converterType,
			String methodName, boolean value) {
		IConverter converter = loadConvertor(converterType, methodName, value);
		if (converter != null) {
			convertorRegister.register(source, target, converter);
		}
	}

	public IConverter loadConvertor(Class<?> converter, String methodName,
			boolean value) {
			try {
				Method method = converter.getDeclaredMethod(methodName);
				Object object = method.invoke(null, value);
				if (object instanceof IConverter) {
					return (IConverter) object;
				}
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getConverterService()
	 */
	public ConverterService getConverterService() {
		ConverterService service = (ConverterService) getService(ConverterService.class);
		if (service == null) {
			service = new ConverterService();
			registerService(ConverterService.class, service);
			service.register(Object.class, new IConverter() {
				public Object convert(Object fromObject) {
					return null;
				}

				public Object getFromType() {
					return Object.class;
				}

				public Object getToType() {
					return String.class;
				}
			});
		}
		return service;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#addTracking(org.eclipse.e4.xwt.Tracking)
	 */
	public void addTracking(Tracking tracking) {
		if (!trackingSet.contains(tracking)) {
			trackingSet.add(tracking);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#isTracking(org.eclipse.e4.xwt.Tracking)
	 */
	public boolean isTracking(Tracking tracking) {
		return trackingSet.contains(tracking);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getTrackings()
	 */
	public Set<Tracking> getTrackings() {
		return trackingSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#removeTracking(org.eclipse.e4.xwt.Tracking)
	 */
	public void removeTracking(Tracking tracking) {
		if (trackingSet.contains(tracking)) {
			trackingSet.remove(tracking);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerCommand(java.lang.String,
	 * org.eclipse.e4.xwt.input.ICommand)
	 */
	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getCommand(java.lang.String)
	 */
	public ICommand getCommand(String name) {
		return commands.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getCommands()
	 */
	public Map<String, ICommand> getCommands() {
		return commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#unregisterCommand(java.lang.String)
	 */
	public void unregisterCommand(String name) {
		commands.remove(name);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#addDefaultStyle(org.eclipse.e4.xwt.IStyle)
	 */
	public void addDefaultStyle(IStyle style) {
		defaultStyles.add(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#removeDefaultStyle(org.eclipse.e4.xwt.IStyle
	 * )
	 */
	public void removeDefaultStyle(IStyle style) {
		defaultStyles.remove(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getDefaultStyles()
	 */
	public Collection<IStyle> getDefaultStyles() {
		return new ArrayList<IStyle>(defaultStyles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#addDataProviderFactory(org.eclipse.e4.xwt
	 * .IDataProviderFactory)
	 */
	public void addDataProviderFactory(String name, IDataProviderFactory dataProviderFactory) {
		if (dataProviderFactory == null) {
			return;
		}
		dataProviderFactories.put(name, dataProviderFactory);
		registerMetaclass(dataProviderFactory.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerMetaclass(java.lang.Class)
	 */
	public IMetaclass registerMetaclass(Class<?> type) {
		return registerMetaclass(type, IConstants.XWT_NAMESPACE);
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#removeDataProviderFactory(org.eclipse.e4
	 * .xwt.IDataProviderFactory)
	 */
	public void removeDataProviderFactory(String name) {
		if (name == null) {
			return;
		}
		dataProviderFactories.remove(name);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.IXWTLoader#removeDataProviderFactory(org.eclipse.e4
	 * .xwt.IDataProviderFactory)
	 */
	public void removeDataProviderFactory(IDataProviderFactory dataProviderFactory) {
		if (dataProviderFactory == null) {
			return;
		}
		for (String name : dataProviderFactories.keySet()) {
			IDataProviderFactory value = dataProviderFactories.get(name);
			if (dataProviderFactory == value) {
				dataProviderFactories.remove(name);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getDataProviderFactories()
	 */
	public Collection<IDataProviderFactory> getDataProviderFactories() {
		return dataProviderFactories.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findDataProvider(java.lang.Object)
	 */
	public IDataProvider findDataProvider(Object dataContext) {
		if (dataContext instanceof IDataProvider) {
			return (IDataProvider) dataContext;
		}
		for (IDataProviderFactory factory : dataProviderFactories.values()) {
			IDataProvider dataProvider = factory.create(dataContext);
			if (dataProvider != null) {
				return dataProvider;
			}
		}
		ObjectDataProvider dataProvider = new ObjectDataProvider();
		dataProvider.setObjectInstance(dataContext);
		return dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getLoadingContext()
	 */
	public ILoadingContext getLoadingContext() {
		if (_loadingContext == null) {
			return DefaultLoadingContext.defaultLoadingContext;
		}
		return _loadingContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.IXWTLoader#setLoadingContext(org.eclipse.e4.xwt.
	 * ILoadingContext)
	 */
	public void setLoadingContext(ILoadingContext loadingContext) {
		_loadingContext = loadingContext;
	}
	
	public ILanguageSupport getLanguageSupport() {
		if (languageSupport == null) {
			languageSupport = new JavaLanguageSupport(); 
		}
		return languageSupport;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findLoadingContext(java.lang.Object)
	 */
	public ILoadingContext findLoadingContext(Object container) {
		return getLoadingContext();
	}
	
	public void setLanguageSupport(ILanguageSupport languageSupport) {
		this.languageSupport = languageSupport;
	}
	
	public IMetaclass getMetaclass(ILoadingContext context, String name, String namespace) {
		return getMetaclassService().getMetaclass(context, name, namespace);
	}

	public IMetaclass findMetaclass(Object object) {
		if (object instanceof Class<?>) {
			return getMetaclassService().findMetaclass((Class<?>) object);
		}
		return getMetaclassService().findMetaclass(object.getClass());
	}

	public IMetaclass getMetaclass(Object object) {
		if (object instanceof Class<?>) {
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

	public IMetaclass registerMetaclass(Class<?> metaclass, String namespace, IMetaclass superMetaclass) {
		return getMetaclassService().register(metaclass, namespace, superMetaclass);
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
			Element element = null;
			if (stream == null) {
				element = manager.load(input, (IBeforeParsingCallback) options.get(IXWTLoader.BEFORE_PARSING_CALLBACK));
			}
			else {
				IBeforeParsingCallback callback = (IBeforeParsingCallback) options.get(IXWTLoader.BEFORE_PARSING_CALLBACK);
				InputStream inputStream = stream;
				if (callback != null) {
					int size = stream.read();
					byte[] buffer = new byte[size];
					stream.read(buffer);
					String content = new String(buffer);
					stream.close();
					content = callback.onParsing(content);
					inputStream = new ByteArrayInputStream(content.getBytes());
					element = manager.load(stream, input);					
				}
				element = manager.load(inputStream, input);
			}
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
				Object designMode = options.get(IXWTLoader.DESIGN_MODE_PROPERTY);
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

	static private class ExtensionContext implements IRenderingContext {

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