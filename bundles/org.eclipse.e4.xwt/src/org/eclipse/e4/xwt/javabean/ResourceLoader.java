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
package org.eclipse.e4.xwt.javabean;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IIndexedElement;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.XWTMaps;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IRenderingContext;
import org.eclipse.e4.xwt.core.IUserDataConstants;
import org.eclipse.e4.xwt.core.IVisualElementLoader;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.core.DataBindingTrack;
import org.eclipse.e4.xwt.internal.core.NameScope;
import org.eclipse.e4.xwt.internal.core.Setter;
import org.eclipse.e4.xwt.internal.core.Style;
import org.eclipse.e4.xwt.internal.utils.ClassLoaderUtil;
import org.eclipse.e4.xwt.internal.utils.DocumentObjectSorter;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.NamespaceHelper;
import org.eclipse.e4.xwt.internal.utils.ObjectUtil;
import org.eclipse.e4.xwt.internal.utils.TableEditorHelper;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.e4.xwt.internal.xml.Attribute;
import org.eclipse.e4.xwt.internal.xml.DocumentObject;
import org.eclipse.e4.xwt.internal.xml.Element;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass;
import org.eclipse.e4.xwt.javabean.metadata.Metaclass;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass.Binding;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.utils.PathHelper;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class ResourceLoader implements IVisualElementLoader {
	static Map<String, Object> EMPTY_MAP = Collections.EMPTY_MAP;

	static final String RESOURCE_LOADER_PROPERTY = "XWT.ResourceLoader";

	private static final HashMap<String, Collection<Class<?>>> DELAYED_ATTRIBUTES = new HashMap<String, Collection<Class<?>>>();
	private static final String COLUMN = "Column";

	protected ResourceLoader parentLoader;
	protected IRenderingContext context;
	protected IXWTLoader loader;

	protected Object scopedObject;
	protected NameScope nameScoped;
	protected LoadingData loadData = new LoadingData();

	class LoadingData {
		protected LoadingData parent;
		protected Object clr;
		protected Collection<IStyle> styles = Collections.EMPTY_LIST;
		private Object loadedObject = null;
		private Method loadedMethod = null;
		private Widget hostWidget = null;
		private Widget currentWidget = null;

		public Widget getCurrentWidget() {
			return currentWidget;
		}

		public void setCurrentWidget(Widget currentWidget) {
			this.currentWidget = currentWidget;
		}

		public LoadingData getParent() {
			return parent;
		}

		public LoadingData() {
		}

		public LoadingData(LoadingData loadingData) {
			this.loadedObject = loadingData.loadedObject;
			this.loadedMethod = loadingData.loadedMethod;
			this.hostWidget = loadingData.hostWidget;
			this.parent = loadingData;
			this.styles = loadingData.styles;
			this.clr = loadingData.clr;
			this.currentWidget = loadingData.currentWidget;
		}

		public Collection<IStyle> getStyles() {
			return styles;
		}

		public void setStyles(Collection<IStyle> styles) {
			this.styles = styles;
		}

		public Object getClr() {
			return clr;
		}

		public void setClr(Object clr) {
			this.clr = clr;
		}

		public void updateEvent(IRenderingContext context, Widget control, IEvent event, Attribute attribute, String propertyName) {
			Controller eventController = (Controller) control.getData(IUserDataConstants.XWT_CONTROLLER_KEY);
			if (eventController == null) {
				eventController = new Controller();
				control.setData(IUserDataConstants.XWT_CONTROLLER_KEY, eventController);
			}
			Method method = null;
			Object clrObject = null;
			String methodName = attribute.getContent();
			LoadingData current = this;
			ResourceLoader currentParentLoader = parentLoader;
			while (current != null) {
				Object receiver = current.getClr();
				if (receiver != null) {
					Class<?> clazz = receiver.getClass();
					method = ObjectUtil.findMethod(clazz, methodName, Event.class);
					if (method == null) {
						// Load again.
						clazz = ClassLoaderUtil.loadClass(context.getLoadingContext(), clazz.getName());
						method = ObjectUtil.findMethod(clazz, methodName, Event.class);
					}
					if (method != null) {
						clrObject = receiver;
						if (propertyName.equalsIgnoreCase(Metaclass.LOADED)) {
							method.setAccessible(true);
							this.loadedObject = receiver;
							this.loadedMethod = method;
							this.hostWidget = control;
						}
						eventController.setEvent(event, control, clrObject, method);
						break;
					}
				}
				current = current.getParent();
				if (current == null && currentParentLoader != null) {
					current = currentParentLoader.loadData;
					currentParentLoader = currentParentLoader.parentLoader;
				}
			}
			if (method == null) {
				LoggerManager.log(new XWTException("Event handler \"" + methodName + "\" is not found."));
			}
		}

		public void end() {
			if (parent == null || clr != parent.getClr()) {
				Method method = ObjectUtil.findDeclaredMethod(clr.getClass(), "initializeComponent");
				if (method == null) {
					method = ObjectUtil.findDeclaredMethod(clr.getClass(), "InitializeComponent");
				}
				if (method != null) {
					try {
						method.setAccessible(true);
						method.invoke(clr);
					} catch (Exception e) {
						LoggerManager.log(e);
					}
				}
			}
			// Try to invoke loaded event every time?
			if (loadedObject != null && loadedMethod != null && hostWidget != null) {
				Event event = new Event();
				event.doit = true;
				event.widget = hostWidget;
				try {
					loadedMethod.invoke(loadedObject, new Object[] { event });
				} catch (Exception e) {
					throw new XWTException("");
				}
				loadedObject = null;
				loadedMethod = null;
				hostWidget = null;
			}
		}

		public void addStyle(IStyle style) {
			if (styles == Collections.EMPTY_LIST) {
				styles = new ArrayList<IStyle>();
			}
			styles.add(style);
		}
	}

	private DataBindingTrack dataBindingTrack;

	/**
	 * @param context
	 */
	public ResourceLoader(IRenderingContext context, IXWTLoader loader) {
		this.context = context;
		this.loader = loader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IVisualElementLoader#createCLRElement(org.eclipse. e4.xwt.Element, org.eclipse.e4.xwt.ILoadData, org.eclipse.e4.xwt.IResourceDictionary)
	 */
	public Object createCLRElement(Element element, Map<String, Object> options) {
		try {
			Composite parent = (Composite) options.get(IXWTLoader.CONTAINER_PROPERTY);
			if (!loader.getTrackings().isEmpty()) {
				dataBindingTrack = new DataBindingTrack();
			}
			parentLoader = (ResourceLoader) options.get(RESOURCE_LOADER_PROPERTY);
			options.remove(RESOURCE_LOADER_PROPERTY);
			ResourceDictionary resourceDictionary = (ResourceDictionary) options.get(IXWTLoader.RESOURCE_DICTIONARY_PROPERTY);
			if (resourceDictionary != null) {
				Object styles = resourceDictionary.get(Core.DEFAULT_STYLES_KEY);
				if (styles != null) {
					loadData.setStyles((Collection<IStyle>) styles);
					resourceDictionary.remove(Core.DEFAULT_STYLES_KEY);
				}
			}

			Object control = doCreate(parent, element, null, options);
			// get databinding messages and print into console view
			if (dataBindingTrack != null) {
				String dataBindingMessage = dataBindingTrack.getDataBindMessage();// getDataBindMessage();
				org.eclipse.e4.xwt.ILogger log = loader.getLogger();
				log.addMessage(dataBindingMessage, Tracking.DATABINDING);
				log.printInfo(dataBindingMessage, Tracking.DATABINDING, loader.getTrackings());
			}
			if (control instanceof Composite) {
				((Composite) control).layout();
			}
			return control;
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	private Object doCreate(Object parent, Element element, Class<?> constraintType, Map<String, Object> options) throws Exception {
		int styles = -1;
		if (options.containsKey(IXWTLoader.INIT_STYLE_PROPERTY)) {
			styles = (Integer) options.get(IXWTLoader.INIT_STYLE_PROPERTY);
		}

		ResourceDictionary dico = (ResourceDictionary) options.get(IXWTLoader.RESOURCE_DICTIONARY_PROPERTY);
		Object dataContext = options.get(IXWTLoader.DATACONTEXT_PROPERTY);
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace)) {
			if (IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
				return null;
			}
			if (IConstants.XAML_X_TYPE.equalsIgnoreCase(name) && constraintType != null && constraintType == Class.class) {
				DocumentObject[] children = element.getChildren();
				if (children != null && children.length > 0) {
					if (children[0] instanceof Element) {
						Element type = (Element) children[0];
						IMetaclass metaclass = loader.getMetaclass(type.getName(), type.getNamespace());
						if (metaclass != null) {
							return metaclass.getType();
						}
					}
				} else {
					String content = element.getContent();
					return loader.convertFrom(Class.class, content);
				}
			}
			return null;
		}
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		if (constraintType != null && !(IBinding.class.isAssignableFrom(metaclass.getType())) && (!constraintType.isAssignableFrom(metaclass.getType()))) {
			if (!constraintType.isArray() || !constraintType.getComponentType().isAssignableFrom(metaclass.getType()))
				return null;
		}
		// ...
		trace("load: " + metaclass.getName());
		Object targetObject = null;
		Integer styleValue = getStyleValue(element, styles);

		if (parent == null || metaclass.getType() == Shell.class) {
			if (dataBindingTrack != null) {
				dataBindingTrack.addWidgetElement(element);
			}
			Shell shell = null;
			if (styleValue == null || styleValue == -1) {
				styleValue = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE;
			}
			Display display = Display.getDefault();
			shell = new Shell(display, styleValue);
			targetObject = shell;
			loadData.setCurrentWidget(shell);

			if (metaclass.getType() != Shell.class) {
				shell.setLayout(new FillLayout());
				return doCreate(targetObject, element, constraintType, options);
			} else if (dataContext != null) {
				setDataContext(metaclass, targetObject, dico, dataContext);
			}
			pushStack();

			// for Shell
			Attribute classAttribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_CLASS);
			if (classAttribute != null) {
				String className = classAttribute.getContent();
				loadShellCLR(className, shell);
			}

		} else {
			pushStack();

			//
			// load the content in case of UserControl
			//
			Class<?> type = metaclass.getType();
			URL file = type.getResource(type.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
			if (file != null && nameScoped != null) {
				if (parent instanceof Composite) {
					Object childDataContext = getDataContext(element, (Widget) parent);
					if (dataContext != null) {
						childDataContext = dataContext;
					}
					Map<String, Object> nestedOptions = new HashMap<String, Object>();
					nestedOptions.put(IXWTLoader.CONTAINER_PROPERTY, parent);
					if (styleValue != null) {
						nestedOptions.put(IXWTLoader.INIT_STYLE_PROPERTY, styleValue);
					}
					nestedOptions.put(IXWTLoader.DATACONTEXT_PROPERTY, childDataContext);
					nestedOptions.put(RESOURCE_LOADER_PROPERTY, this);
					targetObject = loader.loadWithOptions(file, nestedOptions);
					if (targetObject == null) {
						return null;
					}
				} else
					throw new XWTException("Cannot add user control: Parent is not a composite");
			} else {
				Object[] parameters = null;
				if (TableViewerColumn.class.isAssignableFrom(type)) {
					int columnIndex = getColumnIndex(element);
					parameters = (styleValue != null ? new Object[] { parent, styleValue, columnIndex } : new Object[] { parent, SWT.NONE, columnIndex });
				} else {
					parameters = (styleValue != null ? new Object[] { parent, styleValue } : new Object[] { parent });
				}

				// x:Class
				{
					Attribute classAttribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_CLASS);
					if (classAttribute != null) {
						String className = classAttribute.getContent();
						targetObject = loadCLR(className, parameters, metaclass.getType(), options);
					}
					if (targetObject == null) {
						targetObject = metaclass.newInstance(parameters);
					} else {
						metaclass = loader.getMetaclass(targetObject);
					}
				}

				if (targetObject == null) {
					return null;
				}
			}
		}
		Widget widget = getWidget(targetObject);
		if (widget != null) {
			loadData.setCurrentWidget(widget);
		}
		if (scopedObject == null && widget != null) {
			scopedObject = widget;
			nameScoped = new NameScope((parent == null ? null : loader.findNameContext((Widget) parent)));
			UserDataHelper.bindNameContext((Widget) widget, nameScoped);
		}

		// set first data context and resource dictionary
		setDataContext(metaclass, targetObject, dico, dataContext);

		applyStyles(element, targetObject);

		if (dataBindingTrack != null) {
			dataBindingTrack.tracking(targetObject, element, dataContext);
		}

		// set parent relationship and viewer
		if (targetObject instanceof Widget) {
			if (parent != null) {
				((Widget) targetObject).setData(IUserDataConstants.XWT_PARENT_KEY, parent);
			}
		} else if (JFacesHelper.isViewer(targetObject)) {
			Control control = JFacesHelper.getControl(targetObject);
			control.setData(IUserDataConstants.XWT_PARENT_KEY, parent);
			control.setData(IUserDataConstants.XWT_VIEWER_KEY, targetObject);
		} else if (targetObject instanceof TableItemProperty.Cell) {
			((TableItemProperty.Cell) targetObject).setParent((TableItem) parent);
		}

		for (String key : options.keySet()) {
			if (IXWTLoader.CONTAINER_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.INIT_STYLE_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.DATACONTEXT_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.RESOURCE_DICTIONARY_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.CLASS_PROPERTY.equalsIgnoreCase(key)) {
				continue;
			}
			IProperty property = metaclass.findProperty(key);
			if (property == null) {
				throw new XWTException("Property " + key + " not found.");
			}
			property.setValue(targetObject, options.get(key));
		}

		List<String> delayedAttributes = new ArrayList<String>();
		init(metaclass, targetObject, element, delayedAttributes);
		if (targetObject instanceof Style && element.getChildren().length > 0) {
			Collection<Setter> setters = new ArrayList<Setter>();
			for (DocumentObject doc : element.getChildren()) {
				Object child = doCreate(targetObject, (Element) doc, null, Collections.EMPTY_MAP);
				if (!(child instanceof Setter)) {
					throw new XWTException("Setter is expected in Style.");
				}
				setters.add((Setter) child);
			}
			((Style) targetObject).setSetters(setters);
		} else if (targetObject instanceof ControlEditor) {
			for (DocumentObject doc : element.getChildren()) {
				Object editor = doCreate(parent, (Element) doc, null, Collections.EMPTY_MAP);
				if (editor != null && editor instanceof Control) {
					((ControlEditor) targetObject).setEditor((Control) editor);
					((Control) editor).setData(PropertiesConstants.DATA_CONTROLEDITOR_OF_CONTROL, targetObject);
				}
			}
		} else if (targetObject instanceof IDataProvider) {
			for (DocumentObject doc : element.getChildren()) {
				if (IConstants.XWT_X_NAMESPACE.equals(doc.getNamespace())) {
					String content = doc.getContent();
					if (content != null) {
						((IDataProvider) targetObject).setProperty(doc.getName(), content);
					}
				}
			}
		} else {
			for (DocumentObject doc : element.getChildren()) {
				doCreate(targetObject, (Element) doc, null, Collections.EMPTY_MAP);
			}
		}

		for (String delayed : delayedAttributes) {
			initAttribute(metaclass, targetObject, element, null, delayed);
		}
		popStack();
		return targetObject;
	}

	protected Widget getWidget(Object target) {
		if (JFacesHelper.isViewer(target)) {
			return JFacesHelper.getControl(target);
		} else if (target instanceof Widget) {
			return (Widget) target;
		}
		return null;
	}

	private void setDataContext(IMetaclass metaclass, Object targetObject, ResourceDictionary dico, Object dataContext) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Widget widget = null;
		IMetaclass widgetMetaclass = metaclass;
		if (JFacesHelper.isViewer(targetObject)) {
			widget = JFacesHelper.getControl(targetObject);
			widgetMetaclass = loader.getMetaclass(widget.getClass());
		} else if (targetObject instanceof Widget) {
			widget = (Widget) targetObject;
		} else {
			widget = loadData.getCurrentWidget();
		}
		if (widget != null) {
			if (targetObject instanceof BindingMetaclass.Binding) {
				((BindingMetaclass.Binding) targetObject).setControl(widget);
			}
			if (dico != null) {
				widget.setData(IUserDataConstants.XWT_RESOURCES_KEY, dico);
			}
			if (dataContext != null) {
				IProperty property = widgetMetaclass.findProperty(IConstants.XAML_DATACONTEXT);
				if (property != null) {
					property.setValue(widget, dataContext);
				} else {
					throw new XWTException("DataContext is missing in " + widgetMetaclass.getType().getName());
				}
			}
		}
	}

	private void applyStyles(Element element, Object targetObject) throws Exception {
		if (targetObject instanceof Widget) {
			Widget widget = (Widget) targetObject;
			ResourceDictionary dico = (ResourceDictionary) widget.getData(IUserDataConstants.XWT_RESOURCES_KEY);
			Attribute attribute = element.getAttribute(IConstants.XAML_RESOURCES);
			if (attribute == null) {
				attribute = element.getAttribute(IConstants.XWT_NAMESPACE, IConstants.XAML_RESOURCES);
			}
			if (attribute != null) {
				if (attribute.getChildren().length > 0) {
					if (dico == null) {
						dico = new ResourceDictionary();
						widget.setData(IUserDataConstants.XWT_RESOURCES_KEY, dico);
					}

					for (DocumentObject doc : attribute.getChildren()) {
						Element elem = (Element) doc;
						Object doCreate = doCreate(widget, elem, null, EMPTY_MAP);
						Attribute keyAttribute = elem.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_KEY);
						if (keyAttribute == null) {
							keyAttribute = elem.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_TYPE);
						}
						if (keyAttribute != null) {
							dico.put(keyAttribute.getContent(), doCreate);
						}
						if (doCreate instanceof IStyle) {
							IStyle style = (IStyle) doCreate;
							loadData.addStyle(style);
						}
					}
				}
			}

			// apply the styles defined in parent's resources via TargetType
			Widget current = widget;
			while (current != null) {
				dico = (ResourceDictionary) current.getData(IUserDataConstants.XWT_RESOURCES_KEY);
				if (dico != null) {
					for (Object value : dico.values()) {
						if (value instanceof Style) {
							Style style = (Style) value;
							Class<?> targetType = style.getTargetType();
							if (targetType != null && targetType.isInstance(widget)) {
								style.apply(targetObject);
							}
						}
					}
				}
				current = UserDataHelper.getParent(current);
			}
		}

		for (IStyle style : loadData.getStyles()) {
			style.applyStyle(targetObject);
		}
	}

	private int getColumnIndex(Element columnElement) {
		String name = columnElement.getName();
		String namespace = columnElement.getNamespace();
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		int index = -1;
		Class<?> type = metaclass.getType();
		if (TableViewerColumn.class.isAssignableFrom(type)) {
			DocumentObject parent = columnElement.getParent();
			List<DocumentObject> children = DocumentObjectSorter.sortWithAttr(parent.getChildren(), "Index");
			index = children.indexOf(columnElement);
		}

		return index;
	}

	/**
	 * @param tableItem
	 */
	private void installTableEditors(TableItem tableItem) {
		Table table = tableItem.getParent();
		TableColumn[] columns = table.getColumns();
		if (columns == null || columns.length == 0) {
			return;
		}
		for (TableColumn tableColumn : columns) {
			Object data = tableColumn.getData(PropertiesConstants.DATA_DEFINED_EDITOR);
			if (data == null || !(data instanceof Element)) {
				continue;
			}
			int column = table.indexOf(tableColumn);
			Element editor = (Element) data;
			try {
				TableEditor tableEditor = (TableEditor) doCreate(table, editor, null, EMPTY_MAP);
				if (tableEditor != null) {
					tableEditor.setColumn(column);
					tableEditor.setItem(tableItem);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private Object getDataContext(Element element, Widget swtObject) {
		// x:DataContext
		try {
			{
				Attribute dataContextAttribute = element.getAttribute(IConstants.XWT_NAMESPACE, "DataContext");
				if (dataContextAttribute != null) {
					Widget composite = (Widget) swtObject;
					DocumentObject documentObject = dataContextAttribute.getChildren()[0];
					if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
						String key = documentObject.getContent();
						return new StaticResourceBinding(composite, key);
					} else if (IConstants.XAML_BINDING.equals(documentObject.getName())) {
						return doCreate(swtObject, (Element) documentObject, null, EMPTY_MAP);
					} else {
						LoggerManager.log(new UnsupportedOperationException(documentObject.getName()));
					}
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}

		return null;
	}

	private void pushStack() {
		loadData = new LoadingData(loadData);
	}

	private void popStack() {
		LoadingData previous = loadData;
		loadData = previous.getParent();

		previous.end();
	}

	private Integer getStyleValue(Element element, int styles) {
		Attribute attribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_STYLE);
		if (attribute == null) {
			if (styles != -1) {
				return styles;
			}
			return null;
		}
		if (styles == -1) {
			return (Integer) loader.findConvertor(String.class, Integer.class).convert(attribute.getContent());
		}
		return styles | (Integer) loader.findConvertor(String.class, Integer.class).convert(attribute.getContent());
	}

	private void init(IMetaclass metaclass, Object targetObject, Element element, List<String> delayedAttributes) throws Exception {
		// editors for TableItem,
		if (targetObject instanceof TableItem) {
			installTableEditors((TableItem) targetObject);
		}

		// x:DataContext
		{
			Attribute dataContextAttribute = element.getAttribute(IConstants.XAML_DATACONTEXT);
			if (dataContextAttribute != null) {
				IProperty property = metaclass.findProperty(IConstants.XAML_DATACONTEXT);
				Widget composite = (Widget) targetObject;
				DocumentObject documentObject = dataContextAttribute.getChildren()[0];
				if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
					String key = documentObject.getContent();
					property.setValue(composite, new StaticResourceBinding(composite, key));
				} else if (IConstants.XAML_BINDING.equals(documentObject.getName())) {
					Object object = doCreate(targetObject, (Element) documentObject, null, EMPTY_MAP);
					property.setValue(composite, object);
				} else {
					LoggerManager.log(new UnsupportedOperationException(documentObject.getName()));
				}
			}
		}

		HashSet<String> done = new HashSet<String>();

		Attribute nameAttr = element.getAttribute(IConstants.XAML_X_NAME);
		if (nameAttr == null) {
			nameAttr = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_NAME);
		}
		if (nameAttr != null && getWidget(targetObject) != null) {
			nameScoped.addObject(nameAttr.getContent(), targetObject);
			done.add(IConstants.XAML_X_NAME);
		}

		for (String attrName : element.attributeNames()) {
			if (IConstants.XWT_X_NAMESPACE.equals(element.getAttribute(attrName).getNamespace())) {
				continue;
			} else if (delayedAttributes != null && isDelayedProperty(attrName.toLowerCase(), metaclass.getType()))
				delayedAttributes.add(attrName);
			else {
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, null, attrName);
					done.add(attrName);
				}
			}
		}

		for (String namespace : element.attributeNamespaces()) {
			if (IConstants.XWT_X_NAMESPACE.equals(namespace)) {
				for (String attrName : element.attributeNames(namespace)) {
					if ("class".equalsIgnoreCase(attrName) || IConstants.XAML_STYLE.equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName)) {
						nameScoped.addObject(element.getAttribute(namespace, attrName).getContent(), targetObject);
						done.add(attrName);
					} else if (IConstants.XAML_DATACONTEXT.equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if (IConstants.XAML_X_ARRAY.equalsIgnoreCase(attrName)) {
						IProperty property = metaclass.findProperty(attrName);
						Class<?> type = property.getType();
						Object value = getArrayProperty(type, targetObject, element, attrName);
						if (value != null) {
							property.setValue(targetObject, value);
						}
					} else if (IConstants.XAML_RESOURCES.equalsIgnoreCase(attrName)) {
						continue;
					} else {
						if (!done.contains(attrName)) {
							initAttribute(metaclass, targetObject, element, namespace, attrName);
							done.add(attrName);
						}
					}
				}
				continue;
			}

			for (String attrName : element.attributeNames(namespace)) {
				if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName) && (targetObject instanceof Widget)) {
					continue;
				}
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, namespace, attrName);
					done.add(attrName);
				}
			}
		}
		for (String attrName : element.attributeNames()) {
			if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName) && getWidget(targetObject) != null) {
				continue;
			}
			if (!done.contains(attrName) && !delayedAttributes.contains(attrName)) {
				initAttribute(metaclass, targetObject, element, null, attrName);
				done.add(attrName);
			}
		}

		//
		// handle foreigner namespace
		//
		for (String namespace : element.attributeNamespaces()) {
			if (XWT.isXWTNamespace(namespace)) {
				continue;
			}
			INamespaceHandler namespaceHandler = loader.getNamespaceHandler(namespace);
			if (namespaceHandler != null) {
				for (String attrName : element.attributeNames(namespace)) {
					Attribute attribute = element.getAttribute(namespace, attrName);
					namespaceHandler.handleAttribute(loadData.getCurrentWidget(), targetObject, attrName, attribute.getContent());
				}
			}
		}
	}

	private Object getArrayProperty(Class<?> type, Object swtObject, DocumentObject element, String attrName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		if (!type.isArray()) {
			throw new XWTException("Type mismatch: property " + attrName + " isn't an array.");
		}

		Class<?> arrayType = type.getComponentType();
		if (arrayType != null) {
			List<Object> list = new ArrayList<Object>();
			for (DocumentObject childModel : element.getChildren()) {
				if (!(childModel instanceof Element)) {
					continue;
				}
				Object child = createInstance(swtObject, (Element) childModel);
				list.add(child);
			}
			Object[] array = (Object[]) Array.newInstance(arrayType, list.size());
			list.toArray(array);

			for (int i = 0; i < array.length; i++) {
				if (array[i] instanceof IIndexedElement) {
					((IIndexedElement) array[i]).setIndex(swtObject, i);
				}
			}
			return array;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Object getCollectionProperty(Class<?> type, Object swtObject, DocumentObject element, String attrName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Collection<Object> collector = null;
		if (type.isInterface()) {
			collector = new ArrayList<Object>();
		} else {
			if (Modifier.isAbstract(type.getModifiers())) {
				LoggerManager.log(new XWTException("Collection " + type.getSimpleName() + " is abstract type"));
			}
			try {
				collector = (Collection) type.newInstance();
			} catch (InstantiationException e) {
				LoggerManager.log(new XWTException(e));
			}
		}

		for (DocumentObject childModel : element.getChildren()) {
			if (!(childModel instanceof Element)) {
				continue;
			}
			Object child = createInstance(swtObject, (Element) childModel);
			collector.add(child);
			if (child instanceof IIndexedElement) {
				((IIndexedElement) child).setIndex(swtObject, collector.size() - 1);
			}
		}
		return collector;
	}

	private String findNamespace(DocumentObject context, String prefix) {
		while (context != null && !(context instanceof Element)) {
			context = context.getParent();
		}
		if (context == null) {
			return null;
		}
		Element element = (Element) context;

		if (prefix != null) {
			prefix = (prefix.length() == 0 ? null : prefix);
		}

		String namespace = element.getXmlns(prefix);
		if (namespace != null) {
			return namespace;
		}
		DocumentObject parent = element.getParent();
		return findNamespace(parent, prefix);
	}

	private Object createInstance(Object swtObject, Element element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace) && IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		try {
			Class<?> type = NamespaceHelper.loadCLRClass(context.getLoadingContext(), name, namespace);
			IMetaclass metaclass = loader.getMetaclass(name, namespace);
			if (type == null) {
				if (metaclass != null)
					type = metaclass.getType();
			}
			// type = expected type;
			// Need to support the
			String content = element.getContent();
			Object instance = null;
			if (content == null) {
				instance = metaclass.newInstance(new Object[] { swtObject });
				if (instance instanceof TableEditor) {
					// TODO should be moved into IMetaclass
					TableEditor tableEditor = (TableEditor) instance;
					if (swtObject instanceof TableItem) {
						TableItem item = (TableItem) swtObject;
						tableEditor.setItem(item);
						for (DocumentObject doc : element.getChildren()) {
							Control control = (Control) doCreate(((TableItem) swtObject).getParent(), (Element) doc, null, EMPTY_MAP);
							tableEditor.setEditor(control);
							int column = getColumnValue(element);
							TableEditorHelper.initEditor(item, control, column);
						}
					}
				}
			} else {
				Constructor<?> constructor = type.getConstructor(type);
				if (constructor != null) {
					instance = constructor.newInstance(loader.convertFrom(type, content));
				} else {
					LoggerManager.log(new XWTException("Constructor \"" + name + "(" + type.getSimpleName() + ")\" is not found"));
				}
			}

			List<String> delayedAttributes = new ArrayList<String>();
			init(metaclass, instance, element, delayedAttributes);
			for (String delayed : delayedAttributes) {
				initAttribute(metaclass, instance, element, null, delayed);
			}
			return instance;
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	static protected int getColumnValue(Element context) {
		Attribute attribute = context.getAttribute(COLUMN);
		if (attribute != null) {
			String content = attribute.getContent();
			if (content != null) {
				return Integer.parseInt(content);
			}
		}
		return 0;
	}

	private void loadShellCLR(String className, Shell shell) {
		Class<?> type = ClassLoaderUtil.loadClass(context.getLoadingContext(), className);
		try {
			Object instance = type.newInstance();
			loadData.setClr(instance);
			UserDataHelper.setCLR(shell, instance);
		} catch (Exception e) {
			LoggerManager.log(e);
		}
	}

	private Object loadCLR(String className, Object[] parameters, Class<?> currentTagType, Map<String, Object> options) {
		Class<?> type = ClassLoaderUtil.loadClass(context.getLoadingContext(), className);
		try {
			Object clr = options.get(XWTLoader.CLASS_PROPERTY);
			if (clr != null && type != null && type.isInstance(clr)) {
				loadData.setClr(clr);
				if (clr instanceof Widget) {
					UserDataHelper.setCLR((Widget) clr, clr);
				}
			} else if (currentTagType != null && currentTagType.isAssignableFrom(type)) {
				IMetaclass metaclass = loader.getMetaclass(type);
				Object instance = metaclass.newInstance(parameters);
				loadData.setClr(instance);
				// use x:Class's instance
				if (instance instanceof Widget) {
					UserDataHelper.setCLR((Widget) instance, instance);
				}
				return instance;
			} else {
				Object instance = type.newInstance();
				loadData.setClr(instance);
				if (instance instanceof Widget) {
					UserDataHelper.setCLR((Widget) instance, instance);
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	private void trace(String message) {
		// System.out.println(message);
	}

	private void initAttribute(IMetaclass metaclass, Object targetObject, Element element, String namespace, String attrName) throws Exception {
		trace("Set attribute: " + metaclass.getName() + "." + attrName);
		if (attrName.indexOf('.') != -1) {
			String[] segments = attrName.split("\\.");
			IMetaclass currentMetaclass = metaclass;
			Object target = targetObject;
			for (int i = 0; i < segments.length - 1; i++) {
				IProperty property = currentMetaclass.findProperty(segments[i]);
				if (property != null) {
					target = property.getValue(target);
					if (target == null) {
						LoggerManager.log(new XWTException("Property \"" + segments[i] + "\" is null."));
					}
					currentMetaclass = loader.getMetaclass(target);
				} else {
					LoggerManager.log(new XWTException("Property \"" + segments[i] + "\" not found in " + element.getName() + "."));
				}
			}
			initSegmentAttribute(currentMetaclass, segments[segments.length - 1], target, element, namespace, attrName);
			return;
		}
		initSegmentAttribute(metaclass, attrName, targetObject, element, namespace, attrName);
	}

	private void addCommandExecuteListener(String commandName, final Widget targetButton) {
		final ICommand commandObj = loader.getCommand(commandName);
		if (commandObj != null) {
			targetButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					commandObj.execute(targetButton);
				}
			});
		}
	}

	private void initSegmentAttribute(IMetaclass metaclass, String propertyName, Object target, Element element, String namespace, String attrName) throws Exception {
		Attribute attribute = namespace == null ? element.getAttribute(attrName) : element.getAttribute(namespace, attrName);
		if (attribute == null) {
			attribute = element.getAttribute(attrName);
		}
		IProperty property = metaclass.findProperty(propertyName);
		if (propertyName.equals(IConstants.XAML_DATACONTEXT)) {
			property = null;
		}
		if (IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName) && ICommand.class.isAssignableFrom(property.getType()) && (target instanceof Widget)) {
			addCommandExecuteListener(attribute.getContent(), (Widget) target);
		}
		if (property == null) {
			IEvent event = metaclass.findEvent(attrName);
			if (event == null) {
				trace("event is null: " + attrName);
				return;
			}
			// add events for controls and items.
			if (!(target instanceof Widget)) {
				trace("property is null: " + attrName);
				return;
			}
			loadData.updateEvent(context, (Widget) target, event, attribute, propertyName);
			return;
		}
		try {
			String contentValue = attribute.getContent();
			if ("MenuItem".equalsIgnoreCase(element.getName()) && "Text".equalsIgnoreCase(attrName)) {
				Attribute attributeAccelerator = element.getAttribute("Accelerator");
				if (attributeAccelerator != null) {

					contentValue = contentValue + '\t' + getContentValue(attributeAccelerator.getContent());
				}
			}

			if (contentValue != null && "Accelerator".equalsIgnoreCase(attrName)) {
				contentValue = XWTMaps.getCombAccelerator(contentValue);
				if (contentValue.contains("'")) {
					contentValue = removeSubString(contentValue, "'");
				}
			}
			if (contentValue != null && (Image.class.isAssignableFrom(property.getType()))) {
				contentValue = getImagePath(attribute, contentValue);
			}
			if (contentValue != null && (URL.class.isAssignableFrom(property.getType()))) {
				contentValue = getSourceURL(contentValue);
			}
			Object value = null;
			DocumentObject[] children = attribute.getChildren();
			if (contentValue == null) {
				Class<?> type = property.getType();
				if (Collection.class.isAssignableFrom(type)) {
					value = getCollectionProperty(type, target, attribute, attrName);
				} else {
					if (TableViewerColumn.class.isAssignableFrom(type) && attrName.equalsIgnoreCase("columns")) {
						children = DocumentObjectSorter.sortWithAttr(children, "Index").toArray(new DocumentObject[children.length]);
					}

					for (DocumentObject child : children) {
						String name = child.getName();
						String ns = child.getNamespace();
						if (name.equalsIgnoreCase(IConstants.XAML_X_STATIC) && ns.equals(IConstants.XWT_X_NAMESPACE)) {
							value = getStaticValue(child);
						} else if (name.equalsIgnoreCase(IConstants.XAML_STATICRESOURCES) && ns.equals(IConstants.XWT_NAMESPACE)) {
							String key = child.getContent();
							value = new StaticResourceBinding(loadData.getCurrentWidget(), key);
						} else if ((IConstants.XWT_X_NAMESPACE.equals(ns) && IConstants.XAML_X_ARRAY.equalsIgnoreCase(name))) {
							value = getArrayProperty(property.getType(), target, child, name);
						} else if (property.getType().isArray()) {
							value = getArrayProperty(property.getType(), target, attribute, name);
							break;
						} else if (isAssignableFrom(element, TableColumn.class) && isAssignableFrom(child, TableEditor.class)) {
							value = child;
						} else if (TableViewerColumn.class.isAssignableFrom(property.getType()) && attribute.getContent() != null) {
							value = attribute.getContent();
						} else {
							value = doCreate(target, (Element) child, type, EMPTY_MAP);
							if (value instanceof Binding) {
								((Binding) value).setType(attrName);
							}
						}
					}
				}
			}
			if (contentValue != null && value == null && !IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName)) {
				if (property.getType().isInstance(Class.class)) {
					int index = contentValue.lastIndexOf(':');
					if (index != -1) {
						String prefix = contentValue.substring(0, index);
						contentValue = findNamespace(attribute, prefix) + contentValue.substring(index);
					}
				}
				value = loader.convertFrom(property.getType(), contentValue);
			}
			if (value != null) {
				Class<?> propertyType = property.getType();
				if (!propertyType.isAssignableFrom(value.getClass()) || value instanceof IBinding) {
					Object orginalValue = value;
					IConverter converter = loader.findConvertor(value.getClass(), propertyType);
					if (converter != null) {
						value = converter.convert(value);
						if (value != null && orginalValue instanceof IBinding && !propertyType.isAssignableFrom(value.getClass())) {
							converter = loader.findConvertor(value.getClass(), propertyType);
							if (converter != null) {
								value = converter.convert(value);
							} else {
								LoggerManager.log(new XWTException("Convertor " + value.getClass().getSimpleName() + "->" + propertyType.getSimpleName() + " is not found"));
							}
						}
					} else {
						LoggerManager.log(new XWTException("Convertor " + value.getClass().getSimpleName() + "->" + propertyType.getSimpleName() + " is not found"));
					}
				}
				property.setValue(target, value);
			} else {
				if (value == null) {
					value = property.getValue(target);
				}
				if (value != null) {
					// create children.
					for (DocumentObject child : children) {
						String name = child.getName();
						String ns = child.getNamespace();
						if (!IConstants.XWT_X_NAMESPACE.equals(ns) || !IConstants.XAML_X_ARRAY.equalsIgnoreCase(name)) {
							Class<?> type = property.getType();
							if (!Collection.class.isAssignableFrom(type)) {
								doCreate(value, (Element) child, null, EMPTY_MAP);
							}
						}
					}
				}
			}

			if (attribute.attributeNames(IConstants.XWT_NAMESPACE).length > 0) {
				IMetaclass propertyMetaclass = loader.getMetaclass(property.getType());
				if (value == null) {
					value = property.getValue(target);
				}
				if (value != null) {
					List<String> delayedAttributes = new ArrayList<String>();
					init(propertyMetaclass, value, attribute, delayedAttributes);
					for (String delayed : delayedAttributes) {
						initAttribute(metaclass, target, element, null, delayed);
					}
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
	}

	/**
	 * @param contentValue
	 * @return
	 */
	private String getSourceURL(String contentValue) {
		URL url = null;
		try {
			url = new URL(contentValue);
		} catch (MalformedURLException e) {
			if (!contentValue.startsWith("/")) {
				contentValue = "/" + contentValue;
			}
			ILoadingContext loadingContext = context.getLoadingContext();
			URL resource = loadingContext.getClassLoader().getResource(contentValue);
			if (resource == null) {
				try {
					resource = new URL(context.getResourcePath() + contentValue);
				} catch (MalformedURLException e1) {
				}
			}
			return resource.toString();
		}
		if (url != null) {
			return url.toString();
		}
		return contentValue;
	}

	private Class<?> getJavaType(DocumentObject element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace) && IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		if (metaclass == null) {
			return null;
		}
		return metaclass.getType();
	}

	private boolean isAssignableFrom(DocumentObject element, Class<?> type) {
		Class<?> targetType = getJavaType(element);
		if (targetType == null) {
			return false;
		}
		return targetType.isAssignableFrom(type);
	}

	private Object getStaticValue(DocumentObject child) {
		DocumentObject[] children = child.getChildren();
		if (children.length == 1) {
			Element element = (Element) children[0];
			if (element != null) {
				return ClassLoaderUtil.loadStaticMember(context.getLoadingContext(), element);
			}
		}
		return null;
	}

	private String getImagePath(Attribute attribute, String contentValue) {
		try {
			File file = new File(contentValue);
			if (file.exists()) {
				return file.toURI().toURL().toString();
			}
			if (!contentValue.startsWith("/")) {
				contentValue = "/" + contentValue;
			}
			ILoadingContext loadingContext = context.getLoadingContext();
			URL resource = loadingContext.getClassLoader().getResource(contentValue);
			if (resource == null) {
				URL resourcePath = context.getResourcePath();
				String fPath = resourcePath.toString();
				String absolutePath = PathHelper.getAbsolutePath(fPath, contentValue);
				if ((file = new File(absolutePath)).exists()) {
					return file.toURI().toURL().toString();
				}
				resource = new URL(absolutePath);
			}
			return resource.toString();
		} catch (MalformedURLException e) {
			return contentValue;
		}
	}

	private String removeSubString(String str, String subString) {
		StringBuffer stringBuffer = new StringBuffer();
		int lenOfsource = str.length();
		int i;
		int posStart;
		for (posStart = 0; (i = str.indexOf(subString, posStart)) >= 0; posStart = i + subString.length()) {
			stringBuffer.append(str.substring(posStart, i));
		}
		if (posStart < lenOfsource) {
			stringBuffer.append(str.substring(posStart));
		}
		return stringBuffer.toString();
	}

	private String getContentValue(String text) {
		StringBuffer stringBuffer = new StringBuffer();
		String subString = "SWT.";
		String str = XWTMaps.getCombAccelerator(text);

		if (str.contains(subString)) {
			str = removeSubString(str, subString);
		}
		if (str.contains("'")) {
			str = removeSubString(str, "'");
		}
		if (str.contains(" ")) {
			str = removeSubString(str, " ");
		}
		if (str.contains("|")) {
			str = str.replace('|', '+');
		}
		stringBuffer.append(str);
		return stringBuffer.toString();

	}

	static protected boolean isDelayedProperty(String attr, Class<?> type) {
		Collection<Class<?>> types = DELAYED_ATTRIBUTES.get(attr);
		if (types == null) {
			return false;
		}
		if (types.contains(type)) {
			return true;
		}
		for (Class<?> class1 : types) {
			if (class1.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	static {
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Viewer.class);
			DELAYED_ATTRIBUTES.put("input", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Sash.class);
			types.add(SashForm.class);
			DELAYED_ATTRIBUTES.put("weights", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Combo.class);
			types.add(CCombo.class);
			DELAYED_ATTRIBUTES.put("text", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(Browser.class);
			DELAYED_ATTRIBUTES.put("url", types);
		}
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(TableEditor.class);
			DELAYED_ATTRIBUTES.put("dynamic", types);
		}
	}

}
