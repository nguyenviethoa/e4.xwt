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
import org.eclipse.e4.xwt.converters.DateToString;
import org.eclipse.e4.xwt.converters.EnumToString;
import org.eclipse.e4.xwt.converters.ObjectToObject;
import org.eclipse.e4.xwt.converters.ObjectToString;
import org.eclipse.e4.xwt.converters.StringToBoolean;
import org.eclipse.e4.xwt.converters.StringToColor;
import org.eclipse.e4.xwt.converters.StringToFont;
import org.eclipse.e4.xwt.converters.StringToImage;
import org.eclipse.e4.xwt.converters.StringToIntArray;
import org.eclipse.e4.xwt.converters.StringToInteger;
import org.eclipse.e4.xwt.converters.StringToPoint;
import org.eclipse.e4.xwt.converters.StringToRectangle;
import org.eclipse.e4.xwt.converters.StringToType;
import org.eclipse.e4.xwt.converters.StringToURL;
import org.eclipse.e4.xwt.dataproviders.ObjectDataProvider;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.Core;
import org.eclipse.e4.xwt.internal.IUserDataConstants;
import org.eclipse.e4.xwt.internal.LoadingContext;
import org.eclipse.e4.xwt.internal.MetaclassManager;
import org.eclipse.e4.xwt.internal.NameScope;
import org.eclipse.e4.xwt.internal.Setter;
import org.eclipse.e4.xwt.internal.Style;
import org.eclipse.e4.xwt.internal.javabean.ResourceLoaderFactory;
import org.eclipse.e4.xwt.internal.javabean.ValueConvertorRegister;
import org.eclipse.e4.xwt.internal.jface.ComboBoxCellEditor;
import org.eclipse.e4.xwt.internal.jface.DefaultCellModifier;
import org.eclipse.e4.xwt.internal.jface.DefaultLabelProvider;
import org.eclipse.e4.xwt.internal.jface.JFacesHelper;
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
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnTextProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnWidthProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnsProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.utils.ResourceManager;
import org.eclipse.e4.xwt.utils.UserDataHelper;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
public class XWTLoader {
	// Properties
	/**
	 * style of type int is used to create SWT element
	 */
	public static final String CONTAINER_PROPERTY = "XWT.Container";
	public static final String INIT_STYLE_PROPERTY = "XWT.Style";

	/**
	 * Default styles to apply. The value should be a collection or Array of IStyle
	 * 
	 */
	public static final String DEFAULT_STYLES_PROPERTY = "XWT.DefaultStyles";

	/**
	 * Enabled or disabled the styles. By default, it is enabled
	 * 
	 */
	public static final String DISBALE_STYLES_PROPERTY = "XWT.DisabledStyles";

	/**
	 * The DataContext to setup in root element
	 * 
	 */
	public static final String DATACONTEXT_PROPERTY = "XWT.DataContext";

	/**
	 * Resources to associate to root element
	 * 
	 */
	public static final String RESOURCE_DICTIONARY_PROPERTY = "XWT.Resources";

	// Declarations
	private Core core = null;
	private ILoadingContext _loadingContext = null;

	private Set<Tracking> trackingSet = new HashSet<Tracking>();
	private Map<String, ICommand> commands = new HashMap<String, ICommand>();
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

	/**
	 * Get the system logger.
	 * 
	 * @return
	 */
	public ILogger getLogger() {
		if (logger == null) {
			return Core.nullLog;
		}
		return logger;
	}

	/**
	 * Change the system logger
	 * 
	 * @param logger
	 */
	public void setLogger(ILogger log) {
		logger = log;
	}

	/**
	 * This namespace service returns the associated or declared namespace for a given class.
	 * 
	 * @param javaclass
	 * @return
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

	/**
	 * Get the name of the element, which is defined by <code>Name</code> or <code>x:Name</code>. Return <code>null</code>
	 * 
	 * @param object
	 * @return
	 */
	public String getElementName(Object object) {
		return UserDataHelper.getElementName(object);
	}

	/**
	 * A NameContext is a manager of UI element's name in a scope. A name in a NameContext must be unique.
	 * 
	 * @param widget
	 * @return
	 */
	public NameScope findNameContext(Widget widget) {
		return UserDataHelper.findNameContext(widget);
	}

	/**
	 * Find a named UI element.
	 * 
	 * @param context
	 *            the start point of research.
	 * @param name
	 * @return
	 */
	public Object findElementByName(Widget context, String name) {
		NameScope nameContext = UserDataHelper.findNameContext(context);
		if (nameContext != null) {
			return nameContext.getObject(name);
		}
		return null;
	}

	/**
	 * Get the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public Object getDataContext(Widget element) {
		return UserDataHelper.getDataContext(element);
	}

	/**
	 * Change the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public void setDataContext(Widget widget, Object dataContext) {
		UserDataHelper.setDataContext(widget, dataContext);
	}

	/**
	 * Get the CLR (Common Language Runtime) object. If no CLR object is found in this element, the research will be propagated in it parent.
	 * 
	 * @param widget
	 * @return
	 */
	public Object getCLR(Widget widget) {
		return UserDataHelper.getCLR(widget);
	}

	/**
	 * Find the root shell
	 * 
	 * @param context
	 * @return
	 */
	public Shell findShell(Widget context) {
		return UserDataHelper.findShell(context);
	}

	/**
	 * Find the closet parent of type Composite
	 * 
	 * @param context
	 * @return
	 */
	public Composite findCompositeParent(Widget context) {
		return UserDataHelper.findCompositeParent(context);
	}

	/**
	 * Get the Metaclass of the given object
	 * 
	 * @param context
	 * @return
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

	/**
	 * Load the file content. All widget will be created but they are showed. This method return the root element.
	 * 
	 */
	public synchronized Control load(URL file) throws Exception {
		return loadWithOptions(file, Collections.EMPTY_MAP);
	}

	/**
	 * Load the file content with a DataContext. All widget will be created but they are showed. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public synchronized Control load(URL file, Object dataContext) throws Exception {
		return load(null, file, dataContext);
	}

	/**
	 * Load the file content under a Composite. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public synchronized Control load(Composite parent, URL file) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		return loadWithOptions(file, options);
	}

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	public synchronized Control load(Composite parent, URL file, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		options.put(DATACONTEXT_PROPERTY, dataContext);
		return loadWithOptions(file, options);
	}

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
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

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
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

	/**
	 * Open and show the file content in a new Shell.
	 */
	public synchronized void open(Class<?> type) throws Exception {
		open(type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX), Collections.EMPTY_MAP);
	}

	/**
	 * Open and show the file content in a new Shell.
	 */
	public synchronized void open(final URL url) throws Exception {
		open(url, Collections.EMPTY_MAP);
	}

	/**
	 * load the content from a stream with a style, a DataContext and a ResourceDictionary. The root elements will be hold by Composite parent
	 */
	public synchronized Control load(Composite parent, InputStream stream, URL file, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(CONTAINER_PROPERTY, parent);
		options.put(DATACONTEXT_PROPERTY, dataContext);
		return loadWithOptions(stream, file, options);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	public synchronized void open(URL url, Object dataContext) throws Exception {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(DATACONTEXT_PROPERTY, dataContext);
		open(url, options);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	public synchronized void open(Class<?> type, Object dataContext) throws Exception {
		open(type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX), dataContext);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
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

	/**
	 * Data conversion service from String to a given type
	 * 
	 * @param type
	 * @param string
	 * @return
	 */
	public Object convertFrom(IMetaclass type, String string) {
		Class<?> targetType = type.getType();
		return convertFrom(targetType, string);
	}

	/**
	 * Data conversion service from String to a given type
	 * 
	 * @param targetType
	 * @param string
	 * @return
	 */
	public Object convertFrom(Class<?> targetType, String string) {
		if (targetType == String.class) {
			return string;
		}
		IConverter converter = findConvertor(String.class, targetType);
		if (converter != null) {
			return converter.convert(string);
		}
		throw new XWTException("Converter is missing of type: " + targetType.getName());
	}

	public synchronized Control loadWithOptions(URL url, Map<String, Object> options) throws Exception {
		Composite object = (Composite) options.get(CONTAINER_PROPERTY);
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		options = prepareOptions(options);
		Control visualObject = core.load(loadingContext, url, options);
		return visualObject;
	}

	/**
	 * 
	 * @param stream
	 * @param url
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public synchronized Control load(InputStream stream, URL url) throws Exception {
		return loadWithOptions(stream, url, Collections.EMPTY_MAP);
	}

	/**
	 * Generic load method
	 * 
	 * @param stream
	 * @param url
	 * @param loadData
	 * @return
	 * @throws Exception
	 */
	public synchronized Control loadWithOptions(InputStream stream, URL url, Map<String, Object> options) throws Exception {
		Composite object = (Composite) options.get(CONTAINER_PROPERTY);
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		options = prepareOptions(options);
		Control visualObject = core.load(loadingContext, stream, url, options);
		return visualObject;
	}

	/**
	 * Metaclass services to return all registered Metaclasses.
	 * 
	 * @param stream
	 * @param url
	 * @param loadData
	 * @return
	 * @throws Exception
	 */
	public IMetaclass[] getAllMetaclasses() {
		Collection<?> metaclasses = core.getAllMetaclasses(IConstants.XWT_NAMESPACE);
		return metaclasses.toArray(new IMetaclass[metaclasses.size()]);
	}

	/**
	 * Get the corresponding Metaclass
	 * 
	 * @param tagName
	 * @param ns
	 *            The namespace
	 * @return
	 */
	public IMetaclass getMetaclass(String tagName, String ns) {
		return (IMetaclass) core.getMetaclass(getLoadingContext(), tagName, ns);
	}

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	public IMetaclass registerMetaclass(Class<?> type) {
		return register(type, IConstants.XWT_NAMESPACE);
	}

	/**
	 * Register Metaclass factory
	 * 
	 * @param javaclass
	 */
	public void registerMetaclassFactory(IMetaclassFactory metaclassFactory) {
		core.registerMetaclassFactory(metaclassFactory);
	}

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	public IMetaclass register(Class<?> javaclass, String namespace) {
		return core.registerMetaclass(javaclass, namespace);
	}

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

	private Class<?> normalizedType(Class<?> type) {
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

	/**
	 * Find a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	public IConverter findConvertor(Class<?> source, Class<?> target) {
		source = normalizedType(source);
		target = normalizedType(target);
		if (source == target || (source != Object.class && source.isAssignableFrom(target))) {
			return ObjectToObject.instance;
		}
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		return convertorRegister.findConverter(source, target);
	}

	/**
	 * Register a Data converter
	 * 
	 * @param converter
	 * @param type
	 */
	public void registerConvertor(IConverter converter) {
		Class<?> source = (Class<?>) converter.getFromType();
		Class<?> target = (Class<?>) converter.getToType();
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		convertorRegister.register(source, target, converter);
	}

	/**
	 * Add a tracking option
	 * 
	 * @param tracking
	 */
	public void addTracking(Tracking tracking) {
		if (!trackingSet.contains(tracking)) {
			trackingSet.add(tracking);
		}
	}

	/**
	 * Test if the tracking on argument is enabled.
	 * 
	 * @param tracking
	 * @return
	 */
	public boolean isTracking(Tracking tracking) {
		return trackingSet.contains(tracking);
	}

	/**
	 * Get all tracking options
	 * 
	 * @return
	 */
	public Set<Tracking> getTrackings() {
		return trackingSet;
	}

	/**
	 * Remove a tracking option.
	 * 
	 * @param tracking
	 */
	public void removeTracking(Tracking tracking) {
		if (trackingSet.contains(tracking)) {
			trackingSet.remove(tracking);
		}
	}

	/**
	 * Register a command to a name
	 * 
	 * @param name
	 * @param command
	 */
	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	/**
	 * Find a command by name
	 * 
	 * @param name
	 * @return
	 */
	public ICommand getCommand(String name) {
		return commands.get(name);
	}

	/**
	 * Return all registered commands
	 * 
	 * @return
	 */
	public Map<String, ICommand> getCommands() {
		return commands;
	}

	/**
	 * Unregister a command
	 * 
	 * @param name
	 */
	public void unregisterCommand(String name) {
		commands.remove(name);
	}

	/**
	 * Add a default style
	 * 
	 * @param style
	 * @return
	 */
	public void addDefaultStyle(IStyle style) {
		defaultStyles.add(style);
	}

	/**
	 * Remove a default style
	 * 
	 * @param style
	 * @return
	 */
	public void removeDefaultStyle(IStyle style) {
		defaultStyles.remove(style);
	}

	public Collection<IStyle> getDefaultStyles() {
		return new ArrayList<IStyle>(defaultStyles);
	}

	public void addDataProviderFactory(IDataProviderFactory dataProviderFactory) {
		if (dataProviderFactory == null) {
			return;
		}
		if (!dataProviderFactories.contains(dataProviderFactory)) {
			dataProviderFactories.add(dataProviderFactory);
			registerMetaclass(dataProviderFactory.getType());
		}
	}

	public void removeDataProviderFactory(IDataProviderFactory dataProvider) {
		if (dataProvider == null) {
			return;
		}
		if (dataProviderFactories.contains(dataProvider)) {
			dataProviderFactories.remove(dataProvider);
		}
	}

	public Collection<IDataProviderFactory> getDataProviderFactories() {
		return dataProviderFactories;
	}

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
		core.registerMetaclass(new TableEditorMetaclass(core.getMetaclass(ControlEditor.class), this), IConstants.XWT_NAMESPACE);

		registerConvertor(ObjectToString.instance);
		registerConvertor(DateToString.instance);
		registerConvertor(EnumToString.instance);
		registerConvertor(StringToInteger.instance);
		registerConvertor(StringToNumberConverter.toBigDecimal());
		registerConvertor(StringToNumberConverter.toByte(false));
		registerConvertor(StringToNumberConverter.toLong(false));
		registerConvertor(StringToNumberConverter.toShort(false));
		registerConvertor(StringToNumberConverter.toFloat(false));
		registerConvertor(StringToNumberConverter.toDouble(false));

		registerConvertor(NumberToStringConverter.fromInteger(false));
		registerConvertor(NumberToStringConverter.fromBigDecimal());
		registerConvertor(NumberToStringConverter.fromByte(false));
		registerConvertor(NumberToStringConverter.fromLong(false));
		registerConvertor(NumberToStringConverter.fromShort(false));
		registerConvertor(NumberToStringConverter.fromFloat(false));
		registerConvertor(NumberToStringConverter.fromDouble(false));

		registerConvertor(StringToBoolean.instance);
		registerConvertor(StringToIntArray.instance);
		registerConvertor(BindingToObject.instance);
		registerConvertor(StringToColor.instance);
		registerConvertor(StringToFont.instance);
		registerConvertor(StringToImage.instance);
		registerConvertor(StringToPoint.instance);
		registerConvertor(StringToRectangle.instance);
		registerConvertor(StringToURL.instance);
		registerConvertor(StringToType.instance);

		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		convertorRegister.register(String.class, float.class, StringToNumberConverter.toFloat(true));
		convertorRegister.register(String.class, int.class, StringToInteger.instance);
		convertorRegister.register(String.class, short.class, StringToNumberConverter.toShort(true));
		convertorRegister.register(String.class, long.class, StringToNumberConverter.toLong(true));
		convertorRegister.register(String.class, byte.class, StringToNumberConverter.toByte(true));
		convertorRegister.register(String.class, boolean.class, StringToBoolean.instance);
		convertorRegister.register(String.class, double.class, StringToNumberConverter.toDouble(true));

		convertorRegister.register(float.class, String.class, NumberToStringConverter.fromFloat(true));
		convertorRegister.register(int.class, String.class, NumberToStringConverter.fromInteger(true));
		convertorRegister.register(short.class, String.class, NumberToStringConverter.fromShort(true));
		convertorRegister.register(long.class, String.class, NumberToStringConverter.fromLong(true));
		convertorRegister.register(byte.class, String.class, NumberToStringConverter.fromByte(true));
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

		IMetaclass TableEditorMetaclass = getMetaclass(TableEditor.class);
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
		metaclass = (IMetaclass) core.getMetaclass(type);
		if (metaclass != null) {
			metaclass.addProperty(new DynamicBeanProperty(type, String[].class, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES));
			metaclass.addProperty(new TableViewerColumnsProperty());
		}

		for (Class<?> cls : JFacesHelper.getSupportedElements()) {
			registerMetaclass(cls);
		}
		core.registerMetaclass(new ComboBoxCellEditorMetaclass(core.getMetaclass(ComboBoxCellEditor.class.getSuperclass()), this), IConstants.XWT_NAMESPACE);

		type = org.eclipse.jface.viewers.TableViewerColumn.class;
		core.registerMetaclass(new TableViewerColumnMetaClass(core.getMetaclass(type.getSuperclass()), this), IConstants.XWT_NAMESPACE);

		metaclass = (IMetaclass) core.getMetaclass(type);
		metaclass.addProperty(new TableViewerColumnWidthProperty());
		metaclass.addProperty(new TableViewerColumnTextProperty());

		registerMetaclass(DefaultCellModifier.class);
		registerMetaclass(DefaultLabelProvider.class);

		registerMetaclass(ObjectDataProvider.class);

		registerMetaclass(Style.class);
		registerMetaclass(Setter.class);

	}

	public ILoadingContext findLoadingContext(Object container) {
		return getLoadingContext();
	}

	public ILoadingContext getLoadingContext(Composite object) {
		return getLoadingContext();
	}

	public ILoadingContext getLoadingContext() {
		if (_loadingContext == null) {
			return LoadingContext.defaultLoadingContext;
		}
		return _loadingContext;
	}

	public void setLoadingContext(ILoadingContext loadingContext) {
		_loadingContext = loadingContext;
	}

}
