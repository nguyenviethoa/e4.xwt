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
import java.util.Map;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.converters.BindingToObject;
import org.eclipse.e4.xwt.converters.DateToString;
import org.eclipse.e4.xwt.converters.IntegerToString;
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
import org.eclipse.e4.xwt.impl.LoadData;
import org.eclipse.e4.xwt.impl.LoadingContext;
import org.eclipse.e4.xwt.javabean.ResourceLoaderFactory;
import org.eclipse.e4.xwt.javabean.ValueConvertorRegister;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.DataContext;
import org.eclipse.e4.xwt.javabean.metadata.DynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.ExpandItemHeightAction;
import org.eclipse.e4.xwt.javabean.metadata.Metaclass;
import org.eclipse.e4.xwt.javabean.metadata.MetaclassManager;
import org.eclipse.e4.xwt.javabean.metadata.TableItemProperty;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.utils.JFacesHelper;
import org.eclipse.e4.xwt.utils.ResourceManager;
import org.eclipse.e4.xwt.utils.UserDataHelper;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu
 */
public class XWT {
	public static final String ID = "org.soyatec.xaswt.core";

	static Core core = new Core(new ResourceLoaderFactory());

	private static boolean initialized = false;

	private static ILoadingContext loadingContext;

	public static boolean IsInitialized() {
		return initialized;
	}

	public static String getNamespace(Class<?> javaclass) {
		if (getMetaclass(javaclass) != null) {
			return IConstants.XWT_NAMESPACE;
		}
		Package javaPackage = javaclass.getPackage();
		if (javaPackage == null) {
			return IConstants.XWT_CLR_NAMESPACE_PROTO;
		}
		return IConstants.XWT_CLR_NAMESPACE_PROTO + javaclass.getPackage().getName();
	}

	public static NameContext findNameContext(Widget widget) {
		return UserDataHelper.findNameContext(widget);
	}

	public static Object findElementByName(Widget context, String name) {
		NameContext nameContext = UserDataHelper.findNameContext(context);
		if (nameContext != null) {
			return nameContext.getObject(name);
		}
		return null;
	}

	public static Class<?> getCLR(Widget widget) {
		return UserDataHelper.getCLR(widget);
	}

	public static Shell findShell(Widget context) {
		return UserDataHelper.findShell(context);
	}

	public static Composite findCompositeParent(Widget context) {
		return UserDataHelper.findCompositeParent(context);
	}

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
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized Control load(URL file) throws Exception {
		return load(file, (ResourceDictionary) null);
	}

	static public synchronized Control load(URL file, Object dataContext) throws Exception {
		return load(null, file, dataContext);
	}

	static public synchronized Control load(Composite parent, URL file, Object dataContext) throws Exception {
		return load(parent, file, -1, dataContext);
	}

	static public synchronized Control load(Composite parent, URL file, int styles, Object dataContext) throws Exception {
		ILoadData loadData = ILoadData.DefaultLoadData;
		if (dataContext != null || parent != null) {
			loadData = new LoadData();
			loadData.setParent(parent);
			loadData.setDataContext(dataContext);
		}
		return load(file, loadData);
	}

	static public synchronized Control load(URL file, ResourceDictionary dico, Object dataContext) throws Exception {
		ILoadData loadData = ILoadData.DefaultLoadData;
		if (dico != null || dataContext != null) {
			loadData = new LoadData();
			loadData.setResourceDictionary(dico);
			loadData.setDataContext(dataContext);
		}
		return load(file, loadData);
	}

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
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(URL url, Object dataContext) throws Exception {
		open(url, null, dataContext);
	}

	/**
	 * load the file content. The corresponding UI element is not yet created
	 */
	static public synchronized void open(final URL url, final ResourceDictionary dico, final Object dataContext) throws Exception {

		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), new Runnable() {
			public void run() {
				try {
					Control control = load(url, dico, dataContext);
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

	static public Object convertFrom(IMetaclass type, String string) {
		Class<?> targetType = type.getType();
		return convertFrom(targetType, string);
	}

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

	static public synchronized Widget load(String file) throws Exception {
		return load(new URL(file), (ResourceDictionary) null);
	}

	static public synchronized Widget load(Composite parent, String file) throws Exception {
		return load(parent, new URL(file), -1);
	}

	static public synchronized Widget load(Composite parent, String file, int styles) throws Exception {
		return load(parent, new URL(file), styles);
	}

	static public synchronized Widget load(Composite parent, URL file) throws Exception {
		return load(parent, file, -1);
	}

	static public synchronized Widget load(Composite parent, URL file, int styles) throws Exception {
		LoadData loadData = new LoadData();
		loadData.setParent(parent);
		loadData.setStyles(styles);
		return load(file, loadData);
	}

	static private synchronized Control load(URL url, ILoadData loadData) throws Exception {
		checkInitialize();
		Composite object = loadData.getParent();
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		Control visualObject = core.load(loadingContext, url, loadData);
		return visualObject;
	}

	static public synchronized Control load(InputStream stream, URL url, ILoadData loadData) throws Exception {
		checkInitialize();
		Composite object = loadData.getParent();
		ILoadingContext loadingContext = (object != null ? getLoadingContext(object) : getLoadingContext());
		Control visualObject = core.load(loadingContext, stream, url, loadData);
		return visualObject;
	}

	static public IMetaclass[] getAllMetaclasses() {
		checkInitialize();
		Collection<?> metaclasses = core.getAllMetaclasses(IConstants.XWT_NAMESPACE);
		return metaclasses.toArray(new IMetaclass[metaclasses.size()]);
	}

	static public IMetaclass getMetaclass(String tagName, String ns) {
		checkInitialize();
		return (IMetaclass) core.getMetaclass(getLoadingContext(), tagName, ns);
	}

	/**
	 * Register UI elementIRenderActivator
	 * 
	 * @param javaclass
	 */
	static public IMetaclass registerMetaclass(Class type) {
		checkInitialize();
		return register(type, IConstants.XWT_NAMESPACE);
	}

	/**
	 * Register UI element
	 * 
	 * @param javaclass
	 */
	static public IMetaclass register(Class<?> javaclass, String namespace) {
		checkInitialize();
		return core.registerMetaclass(javaclass, namespace);
	}

	static public void registerValueConverter(IConverter converter, Class<?> type) {
		checkInitialize();
		getConverterService().register(type, converter);
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

	static public IConverter findConvertor(Class<?> source, Class<?> target) {
		checkInitialize();
		source = normalizedType(source);
		target = normalizedType(target);
		if (source == target) {
			return ObjectToObject.instance;
		}
		ValueConvertorRegister convertorRegister = (ValueConvertorRegister) core.getService(ValueConvertorRegister.class);
		return convertorRegister.findConverter(source, target);
	}

	static public void registerConvertor(IConverter converter) {
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

		registerConvertor(new ObjectToString());
		registerConvertor(new IntegerToString());
		registerConvertor(new DateToString());
		registerConvertor(new StringToInteger());
		registerConvertor(new StringToBoolean());
		registerConvertor(new StringToIntArray());
		registerConvertor(new BindingToObject());
		registerConvertor(new StringToColor());
		registerConvertor(new StringToFont());
		registerConvertor(new StringToImage());
		registerConvertor(new StringToPoint());
		registerConvertor(new StringToRectangle());

		core.registerMetaclassManager(IConstants.XWT_NAMESPACE, new MetaclassManager(null));

		core.registerMetaclass(new BindingMetaclass(), IConstants.XWT_NAMESPACE);

		Class<?> type = org.eclipse.swt.browser.Browser.class;
		Metaclass browserMetaclass = (Metaclass) registerMetaclass(type);
		// Can't load url setter from BeanInfo.
		browserMetaclass.addProperty(new DynamicProperty(type, String.class, "url"));
		registerMetaclass(org.eclipse.swt.widgets.Button.class);
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
		registerMetaclass(org.eclipse.swt.widgets.MenuItem.class);
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
		Metaclass metaclass = (Metaclass) registerMetaclass(type);
		metaclass.addArrayProperty(new DynamicProperty(type, String[].class, "text"));
		metaclass.addProperty(new TableItemProperty(type, Collection.class, "cells"));

		registerMetaclass(TableItemProperty.Cell.class);

		registerMetaclass(org.eclipse.swt.widgets.TableColumn.class);
		registerMetaclass(org.eclipse.swt.widgets.Text.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolBar.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolItem.class);
		registerMetaclass(org.eclipse.swt.widgets.ToolTip.class);
		registerMetaclass(org.eclipse.swt.widgets.Tracker.class);
		registerMetaclass(org.eclipse.swt.widgets.Tray.class);
		registerMetaclass(org.eclipse.swt.widgets.Tree.class);
		registerMetaclass(org.eclipse.swt.widgets.TreeColumn.class);
		// registerMetaclass(org.eclipse.swt.widgets.TreeItem.class);
		type = org.eclipse.swt.widgets.TreeItem.class;
		metaclass = (Metaclass) registerMetaclass(type);
		metaclass.addArrayProperty(new DynamicProperty(type, String[].class, "text"));

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
		metaclass = (Metaclass) registerMetaclass(type);
		metaclass.addProperty(new DataContext("DataContext"));
		for (Class cls : JFacesHelper.getSupportedElements()) {
			registerMetaclass(cls);
		}
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
}
