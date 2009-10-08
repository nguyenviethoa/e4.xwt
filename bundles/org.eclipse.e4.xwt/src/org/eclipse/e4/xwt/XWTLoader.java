/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *     Anyware-tech - add multiple loaders
 *******************************************************************************/
package org.eclipse.e4.xwt;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.converters.BindingToObject;
import org.eclipse.e4.xwt.converters.CollectionToBoolean;
import org.eclipse.e4.xwt.converters.DateToString;
import org.eclipse.e4.xwt.converters.EnumToString;
import org.eclipse.e4.xwt.converters.ObjectToBoolean;
import org.eclipse.e4.xwt.converters.ObjectToObject;
import org.eclipse.e4.xwt.converters.ObjectToString;
import org.eclipse.e4.xwt.converters.SelectionToBoolean;
import org.eclipse.e4.xwt.converters.StringToBoolean;
import org.eclipse.e4.xwt.converters.StringToColor;
import org.eclipse.e4.xwt.converters.StringToEnum;
import org.eclipse.e4.xwt.converters.StringToFont;
import org.eclipse.e4.xwt.converters.StringToFormAttachment;
import org.eclipse.e4.xwt.converters.StringToImage;
import org.eclipse.e4.xwt.converters.StringToIntArray;
import org.eclipse.e4.xwt.converters.StringToInteger;
import org.eclipse.e4.xwt.converters.StringToPoint;
import org.eclipse.e4.xwt.converters.StringToRectangle;
import org.eclipse.e4.xwt.converters.StringToType;
import org.eclipse.e4.xwt.converters.StringToURL;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.core.Setter;
import org.eclipse.e4.xwt.core.Style;
import org.eclipse.e4.xwt.dataproviders.ObjectDataProvider;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.core.MetaclassManager;
import org.eclipse.e4.xwt.internal.core.NameScope;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.e4.xwt.javabean.ResourceLoaderFactory;
import org.eclipse.e4.xwt.javabean.ValueConvertorRegister;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.ComboBoxCellEditorMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.ExpandItemHeightAction;
import org.eclipse.e4.xwt.javabean.metadata.TableEditorMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.TableViewerColumnMetaClass;
import org.eclipse.e4.xwt.javabean.metadata.properties.DataProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicBeanProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.StyleProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableColumnEditorProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableEditorDynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemEditorProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnImageProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnPropertyProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnTextProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnWidthProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnsProperty;
import org.eclipse.e4.xwt.jface.ComboBoxCellEditor;
import org.eclipse.e4.xwt.jface.DefaultCellModifier;
import org.eclipse.e4.xwt.jface.DefaultLabelProvider;
import org.eclipse.e4.xwt.jface.DefaultListContentProvider;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.utils.ResourceManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Default XWT loader
 * 
 * @author yyang (yves.yang@soyatec.com) jliu (jin.liu@soyatec.com)
 */
public class XWTLoader implements IXWTLoader {
	// Declarations
	private Core core = null;
	private ILoadingContext _loadingContext = null;

	private Set<Tracking> trackingSet = new HashSet<Tracking>();
	private Map<String, ICommand> commands = new HashMap<String, ICommand>();
	private Map<String, INamespaceHandler> nsHandlers = new HashMap<String, INamespaceHandler>();
	private ILogger logger;
	private Collection<IStyle> defaultStyles = new ArrayList<IStyle>();

	private static Collection<IDataProviderFactory> dataProviderFactories = new ArrayList<IDataProviderFactory>();

	public Display display;
	public Realm realm;

	public XWTLoader() {
		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
		}
		if (realm == null) {
			realm = SWTObservables.getRealm(display);
		}

		initialize();
	}

	public Realm getRealm() {
		return realm;
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
	public void registerNamespaceHandler(String nsmapace, INamespaceHandler handler) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getNamespace(java.lang.Class)
	 */
	public String getNamespace(Class<?> javaclass) {
		if (getMetaclass(javaclass) != null) {
			return IConstants.XWT_NAMESPACE;
		}
		Package javaPackage = javaclass.getPackage();
		if (javaPackage == null) {
			return IConstants.XAML_CLR_NAMESPACE_PROTO;
		}
		return IConstants.XAML_CLR_NAMESPACE_PROTO + javaclass.getPackage().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getElementName(java.lang.Object)
	 */
	public String getElementName(Object object) {
		return UserDataHelper.getElementName(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findNameContext(org.eclipse.swt.widgets.Widget)
	 */
	public NameScope findNameContext(Widget widget) {
		return UserDataHelper.findNameContext(widget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findElementByName(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Object findElementByName(Widget context, String name) {
		return UserDataHelper.findElementByName(context, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getDataContext(org.eclipse.swt.widgets.Widget)
	 */
	public Object getDataContext(Widget element) {
		return UserDataHelper.getDataContext(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#setDataContext(org.eclipse.swt.widgets.Widget, java.lang.Object)
	 */
	public void setDataContext(Widget widget, Object dataContext) {
		UserDataHelper.setDataContext(widget, dataContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getCLR(org.eclipse.swt.widgets.Widget)
	 */
	public Object getCLR(Widget widget) {
		return UserDataHelper.getCLR(widget);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findShell(org.eclipse.swt.widgets.Widget)
	 */
	public Shell findShell(Widget context) {
		return UserDataHelper.findShell(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findCompositeParent(org.eclipse.swt.widgets.Widget)
	 */
	public Composite findCompositeParent(Widget context) {
		return UserDataHelper.findCompositeParent(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getMetaclass(java.lang.Object)
	 */
	public IMetaclass getMetaclass(Object object) {
		return core.getMetaclass(object);
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
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(java.net.URL)
	 */
	public synchronized Control load(URL file) throws Exception {
		return loadWithOptions(file, Collections.EMPTY_MAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(java.net.URL, java.lang.Object)
	 */
	public synchronized Control load(URL file, Object dataContext) throws Exception {
		return load(null, file, dataContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(org.eclipse.swt.widgets.Composite, java.net.URL)
	 */
	public synchronized Control load(Composite parent, URL file) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		return loadWithOptions(file, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(org.eclipse.swt.widgets.Composite, java.net.URL, java.lang.Object)
	 */
	public synchronized Control load(Composite parent, URL file, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		options.put(DATACONTEXT_PROPERTY, dataContext);
		return loadWithOptions(file, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(org.eclipse.swt.widgets.Composite, java.lang.Class, java.lang.Object)
	 */
	public synchronized Control load(Composite parent, Class<?> viewType, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		options.put(DATACONTEXT_PROPERTY, dataContext);
		return loadWithOptions(viewType, options);
	}

	protected Map<String, Object> prepareOptions(Map<String, Object> options) {
		Boolean disabledStyle = (Boolean) options.get(DISBALE_STYLES_PROPERTY);
		if (!Boolean.TRUE.equals(disabledStyle)) {
			Collection<IStyle> defaultStyles = getDefaultStyles();
			Object styles = options.get(DEFAULT_STYLES_PROPERTY);
			if (styles != null) {
				if (styles instanceof IStyle) {
					defaultStyles.add((IStyle) styles);
				} else if (styles instanceof Collection) {
					for (IStyle style : (Collection<IStyle>) styles) {
						defaultStyles.add(style);
					}
				} else if (styles instanceof Object[]) {
					for (Object element : (Object[]) styles) {
						if (element instanceof IStyle) {
							defaultStyles.add((IStyle) element);
						} else {
							throw new XWTException("IStyle is expected in [styles] paramters.");
						}
					}
				}
				options.remove(DEFAULT_STYLES_PROPERTY);
			}
			if (!defaultStyles.isEmpty()) {
				ResourceDictionary dictionary = (ResourceDictionary) options.get(RESOURCE_DICTIONARY_PROPERTY);
				if (dictionary == null) {
					dictionary = new ResourceDictionary();
					if (options == Collections.EMPTY_MAP) {
						options = new HashMap<String, Object>();
					}
					options.put(RESOURCE_DICTIONARY_PROPERTY, dictionary);
				}
				dictionary.put(Core.DEFAULT_STYLES_KEY, defaultStyles);
			}
		}
		return options;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#loadWithOptions(java.lang.Class, java.util.Map)
	 */
	public synchronized Control loadWithOptions(Class<?> viewType, Map<String, Object> options) throws Exception {
		ILoadingContext context = getLoadingContext();
		try {
			setLoadingContext(new LoadingContext(viewType.getClassLoader()));
			options = prepareOptions(options);
			return loadWithOptions(viewType.getResource(viewType.getSimpleName() + ".xwt"), options);
		} finally {
			setLoadingContext(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#open(java.lang.Class)
	 */
	public synchronized void open(Class<?> type) throws Exception {
		open(type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX), Collections.EMPTY_MAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#open(java.net.URL)
	 */
	public synchronized void open(final URL url) throws Exception {
		open(url, Collections.EMPTY_MAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(org.eclipse.swt.widgets.Composite, java.io.InputStream, java.net.URL, java.lang.Object)
	 */
	public synchronized Control load(Composite parent, InputStream stream, URL file, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		options.put(DATACONTEXT_PROPERTY, dataContext);
		return loadWithOptions(stream, file, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#open(java.net.URL, java.lang.Object)
	 */
	public synchronized void open(URL url, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(DATACONTEXT_PROPERTY, dataContext);
		open(url, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#open(java.lang.Class, java.lang.Object)
	 */
	public synchronized void open(Class<?> type, Object dataContext) throws Exception {
		open(type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX), dataContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#open(java.net.URL, java.util.Map)
	 */
	public synchronized void open(final URL url, final Map<String, Object> options) throws Exception {
		Realm.runWithDefault(realm, new Runnable() {
			public void run() {
				try {
					Control control = loadWithOptions(url, options);
					Shell shell = control.getShell();
					shell.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							ResourceManager.resources.dispose();
						}
					});
					shell.open();
					while (!shell.isDisposed()) {
						if (!shell.getDisplay().readAndDispatch())
							shell.getDisplay().sleep();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#convertFrom(org.eclipse.e4.xwt.metadata.IMetaclass, java.lang.String)
	 */
	public Object convertFrom(IMetaclass type, String string) {
		Class<?> targetType = type.getType();
		return convertFrom(targetType, string);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#convertFrom(java.lang.Class, java.lang.String)
	 */
	public Object convertFrom(Class<?> targetType, String string) {
		if (targetType == String.class) {
			return string;
		}
		IConverter converter = findConvertor(String.class, targetType);
		if (converter != null) {
			return converter.convert(string);
		}
		throw new XWTException("Converter is missing of type: " + targetType.getName() + " from String");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#loadWithOptions(java.net.URL, java.util.Map)
	 */
	public synchronized Control loadWithOptions(URL url, Map<String, Object> options) throws Exception {
		Composite object = (Composite) options.get(CONTAINER_PROPERTY);
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		options = prepareOptions(options);
		Control visualObject = core.load(loadingContext, url, options);
		return visualObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#load(java.io.InputStream, java.net.URL)
	 */
	public synchronized Control load(InputStream stream, URL url) throws Exception {
		return loadWithOptions(stream, url, Collections.EMPTY_MAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#loadWithOptions(java.io.InputStream, java.net.URL, java.util.Map)
	 */
	public synchronized Control loadWithOptions(InputStream stream, URL base, Map<String, Object> options) throws Exception {
		Composite object = (Composite) options.get(CONTAINER_PROPERTY);
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		options = prepareOptions(options);
		Control visualObject = core.load(loadingContext, stream, base, options);
		return visualObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getAllMetaclasses()
	 */
	public IMetaclass[] getAllMetaclasses() {
		Collection<?> metaclasses = core.getAllMetaclasses(IConstants.XWT_NAMESPACE);
		return metaclasses.toArray(new IMetaclass[metaclasses.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getMetaclass(java.lang.String, java.lang.String)
	 */
	public IMetaclass getMetaclass(String tagName, String ns) {
		return (IMetaclass) core.getMetaclass(getLoadingContext(), tagName, ns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerMetaclass(java.lang.Class)
	 */
	public IMetaclass registerMetaclass(Class<?> type) {
		return register(type, IConstants.XWT_NAMESPACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerMetaclassFactory(org.eclipse.e4.xwt.IMetaclassFactory)
	 */
	public void registerMetaclassFactory(IMetaclassFactory metaclassFactory) {
		core.registerMetaclassFactory(metaclassFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#register(java.lang.Class, java.lang.String)
	 */
	public IMetaclass register(Class<?> javaclass, String namespace) {
		return core.registerMetaclass(javaclass, namespace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getConverterService()
	 */
	public ConverterService getConverterService() {
		ConverterService service = (ConverterService) core.getService(ConverterService.class);
		if (service == null) {
			service = new ConverterService();
			core.registerService(ConverterService.class, service);
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

	public static Class<?> normalizedType(Class<?> type) {
		if (type == int.class) {
			return Integer.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findConvertor(java.lang.Class, java.lang.Class)
	 */
	public IConverter findConvertor(Class<?> source, Class<?> target) {
		source = normalizedType(source);
		target = normalizedType(target);
		if (source == target || (source != Object.class && source.isAssignableFrom(target))) {
			return ObjectToObject.instance;
		}
		if (String.class == source && target.isEnum()) {
			return new StringToEnum(target);
		}
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		return convertorRegister.findConverter(source, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerConvertor(org.eclipse.core.databinding.conversion.IConverter)
	 */
	public void registerConvertor(IConverter converter) {
		Class<?> source = (Class<?>) converter.getFromType();
		Class<?> target = (Class<?>) converter.getToType();
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		convertorRegister.register(source, target, converter);
	}

	protected void registerConvertor(Class<?> converter, String methodName) {
		try {
			Method method = converter.getDeclaredMethod(methodName);
			Object object = method.invoke(null);
			if (object instanceof IConverter) {
				registerConvertor((IConverter) object);
			}
		} catch (Exception e) {
		}
	}

	protected void registerConvertor(Class<?> converterType, String methodName, boolean value) {
		IConverter converter = loadConvertor(converterType, methodName, value);
		if (converter != null) {
			registerConvertor(converter);
		}
	}

	protected void registerConvertor(ValueConvertorRegister convertorRegister, Class<?> source, Class<?> target, Class<?> converterType, String methodName, boolean value) {
		IConverter converter = loadConvertor(converterType, methodName, value);
		if (converter != null) {
			convertorRegister.register(source, target, converter);
		}
	}

	protected IConverter loadConvertor(Class<?> converter, String methodName, boolean value) {
		try {
			Method method = converter.getDeclaredMethod(methodName);
			Object object = method.invoke(null, value);
			if (object instanceof IConverter) {
				return (IConverter) object;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#addTracking(org.eclipse.e4.xwt.Tracking)
	 */
	public void addTracking(Tracking tracking) {
		if (!trackingSet.contains(tracking)) {
			trackingSet.add(tracking);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#isTracking(org.eclipse.e4.xwt.Tracking)
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
	 * @see org.eclipse.e4.xwt.IXWTLoader#removeTracking(org.eclipse.e4.xwt.Tracking)
	 */
	public void removeTracking(Tracking tracking) {
		if (trackingSet.contains(tracking)) {
			trackingSet.remove(tracking);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#registerCommand(java.lang.String, org.eclipse.e4.xwt.input.ICommand)
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
	 * @see org.eclipse.e4.xwt.IXWTLoader#addDefaultStyle(org.eclipse.e4.xwt.IStyle)
	 */
	public void addDefaultStyle(IStyle style) {
		defaultStyles.add(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#removeDefaultStyle(org.eclipse.e4.xwt.IStyle)
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
	 * @see org.eclipse.e4.xwt.IXWTLoader#addDataProviderFactory(org.eclipse.e4.xwt.IDataProviderFactory)
	 */
	public void addDataProviderFactory(IDataProviderFactory dataProviderFactory) {
		if (dataProviderFactory == null) {
			return;
		}
		if (!dataProviderFactories.contains(dataProviderFactory)) {
			dataProviderFactories.add(dataProviderFactory);
			registerMetaclass(dataProviderFactory.getType());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#removeDataProviderFactory(org.eclipse.e4.xwt.IDataProviderFactory)
	 */
	public void removeDataProviderFactory(IDataProviderFactory dataProvider) {
		if (dataProvider == null) {
			return;
		}
		if (dataProviderFactories.contains(dataProvider)) {
			dataProviderFactories.remove(dataProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getDataProviderFactories()
	 */
	public Collection<IDataProviderFactory> getDataProviderFactories() {
		return dataProviderFactories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findDataProvider(java.lang.Object)
	 */
	public IDataProvider findDataProvider(Object dataContext) {
		for (IDataProviderFactory factory : dataProviderFactories) {
			IDataProvider dataProvider = factory.create(dataContext);
			if (dataProvider != null) {
				return dataProvider;
			}
		}
		ObjectDataProvider dataProvider = new ObjectDataProvider();
		dataProvider.setObjectInstance(dataContext);
		return dataProvider;
	}

	private void initialize() {

		core = new Core(new ResourceLoaderFactory(), this);
		core.registerService(ValueConvertorRegister.class, new ValueConvertorRegister());

		core.registerMetaclassManager(IConstants.XWT_NAMESPACE, new MetaclassManager(null, null, this));
		core.registerMetaclass(new BindingMetaclass(this), IConstants.XWT_NAMESPACE);
		core.registerMetaclass(new TableEditorMetaclass(core.getMetaclass(ControlEditor.class, IConstants.XWT_NAMESPACE), this), IConstants.XWT_NAMESPACE);

		registerConvertor(ObjectToString.instance);
		registerConvertor(DateToString.instance);
		registerConvertor(EnumToString.instance);
		registerConvertor(StringToInteger.instance);
		// It is not supported by eclipse 3.4.1
		registerConvertor(StringToNumberConverter.class, "toBigDecimal");
		registerConvertor(StringToNumberConverter.class, "toByte", false);

		registerConvertor(StringToNumberConverter.toLong(false));

		// It is not supported by eclipse 3.4.1
		registerConvertor(StringToNumberConverter.class, "toShort", false);

		registerConvertor(StringToNumberConverter.toFloat(false));
		registerConvertor(StringToNumberConverter.toDouble(false));

		registerConvertor(NumberToStringConverter.fromInteger(false));

		// It is not supported by eclipse 3.4.1
		registerConvertor(NumberToStringConverter.class, "fromBigDecimal");
		registerConvertor(NumberToStringConverter.class, "fromByte", false);

		registerConvertor(NumberToStringConverter.fromLong(false));

		// It is not supported by eclipse 3.4.1
		registerConvertor(NumberToStringConverter.class, "fromShort", false);

		registerConvertor(NumberToStringConverter.fromFloat(false));
		registerConvertor(NumberToStringConverter.fromDouble(false));

		registerConvertor(StringToBoolean.instance);
		registerConvertor(ObjectToBoolean.instance);
		registerConvertor(SelectionToBoolean.instance);
		registerConvertor(CollectionToBoolean.instance);
		registerConvertor(StringToIntArray.instance);
		registerConvertor(BindingToObject.instance);
		registerConvertor(StringToColor.instance);
		registerConvertor(StringToFont.instance);
		registerConvertor(StringToImage.instance);
		registerConvertor(StringToPoint.instance);
		registerConvertor(StringToRectangle.instance);
		registerConvertor(StringToURL.instance);
		registerConvertor(StringToType.instance);
		registerConvertor(StringToFormAttachment.instance);

		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		convertorRegister.register(String.class, float.class, StringToNumberConverter.toFloat(true));
		convertorRegister.register(String.class, int.class, StringToInteger.instance);

		// It is not supported by eclipse 3.4.1
		// convertorRegister.register(String.class, short.class, StringToNumberConverter.toShort(true));
		registerConvertor(convertorRegister, String.class, short.class, StringToNumberConverter.class, "toShort", true);

		convertorRegister.register(String.class, long.class, StringToNumberConverter.toLong(true));

		// It is not supported by eclipse 3.4.1
		// convertorRegister.register(String.class, byte.class, StringToNumberConverter.toByte(true));
		registerConvertor(convertorRegister, String.class, byte.class, StringToNumberConverter.class, "toByte", true);

		convertorRegister.register(String.class, boolean.class, StringToBoolean.instance);
		convertorRegister.register(String.class, double.class, StringToNumberConverter.toDouble(true));

		convertorRegister.register(float.class, String.class, NumberToStringConverter.fromFloat(true));
		convertorRegister.register(int.class, String.class, NumberToStringConverter.fromInteger(true));

		// It is not supported by eclipse 3.4.1
		// convertorRegister.register(short.class, String.class, NumberToStringConverter.fromShort(true));
		registerConvertor(convertorRegister, short.class, String.class, NumberToStringConverter.class, "fromShort", true);

		convertorRegister.register(long.class, String.class, NumberToStringConverter.fromLong(true));

		// It is not supported by eclipse 3.4.1
		// convertorRegister.register(byte.class, String.class, NumberToStringConverter.fromByte(true));
		registerConvertor(convertorRegister, byte.class, String.class, NumberToStringConverter.class, "fromByte", true);

		convertorRegister.register(double.class, String.class, NumberToStringConverter.fromDouble(true));

		Class<?> type = org.eclipse.swt.browser.Browser.class;
		IMetaclass browserMetaclass = (IMetaclass) registerMetaclass(type);
		browserMetaclass.addProperty(new DynamicProperty(type, String.class, PropertiesConstants.PROPERTY_URL));
		IMetaclass buttonMetaclass = (IMetaclass) registerMetaclass(org.eclipse.swt.widgets.Button.class);
		buttonMetaclass.addProperty(new DataProperty(IConstants.XAML_COMMAND, ICommand.class, IUserDataConstants.XWT_COMMAND_KEY));

		registerMetaclass(org.eclipse.swt.widgets.Canvas.class);
		registerMetaclass(org.eclipse.swt.widgets.Caret.class);
		registerMetaclass(org.eclipse.swt.widgets.Combo.class);
		registerMetaclass(org.eclipse.swt.widgets.Composite.class);
		registerMetaclass(org.eclipse.swt.widgets.CoolBar.class);
		registerMetaclass(org.eclipse.swt.widgets.CoolItem.class);
		registerMetaclass(org.eclipse.swt.widgets.DateTime.class);
		registerMetaclass(org.eclipse.swt.widgets.Decorations.class);
		registerMetaclass(org.eclipse.swt.widgets.ExpandBar.class);
		IMetaclass expandItemMetaclass = registerMetaclass(org.eclipse.swt.widgets.ExpandItem.class);
		expandItemMetaclass.findProperty("control").addSetPostAction(new ExpandItemHeightAction());

		registerMetaclass(org.eclipse.swt.widgets.Group.class);
		registerMetaclass(org.eclipse.swt.widgets.IME.class);
		registerMetaclass(org.eclipse.swt.widgets.Label.class);
		registerMetaclass(org.eclipse.swt.widgets.Link.class);
		registerMetaclass(org.eclipse.swt.widgets.Listener.class);
		registerMetaclass(org.eclipse.swt.widgets.List.class);
		registerMetaclass(org.eclipse.swt.widgets.Menu.class);
		IMetaclass menuItemMetaclass = (IMetaclass) registerMetaclass(org.eclipse.swt.widgets.MenuItem.class);
		menuItemMetaclass.addProperty(new DataProperty(IConstants.XAML_COMMAND, ICommand.class, IUserDataConstants.XWT_COMMAND_KEY));

		registerMetaclass(org.eclipse.swt.widgets.MessageBox.class);
		registerMetaclass(org.eclipse.swt.widgets.ProgressBar.class);
		registerMetaclass(org.eclipse.swt.widgets.Sash.class);
		registerMetaclass(org.eclipse.swt.widgets.Scale.class);
		registerMetaclass(org.eclipse.swt.widgets.ScrollBar.class);
		registerMetaclass(org.eclipse.swt.widgets.Shell.class);
		registerMetaclass(org.eclipse.swt.widgets.Slider.class);
		registerMetaclass(org.eclipse.swt.widgets.Spinner.class);
		registerMetaclass(org.eclipse.swt.widgets.TabFolder.class);
		registerMetaclass(org.eclipse.swt.widgets.TabItem.class);

		registerMetaclass(org.eclipse.swt.widgets.Table.class);
		type = org.eclipse.swt.widgets.TableItem.class;
		IMetaclass metaclass = (IMetaclass) registerMetaclass(type);
		metaclass.addProperty(new TableItemProperty());
		metaclass.addProperty(new TableItemEditorProperty());
		metaclass.addProperty(new DynamicBeanProperty(TableItem.class, String[].class, PropertiesConstants.PROPERTY_TEXTS, PropertiesConstants.PROPERTY_TEXT));

		registerMetaclass(TableItemProperty.Cell.class);
		registerMetaclass(ControlEditor.class);
		registerMetaclass(TableEditor.class);

		IMetaclass TableEditorMetaclass = core.getMetaclass(TableEditor.class, IConstants.XWT_NAMESPACE);
		TableEditorMetaclass.addProperty(new TableEditorDynamicProperty());

		type = org.eclipse.swt.widgets.TableColumn.class;
		metaclass = (IMetaclass) registerMetaclass(type);
		metaclass.addProperty(new TableColumnEditorProperty());

		registerMetaclass(org.eclipse.swt.widgets.Text.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolBar.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolItem.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolTip.class);
		registerMetaclass(org.eclipse.swt.widgets.Tracker.class);
		registerMetaclass(org.eclipse.swt.widgets.Tray.class);
		registerMetaclass(org.eclipse.swt.widgets.Tree.class);
		registerMetaclass(org.eclipse.swt.widgets.TreeColumn.class);
		registerMetaclass(org.eclipse.swt.widgets.TreeItem.class);
		type = org.eclipse.swt.widgets.TreeItem.class;
		metaclass = (IMetaclass) registerMetaclass(type);
		metaclass.addProperty(new DynamicBeanProperty(TreeItem.class, String[].class, PropertiesConstants.PROPERTY_TEXTS, PropertiesConstants.PROPERTY_TEXT));

		// registerMetaclass(org.eclipse.swt.layout.FillData.class);
		registerMetaclass(org.eclipse.swt.layout.FillLayout.class);
		registerMetaclass(org.eclipse.swt.layout.FormAttachment.class);
		registerMetaclass(org.eclipse.swt.layout.FormData.class);
		registerMetaclass(org.eclipse.swt.layout.FormLayout.class);
		registerMetaclass(org.eclipse.swt.layout.GridData.class);
		registerMetaclass(org.eclipse.swt.layout.GridLayout.class);
		registerMetaclass(org.eclipse.swt.layout.RowData.class);
		registerMetaclass(org.eclipse.swt.layout.RowLayout.class);
		registerMetaclass(org.eclipse.swt.custom.StackLayout.class);

		registerMetaclass(org.eclipse.swt.custom.CLabel.class);
		registerMetaclass(org.eclipse.swt.custom.CCombo.class);
		registerMetaclass(org.eclipse.swt.custom.CTabFolder.class);
		registerMetaclass(org.eclipse.swt.custom.CTabItem.class);
		registerMetaclass(org.eclipse.swt.custom.SashForm.class);
		registerMetaclass(org.eclipse.swt.custom.StyledText.class);

		type = org.eclipse.swt.widgets.Widget.class;
		metaclass = (IMetaclass) registerMetaclass(type);
		metaclass.addProperty(new DataProperty(IConstants.XAML_DATACONTEXT, IUserDataConstants.XWT_DATACONTEXT_KEY));
		metaclass.addProperty(new StyleProperty());

		type = org.eclipse.jface.viewers.ColumnViewer.class;
		metaclass = (IMetaclass) core.getMetaclass(type, IConstants.XWT_NAMESPACE);
		if (metaclass != null) {
			metaclass.addProperty(new DynamicBeanProperty(type, String[].class, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES));
			metaclass.addProperty(new TableViewerColumnsProperty());
		}

		for (Class<?> cls : JFacesHelper.getSupportedElements()) {
			registerMetaclass(cls);
		}
		core.registerMetaclass(new ComboBoxCellEditorMetaclass(core.getMetaclass(ComboBoxCellEditor.class.getSuperclass(), IConstants.XWT_NAMESPACE), this), IConstants.XWT_NAMESPACE);

		type = org.eclipse.jface.viewers.TableViewerColumn.class;
		core.registerMetaclass(new TableViewerColumnMetaClass(core.getMetaclass(type.getSuperclass(), IConstants.XWT_NAMESPACE), this), IConstants.XWT_NAMESPACE);

		metaclass = (IMetaclass) core.getMetaclass(type, IConstants.XWT_NAMESPACE);
		metaclass.addProperty(new TableViewerColumnWidthProperty());
		metaclass.addProperty(new TableViewerColumnTextProperty());
		metaclass.addProperty(new TableViewerColumnImageProperty());
		metaclass.addProperty(new TableViewerColumnPropertyProperty());

		registerMetaclass(DefaultCellModifier.class);
		registerMetaclass(DefaultLabelProvider.class);

		registerMetaclass(ObjectDataProvider.class);

		registerMetaclass(Style.class);
		registerMetaclass(Setter.class);

		registerMetaclass(DefaultListContentProvider.class);
		registerMetaclass(ObservableListContentProvider.class);
		registerMetaclass(ObservableListTreeContentProvider.class);
		registerMetaclass(ObservableSetContentProvider.class);
		registerMetaclass(ObservableListTreeContentProvider.class);
		registerMetaclass(ObservableSetTreeContentProvider.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#findLoadingContext(java.lang.Object)
	 */
	public ILoadingContext findLoadingContext(Object container) {
		return getLoadingContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getLoadingContext(org.eclipse.swt.widgets.Composite)
	 */
	public ILoadingContext getLoadingContext(Composite object) {
		return getLoadingContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#getLoadingContext()
	 */
	public ILoadingContext getLoadingContext() {
		if (_loadingContext == null) {
			return LoadingContext.defaultLoadingContext;
		}
		return _loadingContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IXWTLoader#setLoadingContext(org.eclipse.e4.xwt.ILoadingContext)
	 */
	public void setLoadingContext(ILoadingContext loadingContext) {
		_loadingContext = loadingContext;
	}

}
