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
package org.eclipse.e4.xwt;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.converters.BindingToObject;
import org.eclipse.e4.xwt.converters.DateToString;
import org.eclipse.e4.xwt.converters.ObjectToObject;
import org.eclipse.e4.xwt.converters.ObjectToString;
import org.eclipse.e4.xwt.converters.StringToBoolean;
import org.eclipse.e4.xwt.converters.StringToColor;
import org.eclipse.e4.xwt.converters.StringToDouble;
import org.eclipse.e4.xwt.converters.StringToFloat;
import org.eclipse.e4.xwt.converters.StringToFont;
import org.eclipse.e4.xwt.converters.StringToImage;
import org.eclipse.e4.xwt.converters.StringToIntArray;
import org.eclipse.e4.xwt.converters.StringToInteger;
import org.eclipse.e4.xwt.converters.StringToPoint;
import org.eclipse.e4.xwt.converters.StringToRectangle;
import org.eclipse.e4.xwt.impl.Core;
import org.eclipse.e4.xwt.impl.IUserDataConstants;
import org.eclipse.e4.xwt.impl.LoadData;
import org.eclipse.e4.xwt.impl.LoadingContext;
import org.eclipse.e4.xwt.impl.NameScope;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.javabean.ResourceLoaderFactory;
import org.eclipse.e4.xwt.javabean.ValueConvertorRegister;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.ComboBoxCellEditorMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.ExpandItemHeightAction;
import org.eclipse.e4.xwt.javabean.metadata.Metaclass;
import org.eclipse.e4.xwt.javabean.metadata.MetaclassManager;
import org.eclipse.e4.xwt.javabean.metadata.TableEditorMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.TableViewerColumnMetaClass;
import org.eclipse.e4.xwt.javabean.metadata.properties.DataProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicBeanProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableColumnEditorProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableEditorDynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemEditorProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnTextProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnWidthProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableViewerColumnsProperty;
import org.eclipse.e4.xwt.jface.ComboBoxCellEditor;
import org.eclipse.e4.xwt.jface.DefaultCellModifier;
import org.eclipse.e4.xwt.jface.DefaultLabelProvider;
import org.eclipse.e4.xwt.jface.JFacesHelper;
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
 * XWT is the main class of the XWT framework. It provides most of the services in API.
 * 
 * @author yyang
 */
public class XWT {
	static Core core = new Core(new ResourceLoaderFactory());

	private static boolean initialized = false;

	private static ILoadingContext loadingContext;

	private static Set<Tracking> trackingSet = new HashSet<Tracking>();
	private static Map<String, ICommand> commands = new HashMap<String, ICommand>();
	private static ILogger logger;

	/**
	 * Get the system logger.
	 * 
	 * @return
	 */
	public static ILogger getLogger() {
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
	public static void setLogger(ILogger logger) {
		XWT.logger = logger;
	}

	/**
	 * Check if the framework is initialized.
	 * 
	 * @return
	 */
	public static boolean IsInitialized() {
		return initialized;
	}

	/**
	 * This namespace service returns the associated or declared namespace for a given class.
	 * 
	 * @param javaclass
	 * @return
	 */
	public static String getNamespace(Class<?> javaclass) {
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
	 * A NameContext is a manager of UI element's name in a scope. A name in a NameContext must be unique.
	 * 
	 * @param widget
	 * @return
	 */
	public static NameScope findNameContext(Widget widget) {
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
	public static Object findElementByName(Widget context, String name) {
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
	public static Object getDataContext(Widget element) {
		return UserDataHelper.getDataContext(element);
	}

	/**
	 * Change the DataContext of given element
	 * 
	 * @param context
	 * @return
	 */
	public static void setDataContext(Widget widget, Object dataContext) {
		UserDataHelper.setDataContext(widget, dataContext);
	}

	/**
	 * Get the CLR (Common Language Runtime) object. If no CLR object is found in this element, the research will be propagated in it parent.
	 * 
	 * @param widget
	 * @return
	 */
	public static Object getCLR(Widget widget) {
		return UserDataHelper.getCLR(widget);
	}

	/**
	 * Find the root shell
	 * 
	 * @param context
	 * @return
	 */
	public static Shell findShell(Widget context) {
		return UserDataHelper.findShell(context);
	}

	/**
	 * Find the closet parent of type Composite
	 * 
	 * @param context
	 * @return
	 */
	public static Composite findCompositeParent(Widget context) {
		return UserDataHelper.findCompositeParent(context);
	}

	/**
	 * Get the Metaclass of the given object
	 * 
	 * @param context
	 * @return
	 */
	public static IMetaclass getMetaclass(Object object) {
		return core.getMetaclass(object);
	}

	static class ConverterService {
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
	static public synchronized Control load(URL file) throws Exception {
		return load(file, (ResourceDictionary) null);
	}

	/**
	 * Load the file content with a DataContext. All widget will be created but they are showed. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(URL file, int styles, Object dataContext) throws Exception {
		return load(null, file, styles, dataContext, null);
	}

	/**
	 * Load the file content with a DataContext and a ResourceDictionary. All widget will be created but they are showed. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(URL file, int styles, Object dataContext, ResourceDictionary dictionary) throws Exception {
		return load(null, file, styles, dataContext, dictionary);
	}

	/**
	 * Load the file content with a DataContext. All widget will be created but they are showed. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(URL file, Object dataContext) throws Exception {
		return load(null, file, dataContext);
	}

	/**
	 * Load the file content under a Composite. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file) throws Exception {
		return load(parent, file, -1, null);
	}

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file, Object dataContext) throws Exception {
		return load(parent, file, -1, dataContext);
	}

	/**
	 * Load the file content under a Composite with a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, Class<?> viewType, Object dataContext) throws Exception {
		ILoadingContext context = getLoadingContext();
		try {
			setLoadingContext(new LoadingContext(viewType.getClassLoader()));
			return load(parent, viewType.getResource(viewType.getSimpleName() + ".xwt"), -1, dataContext);
		} finally {
			setLoadingContext(context);
		}
	}

	/**
	 * Load the file content under a Composite with a style and a DataContext. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file, int styles, Object dataContext) throws Exception {
		return load(parent, file, -1, dataContext, null);
	}

	/**
	 * Load the file content under a Composite with a style, a DataContext and a ResourceDictionary. All widget will be created. This method returns the root element. The DataContext will be associated to the root element.
	 */
	static public synchronized Control load(Composite parent, URL file, int styles, Object dataContext, ResourceDictionary dictionary) throws Exception {
		ILoadData loadData = ILoadData.DefaultLoadData;
		if (dataContext != null || parent != null) {
			loadData = new LoadData();
			loadData.setParent(parent);
			loadData.setDataContext(dataContext);
		}
		return load(file, loadData);
	}

	/**
	 * Open and show the file content in a new Shell.
	 */
	static public synchronized void open(final URL url) throws Exception {
		open(url, null);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized Control load(InputStream stream, URL file) throws Exception {
		return load(stream, file, ILoadData.DefaultLoadData);
	}

	/**
	 * load the content from a stream with a given DataContext. The root elements will be hold by Composite parent
	 */
	static public synchronized Control load(Composite parent, InputStream stream, URL file, Object dataContext) throws Exception {
		return load(parent, stream, file, -1, dataContext);
	}

	/**
	 * load the content from a stream with a style and a DataContext. The root elements will be hold by Composite parent
	 */
	static public synchronized Control load(Composite parent, InputStream stream, URL file, int styles, Object dataContext) throws Exception {
		return load(parent, stream, file, -1, dataContext, null);
	}

	/**
	 * load the content from a stream with a style, a DataContext and a ResourceDictionary. The root elements will be hold by Composite parent
	 */
	static public synchronized Control load(Composite parent, InputStream stream, URL file, int styles, Object dataContext, ResourceDictionary dictionary) throws Exception {
		LoadData loadData = new LoadData();
		loadData.setParent(parent);
		loadData.setDataContext(dataContext);
		loadData.setStyles(styles);
		loadData.setResourceDictionary(dictionary);
		return load(stream, file, loadData);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(URL url, Object dataContext) throws Exception {
		open(url, dataContext, null);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(final URL url, final Object dataContext, final ResourceDictionary dictionary) throws Exception {

		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), new Runnable() {
			public void run() {
				try {
					Control control = load(url, -1, dataContext, dictionary);
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
	static public Object convertFrom(IMetaclass type, String string) {
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
	static public Object convertFrom(Class<?> targetType, String string) {
		if (targetType == String.class) {
			return string;
		}
		IConverter converter = findConvertor(String.class, targetType);
		if (converter != null) {
			return converter.convert(string);
		}
		throw new XWTException("Converter is missing of type: " + targetType.getName());
	}

	static private synchronized Control load(URL url, ILoadData loadData) throws Exception {
		checkInitialize();
		Composite object = loadData.getParent();
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		Control visualObject = core.load(loadingContext, url, loadData);
		return visualObject;
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
	static public synchronized Control load(InputStream stream, URL url, ILoadData loadData) throws Exception {
		checkInitialize();
		Composite object = loadData.getParent();
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		Control visualObject = core.load(loadingContext, stream, url, loadData);
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
	static public IMetaclass[] getAllMetaclasses() {
		checkInitialize();
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
	static public IMetaclass getMetaclass(String tagName, String ns) {
		checkInitialize();
		return (IMetaclass) core.getMetaclass(getLoadingContext(), tagName, ns);
	}

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	static public IMetaclass registerMetaclass(Class<?> type) {
		checkInitialize();
		return register(type, IConstants.XWT_NAMESPACE);
	}

	/**
	 * Register Metaclass factory
	 * 
	 * @param javaclass
	 */
	static public void registerMetaclassFactory(IMetaclassFactory metaclassFactory) {
		checkInitialize();
		core.registerMetaclassFactory(metaclassFactory);
	}

	/**
	 * Register UI type
	 * 
	 * @param javaclass
	 */
	static public IMetaclass register(Class<?> javaclass, String namespace) {
		checkInitialize();
		return core.registerMetaclass(javaclass, namespace);
	}

	static public ConverterService getConverterService() {
		checkInitialize();

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

	static private Class<?> normalizedType(Class<?> type) {
		if (type == int.class) {
			return Integer.class;
		}
		if (type == double.class) {
			return Double.class;
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
	static public IConverter findConvertor(Class<?> source, Class<?> target) {
		checkInitialize();
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
	static public void registerConvertor(IConverter converter) {
		checkInitialize();
		Class<?> source = (Class<?>) converter.getFromType();
		Class<?> target = (Class<?>) converter.getToType();
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		convertorRegister.register(source, target, converter);
	}

	static protected void checkInitialize() {
		if (initialized) {
			return;
		}
		initialized = true;

		core.registerService(ValueConvertorRegister.class, new ValueConvertorRegister());
		core.registerMetaclassManager(IConstants.XWT_NAMESPACE, new MetaclassManager(null, null));
		core.registerMetaclass(new BindingMetaclass(), IConstants.XWT_NAMESPACE);
		core.registerMetaclass(new TableEditorMetaclass(core.getMetaclass(ControlEditor.class)), IConstants.XWT_NAMESPACE);

		XWT.registerConvertor(new ObjectToString());
		XWT.registerConvertor(new DateToString());
		XWT.registerConvertor(new StringToInteger());
		XWT.registerConvertor(new StringToFloat());
		XWT.registerConvertor(new StringToDouble());
		XWT.registerConvertor(new StringToBoolean());
		XWT.registerConvertor(new StringToIntArray());
		XWT.registerConvertor(new BindingToObject());
		XWT.registerConvertor(new StringToColor());
		XWT.registerConvertor(new StringToFont());
		XWT.registerConvertor(new StringToImage());
		XWT.registerConvertor(new StringToPoint());
		XWT.registerConvertor(new StringToRectangle());

		Class<?> type = org.eclipse.swt.browser.Browser.class;
		Metaclass browserMetaclass = (Metaclass) XWT.registerMetaclass(type);
		browserMetaclass.addProperty(new DynamicProperty(type, String.class, PropertiesConstants.PROPERTY_URL));
		Metaclass buttonMetaclass = (Metaclass) XWT.registerMetaclass(org.eclipse.swt.widgets.Button.class);
		buttonMetaclass.addProperty(new DataProperty(ICommand.class, IConstants.XAML_COMMAND, IUserDataConstants.XWT_COMMAND_KEY));

		XWT.registerMetaclass(org.eclipse.swt.widgets.Canvas.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Caret.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Combo.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Composite.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.CoolBar.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.CoolItem.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.DateTime.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Decorations.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ExpandBar.class);
		IMetaclass expandItemMetaclass = XWT.registerMetaclass(org.eclipse.swt.widgets.ExpandItem.class);
		expandItemMetaclass.findProperty("control").addSetPostAction(new ExpandItemHeightAction());

		XWT.registerMetaclass(org.eclipse.swt.widgets.Group.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.IME.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Label.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Link.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Listener.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.List.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Menu.class);
		Metaclass menuItemMetaclass = (Metaclass) XWT.registerMetaclass(org.eclipse.swt.widgets.MenuItem.class);
		menuItemMetaclass.addProperty(new DataProperty(ICommand.class, IConstants.XAML_COMMAND, IUserDataConstants.XWT_COMMAND_KEY));

		XWT.registerMetaclass(org.eclipse.swt.widgets.MessageBox.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ProgressBar.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Sash.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Scale.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ScrollBar.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Shell.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Slider.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Spinner.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.TabFolder.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.TabItem.class);

		XWT.registerMetaclass(org.eclipse.swt.widgets.Table.class);
		type = org.eclipse.swt.widgets.TableItem.class;
		Metaclass metaclass = (Metaclass) XWT.registerMetaclass(type);
		metaclass.addProperty(new TableItemProperty());
		metaclass.addProperty(new TableItemEditorProperty());
		metaclass.addProperty(new DynamicBeanProperty(TableItem.class, String[].class, PropertiesConstants.PROPERTY_TEXTS, PropertiesConstants.PROPERTY_TEXT));

		XWT.registerMetaclass(TableItemProperty.Cell.class);
		XWT.registerMetaclass(ControlEditor.class);
		XWT.registerMetaclass(TableEditor.class);

		IMetaclass TableEditorMetaclass = XWT.getMetaclass(TableEditor.class);
		TableEditorMetaclass.addProperty(new TableEditorDynamicProperty());

		type = org.eclipse.swt.widgets.TableColumn.class;
		metaclass = (Metaclass) XWT.registerMetaclass(type);
		metaclass.addProperty(new TableColumnEditorProperty());

		XWT.registerMetaclass(org.eclipse.swt.widgets.Text.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ToolBar.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ToolItem.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.ToolTip.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Tracker.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Tray.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.Tree.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.TreeColumn.class);
		XWT.registerMetaclass(org.eclipse.swt.widgets.TreeItem.class);
		type = org.eclipse.swt.widgets.TreeItem.class;
		metaclass = (Metaclass) XWT.registerMetaclass(type);
		metaclass.addProperty(new DynamicBeanProperty(TreeItem.class, String[].class, PropertiesConstants.PROPERTY_TEXTS, PropertiesConstants.PROPERTY_TEXT));

		// XWT.registerMetaclass(org.eclipse.swt.layout.FillData.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.FillLayout.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.FormAttachment.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.FormData.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.FormLayout.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.GridData.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.GridLayout.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.RowData.class);
		XWT.registerMetaclass(org.eclipse.swt.layout.RowLayout.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.StackLayout.class);

		XWT.registerMetaclass(org.eclipse.swt.custom.CLabel.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.CCombo.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.CTabFolder.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.CTabItem.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.SashForm.class);
		XWT.registerMetaclass(org.eclipse.swt.custom.StyledText.class);

		type = org.eclipse.swt.widgets.Widget.class;
		metaclass = (Metaclass) XWT.registerMetaclass(type);
		metaclass.addProperty(new DataProperty(IConstants.XAML_DATACONTEXT, IUserDataConstants.XWT_DATACONTEXT_KEY));
		for (Class<?> cls : JFacesHelper.getSupportedElements()) {
			XWT.registerMetaclass(cls);
		}
		core.registerMetaclass(new ComboBoxCellEditorMetaclass(core.getMetaclass(ComboBoxCellEditor.class.getSuperclass())), IConstants.XWT_NAMESPACE);

		type = org.eclipse.jface.viewers.TableViewer.class;
		metaclass = (Metaclass) core.getMetaclass(type);
		if (metaclass != null) {
			metaclass.addProperty(new DynamicBeanProperty(type, String[].class, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES, PropertiesConstants.PROPERTY_COLUMN_PROPERTIES));
			metaclass.addProperty(new TableViewerColumnsProperty());
		}

		type = org.eclipse.jface.viewers.TableViewerColumn.class;
		core.registerMetaclass(new TableViewerColumnMetaClass(core.getMetaclass(type.getSuperclass())), IConstants.XWT_NAMESPACE);

		metaclass = (Metaclass) core.getMetaclass(type);
		metaclass.addProperty(new TableViewerColumnWidthProperty());
		metaclass.addProperty(new TableViewerColumnTextProperty());

		XWT.registerMetaclass(DefaultCellModifier.class);
		XWT.registerMetaclass(DefaultLabelProvider.class);
	}

	public static ILoadingContext findLoadingContext(Object container) {
		checkInitialize();
		return getLoadingContext();
	}

	public static ILoadingContext getLoadingContext(Composite object) {
		checkInitialize();
		return getLoadingContext();
	}

	public static ILoadingContext getLoadingContext() {
		checkInitialize();
		if (loadingContext == null) {
			return LoadingContext.defaultLoadingContext;
		}
		return loadingContext;
	}

	public static void setLoadingContext(ILoadingContext loadingContext) {
		checkInitialize();
		XWT.loadingContext = loadingContext;
	}

	/**
	 * Add a tracking option
	 * 
	 * @param tracking
	 */
	static public void addTracking(Tracking tracking) {
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
	static public boolean isTracking(Tracking tracking) {
		return trackingSet.contains(tracking);
	}

	/**
	 * Get all tracking options
	 * 
	 * @return
	 */
	static public Set<Tracking> getTrackings() {
		return trackingSet;
	}

	/**
	 * Remove a tracking option.
	 * 
	 * @param tracking
	 */
	static public void removeTracking(Tracking tracking) {
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
	static public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	/**
	 * Find a command by name
	 * 
	 * @param name
	 * @return
	 */
	static public ICommand getCommand(String name) {
		return commands.get(name);
	}

	/**
	 * Return all registered commands
	 * 
	 * @return
	 */
	static public Map<String, ICommand> getCommands() {
		return commands;
	}

	/**
	 * Unregister a command
	 * 
	 * @param name
	 */
	static public void unregisterCommand(String name) {
		commands.remove(name);
	}
}
