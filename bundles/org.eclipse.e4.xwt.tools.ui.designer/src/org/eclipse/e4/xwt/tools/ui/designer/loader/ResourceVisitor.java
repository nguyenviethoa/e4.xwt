/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tools.ui.designer.loader;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IIndexedElement;
import org.eclipse.e4.xwt.ILoadedAction;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.INamespaceHandler;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.UI;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.XWTMaps;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.core.IDynamicBinding;
import org.eclipse.e4.xwt.core.IEventHandler;
import org.eclipse.e4.xwt.core.Setter;
import org.eclipse.e4.xwt.core.Style;
import org.eclipse.e4.xwt.core.TriggerBase;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.internal.core.Core;
import org.eclipse.e4.xwt.internal.core.ScopeKeeper;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.NamespaceHelper;
import org.eclipse.e4.xwt.internal.utils.ObjectUtil;
import org.eclipse.e4.xwt.internal.utils.TableEditorHelper;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.StaticResourceBinding;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
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
 * @author jliu jin.liu@soyatec.com
 */
public class ResourceVisitor {
	public static final String ELEMENT_KEY = "XWTDesigner.Model";
	public static final String UI_FILE_KEY = "XWTDesigner.UIFile";
	// static public final String DEFAULT_STYLES_KEY = "XWT.DefaultStyles";
	static Map<String, Object> EMPTY_MAP = Collections.emptyMap();

	static final String RESOURCE_LOADER_PROPERTY = "XWT.ResourceLoader";

	private static final HashMap<String, Collection<Class<?>>> DELAYED_ATTRIBUTES = new HashMap<String, Collection<Class<?>>>();
	private static final String COLUMN = "Column";

	protected ResourceVisitor parentLoader;
	protected XWTVisualLoader loader;

	protected Object scopedObject;
	protected ScopeKeeper nameScoped;
	protected LoadingData loadData = new LoadingData();
	private DataBindingTrack dataBindingTrack;

	class LoadingData {
		protected LoadingData parent;
		protected Object clr;
		protected Collection<IStyle> styles = Collections.emptyList();
		private Object loadedObject = null;
		private Method loadedMethod = null;
		private Widget hostCLRWidget = null;
		private Object currentWidget = null;
		private Object host = null;

		public Object getHost() {
			return host;
		}

		public Object getCurrentWidget() {
			return currentWidget;
		}

		public void setCurrentWidget(Object currentWidget) {
			this.currentWidget = currentWidget;
		}

		public LoadingData getParent() {
			return parent;
		}

		public LoadingData() {
		}

		public LoadingData(LoadingData loadingData, Object host) {
			this.loadedObject = loadingData.loadedObject;
			this.loadedMethod = loadingData.loadedMethod;
			this.hostCLRWidget = loadingData.hostCLRWidget;
			this.parent = loadingData;
			this.styles = loadingData.styles;
			this.clr = loadingData.clr;
			this.currentWidget = loadingData.currentWidget;
			this.host = host;
		}

		public void inject(Object targetObject, String name) {
			doInject(targetObject, name, null);
		}

		protected void doInject(Object targetObject, String name, Object previousClr) {
			Class<?> filedType = targetObject.getClass();
			if (clr != null && (previousClr != clr || previousClr == null)) {
				for (Field field : clr.getClass().getDeclaredFields()) {
					UI annotation = field.getAnnotation(UI.class);
					if (annotation != null) {
						if (!field.getType().isAssignableFrom(filedType)) {
							continue;
						}
						String annotationValue = annotation.value();
						if (annotationValue == null || annotationValue.length() == 0) {
							if (field.getName().equals(name)) {
								field.setAccessible(true);
								try {
									field.set(clr, targetObject);
									break;
								} catch (Exception e) {
								}
							}
						}
						else if (annotationValue.equals(name)) {
							field.setAccessible(true);
							try {
								field.set(clr, targetObject);
								break;
							} catch (Exception e) {
							}							
						}
					}
				}
			}
			if (parent != null) {
				parent.doInject(targetObject, name, clr);
			}
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

		public void updateEvent(Widget control, IEvent event, String handler) {
			IEventHandler eventController = UserData.updateEventController(control);
			Method method = null;
			Object clrObject = null;
			LoadingData current = this;
			ResourceVisitor currentParentLoader = parentLoader;
			while (current != null) {
				Object receiver = current.getClr();
				if (receiver != null) {
					Class<?> clazz = receiver.getClass();
					method = ObjectUtil.findMethod(clazz, handler, Object.class, Event.class);
					if (method == null) {
						method = ObjectUtil.findMethod(clazz, handler, Event.class);
					}
					if (method == null) {
						// Load again.
						clazz = ClassLoaderUtil.loadClass(loader.getLoadingContext(), clazz.getName());
						method = ObjectUtil.findMethod(clazz, handler, Object.class, Event.class);
						if (method == null) {
							method = ObjectUtil.findMethod(clazz, handler, Event.class);
						}
					}
					if (method != null) {
						clrObject = receiver;
						if (event.getName().equalsIgnoreCase(IEventConstants.XWT_LOADED)) {
							method.setAccessible(true);
							this.loadedObject = receiver;
							this.loadedMethod = method;
							this.hostCLRWidget = control;
						}
						eventController.setEvent(event, control, clrObject, control, method);
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
				throw new XWTException("Event handler \"" + handler + "\" is not found.");
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
			if (loadedObject != null && loadedMethod != null && hostCLRWidget != null) {
				Event event = new Event();
				event.doit = true;
				event.widget = hostCLRWidget;
				try {
					loadedMethod.invoke(loadedObject, new Object[] { event });
				} catch (Exception e) {
					throw new XWTException("");
				}
				loadedObject = null;
				loadedMethod = null;
				hostCLRWidget = null;
			}
		}

		public void addStyle(IStyle style) {
			if (styles == Collections.EMPTY_LIST) {
				styles = new ArrayList<IStyle>();
			}
			styles.add(style);
		}
	}

	public ResourceVisitor(XWTVisualLoader loader) {
		this.loader = loader;
	}

	public Object createCLRElement(XamlElement element, Map<String, Object> options) {
		try {
			Composite parent = (Composite) options.get(IXWTLoader.CONTAINER_PROPERTY);
			if (!loader.getTrackings().isEmpty()) {
				dataBindingTrack = new DataBindingTrack();
			}
			Object object = options.get(RESOURCE_LOADER_PROPERTY);
			if (object instanceof ResourceVisitor) {
				parentLoader = (ResourceVisitor) object;
			}
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
			ILoadedAction loadedAction = (ILoadedAction) options.get(IXWTLoader.LOADED_ACTION);
			if (loadedAction != null) {
				loadedAction.onLoaded(control);
			}
			return control;
		} catch (Exception e) {
			throw new XWTException(e);
		}
	}

	public Object doCreate(Object parent, XamlElement element, Class<?> constraintType, Map<String, Object> options) throws Exception {
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
				XamlNode[] children = element.getChildNodes().toArray(new XamlNode[0]);
				if (children != null && children.length > 0) {
					if (children[0] instanceof XamlElement) {
						XamlElement type = (XamlElement) children[0];
						IMetaclass metaclass = loader.getMetaclass(type.getName(), type.getNamespace());
						if (metaclass != null) {
							return metaclass.getType();
						}
					}
				} else {
					String content = element.getValue();
					return loader.convertFrom(Class.class, content);
				}
			}
			return null;
		}
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		if (metaclass == null) {
			return null;
		}
		if (constraintType != null && !(IBinding.class.isAssignableFrom(metaclass.getType())) && (!constraintType.isAssignableFrom(metaclass.getType()))) {
			if (!constraintType.isArray() || !constraintType.getComponentType().isAssignableFrom(metaclass.getType()))
				return null;
		}
		Object targetObject = null;
		Integer styleValue = getStyleValue(element, styles);

		if (parent == null || metaclass.getType() == Shell.class) {
			if (dataBindingTrack != null) {
				dataBindingTrack.addWidgetElement(element);
			}
			Shell shell = null;
			if (styleValue == null || styleValue == -1) {
				styleValue = SWT.SHELL_TRIM;
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
			pushStack(parent);

			// for Shell
			XamlAttribute classAttribute = element.getAttribute(IConstants.XAML_X_CLASS, IConstants.XWT_X_NAMESPACE);
			if (classAttribute != null) {
				String className = classAttribute.getValue();
				loadShellCLR(className, shell);
			}

		} else {
			pushStack(parent);

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
					if (targetObject instanceof Widget) {
						((Widget) targetObject).setData(UI_FILE_KEY, file);
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
					XamlAttribute classAttribute = element.getAttribute(IConstants.XAML_X_CLASS, IConstants.XWT_X_NAMESPACE);
					if (classAttribute != null) {
						String className = classAttribute.getValue();
						targetObject = loadCLR(className, parameters, metaclass.getType(), options);
					} else {
						Object clr = options.get(XWTLoader.CLASS_PROPERTY);
						if (clr != null) {
							loadData.setClr(clr);
						}
					}
					if (targetObject == null) {
						targetObject = metaclass.newInstance(parameters);
						Widget widget = UserData.getWidget(targetObject);
						if (widget != null) {
							Object clr = loadData.getClr();
							if (clr != null) {
								UserData.setCLR(widget, clr);
							}
						}
					} else {
						metaclass = loader.getMetaclass(targetObject);
					}
				}

				if (targetObject == null) {
					return null;
				}
			}
		}
		Widget widget = UserData.getWidget(targetObject);
		if (widget != null) {
			loadData.setCurrentWidget(targetObject);
		}
		if (scopedObject == null && widget != null) {
			scopedObject = widget;
			nameScoped = new ScopeKeeper((parent == null ? null : UserData.findScopeKeeper((Widget) parent)), widget);
			UserData.bindNameContext((Widget) widget, nameScoped);
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
				UserData.setParent(targetObject, parent);
				((Widget) targetObject).setData(ELEMENT_KEY, element);
			}
		} else if (JFacesHelper.isViewer(targetObject)) {
			UserData.setParent(targetObject, parent);
			UserData.setViewer(targetObject, targetObject);
			// Control control = JFacesHelper.getControl(targetObject);
			// control.setData(ELEMENT_KEY, element);
			((Viewer) targetObject).setData(ELEMENT_KEY, element);
		} else if (targetObject instanceof TableItemProperty.Cell) {
			((TableItemProperty.Cell) targetObject).setParent((TableItem) parent);
		}

		for (String key : options.keySet()) {
			if (IXWTLoader.CONTAINER_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.INIT_STYLE_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.DATACONTEXT_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.RESOURCE_DICTIONARY_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.CLASS_PROPERTY.equalsIgnoreCase(key) || IXWTLoader.LOADED_ACTION.equalsIgnoreCase(key) || IXWTLoader.DESIGN_MODE_ROPERTY.equalsIgnoreCase(key)) {
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
		if (targetObject instanceof Style && element.getChildNodes().size() > 0) {
			Collection<Setter> setters = new ArrayList<Setter>();
			for (XamlNode doc : element.getChildNodes()) {
				Object child = doCreate(targetObject, (XamlElement) doc, null, Collections.EMPTY_MAP);
				if (!(child instanceof Setter)) {
					throw new XWTException("Setter is expected in Style.");
				}
				setters.add((Setter) child);
			}
			((Style) targetObject).setSetters(setters.toArray(new Setter[setters.size()]));
		} else if (targetObject instanceof ControlEditor) {
			for (XamlNode doc : element.getChildNodes()) {
				Object editor = doCreate(parent, (XamlElement) doc, null, Collections.EMPTY_MAP);
				if (editor != null && editor instanceof Control) {
					((ControlEditor) targetObject).setEditor((Control) editor);
					((Control) editor).setData(PropertiesConstants.DATA_CONTROLEDITOR_OF_CONTROL, targetObject);
				}
			}
		} else if (targetObject instanceof IDataProvider) {
			for (XamlNode doc : element.getChildNodes()) {
				if (IConstants.XWT_X_NAMESPACE.equals(doc.getNamespace())) {
					String content = doc.getValue();
					if (content != null) {
						((IDataProvider) targetObject).setProperty(doc.getName(), content);
					}
				}
			}
		} else {
			for (XamlNode doc : element.getChildNodes()) {
				doCreate(targetObject, (XamlElement) doc, null, Collections.EMPTY_MAP);
			}
		}

		for (String delayed : delayedAttributes) {
			initAttribute(metaclass, targetObject, element, null, delayed);
		}
		postCreation(targetObject);
		popStack();
		return targetObject;
	}

	protected void postCreation(Object target) {
		Widget widget = UserData.getWidget(target);
		if (widget == null) {
			return;
		}
		TriggerBase[] triggers = UserData.getTriggers(widget);
		for (TriggerBase triggerBase : triggers) {
			if (triggerBase != null) {
				triggerBase.on(target);
			}
		}
	}

	private void setDataContext(IMetaclass metaclass, Object targetObject, ResourceDictionary dico, Object dataContext) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Object control = null;
		IMetaclass widgetMetaclass = metaclass;
		if (JFacesHelper.isViewer(targetObject)) {
			Widget widget = JFacesHelper.getControl(targetObject);
			widgetMetaclass = loader.getMetaclass(widget.getClass());
			control = targetObject;
		} else if (targetObject instanceof Widget) {
			control = targetObject;
		} else {
			control = loadData.getCurrentWidget();
		}
		if (control != null) {
			if (targetObject instanceof IDynamicBinding) {
				IDynamicBinding dynamicBinding = (IDynamicBinding) targetObject;
				dynamicBinding.setControl(control);
				dynamicBinding.setHost(loadData.getHost());
			}
			if (dico != null) {
				UserData.setResources(control, dico);
			}
			if (dataContext != null) {
				IProperty property = widgetMetaclass.findProperty(IConstants.XAML_DATACONTEXT);
				if (property != null) {
					property.setValue(UserData.getWidget(control), dataContext);
				} else {
					throw new XWTException("DataContext is missing in " + widgetMetaclass.getType().getName());
				}
			}
		}
	}

	private void applyStyles(XamlElement element, Object targetObject) throws Exception {
		if (targetObject instanceof Widget) {
			Widget widget = (Widget) targetObject;
			Map<String, Object> dico = UserData.getLocalResources(widget);
			XamlAttribute attribute = element.getAttribute(IConstants.XAML_RESOURCES);
			if (attribute == null) {
				attribute = element.getAttribute(IConstants.XAML_RESOURCES, IConstants.XWT_NAMESPACE);
			}
			if (attribute != null) {
				if (attribute.getChildNodes().size() > 0) {
					if (dico == null) {
						dico = new ResourceDictionary();
						UserData.setResources(widget, dico);
					}

					for (XamlNode doc : attribute.getChildNodes()) {
						XamlElement elem = (XamlElement) doc;
						Object doCreate = doCreate(widget, elem, null, EMPTY_MAP);
						XamlAttribute keyAttribute = elem.getAttribute(IConstants.XAML_X_KEY, IConstants.XWT_X_NAMESPACE);
						if (keyAttribute == null) {
							keyAttribute = elem.getAttribute(IConstants.XAML_X_TYPE, IConstants.XWT_X_NAMESPACE);
						}
						if (keyAttribute != null) {
							dico.put(keyAttribute.getValue(), doCreate);
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
				dico = UserData.getLocalResources(current);
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
				current = UserData.getParent(current);
			}
		}

		for (IStyle style : loadData.getStyles()) {
			style.applyStyle(targetObject);
		}
	}

	private int getColumnIndex(XamlElement columnElement) {
		String name = columnElement.getName();
		String namespace = columnElement.getNamespace();
		IMetaclass metaclass = loader.getMetaclass(name, namespace);
		int index = -1;
		Class<?> type = metaclass.getType();
		if (TableViewerColumn.class.isAssignableFrom(type)) {
			XamlNode parent = (XamlNode) columnElement.eContainer();
			List<XamlNode> children = NodesSorter.sortWithAttr(parent.getChildNodes().toArray(new XamlNode[0]), "Index");
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
			if (data == null || !(data instanceof XamlElement)) {
				continue;
			}
			int column = table.indexOf(tableColumn);
			XamlElement editor = (XamlElement) data;
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

	private Object getDataContext(XamlElement element, Widget swtObject) {
		// x:DataContext
		try {
			{
				XamlAttribute dataContextAttribute = element.getAttribute("DataContext", IConstants.XWT_NAMESPACE);
				if (dataContextAttribute != null) {
					Widget composite = (Widget) swtObject;
					XamlNode documentObject = dataContextAttribute.getChildNodes().get(0);
					if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
						String key = documentObject.getValue();
						return new StaticResourceBinding(composite, key);
					} else if (IConstants.XAML_BINDING.equals(documentObject.getName())) {
						return doCreate(swtObject, (XamlElement) documentObject, null, EMPTY_MAP);
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

	private void pushStack(Object host) {
		loadData = new LoadingData(loadData, host);
	}

	private void popStack() {
		LoadingData previous = loadData;
		loadData = previous.getParent();

		previous.end();
	}

	private Integer getStyleValue(XamlElement element, int styles) {
		XamlAttribute attribute = element.getAttribute(IConstants.XAML_STYLE, IConstants.XWT_X_NAMESPACE);
		if (attribute == null) {
			if (styles != -1) {
				return styles;
			}
			return null;
		}
		if (styles == -1) {
			return (Integer) loader.findConvertor(String.class, Integer.class).convert(attribute.getValue());
		}
		return styles | (Integer) loader.findConvertor(String.class, Integer.class).convert(attribute.getValue());
	}

	private void init(IMetaclass metaclass, Object targetObject, XamlNode element, List<String> delayedAttributes) throws Exception {
		// editors for TableItem,
		if (targetObject instanceof TableItem) {
			installTableEditors((TableItem) targetObject);
		}

		// x:DataContext
		{
			XamlAttribute dataContextAttribute = element.getAttribute(IConstants.XAML_DATACONTEXT);
			if (dataContextAttribute != null) {
				IProperty property = metaclass.findProperty(IConstants.XAML_DATACONTEXT);
				Widget composite = (Widget) targetObject;
				XamlNode documentObject = dataContextAttribute.getChildNodes().get(0);
				if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
					String key = documentObject.getValue();
					property.setValue(composite, new StaticResourceBinding(composite, key));
				} else if (IConstants.XAML_BINDING.equals(documentObject.getName())) {
					Object object = doCreate(targetObject, (XamlElement) documentObject, null, EMPTY_MAP);
					property.setValue(composite, object);
				} else {
					LoggerManager.log(new UnsupportedOperationException(documentObject.getName()));
				}
			}
		}

		HashSet<String> done = new HashSet<String>();

		XamlAttribute nameAttr = element.getAttribute(IConstants.XAML_X_NAME);
		if (nameAttr == null) {
			nameAttr = element.getAttribute(IConstants.XAML_X_NAME, IConstants.XWT_X_NAMESPACE);
		}
		if (nameAttr != null && UserData.getWidget(targetObject) != null) {
			String value = nameAttr.getValue();
			loadData.inject(targetObject, value);
			
			nameScoped.addNamedObject(value, targetObject);
			done.add(IConstants.XAML_X_NAME);
		}

		for (XamlAttribute attr : element.getAttributes()) {
			String namespace = attr.getNamespace();
			String attrName = attr.getName();

			//
			// 1. handle foreigner namespace
			//
			if (!XWT.isXWTNamespace(namespace)) {
				INamespaceHandler namespaceHandler = loader.getNamespaceHandler(namespace);
				if (namespaceHandler != null) {
					Widget widget = UserData.getWidget(loadData.getCurrentWidget());
					namespaceHandler.handleAttribute(widget, targetObject, attrName, attr.getValue());
				}
				continue;
			}
			//
			// 2. handle 'x' namespace of xwt.
			//
			if (IConstants.XWT_X_NAMESPACE.equals(namespace)) {
				if ("class".equalsIgnoreCase(attrName) || IConstants.XAML_STYLE.equalsIgnoreCase(attrName)) {
					continue; // done before
				} else if (IConstants.XAML_X_NAME.equalsIgnoreCase(attrName)) {
					nameScoped.addNamedObject(element.getAttribute(attrName, namespace).getValue(), targetObject);
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
			//
			// 3. handle delayed attributes.
			//
			else if (delayedAttributes != null && isDelayedProperty(attrName.toLowerCase(), metaclass.getType())) {
				delayedAttributes.add(attrName);
			}
			//
			// 4. handle others.
			//
			else {
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, namespace, attrName);
					done.add(attrName);
				}
			}
		}
	}

	private Object getArrayProperty(Class<?> type, Object swtObject, XamlNode element, String attrName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		if (!type.isArray()) {
			throw new XWTException("Type mismatch: property " + attrName + " isn't an array.");
		}

		Class<?> arrayType = type.getComponentType();
		if (arrayType != null) {
			List<Object> list = new ArrayList<Object>();
			for (XamlElement childModel : element.getChildNodes()) {
				Object child = createInstance(swtObject, childModel);
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
	private Object getCollectionProperty(Class<?> type, Object swtObject, XamlNode element, String attrName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
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

		for (XamlElement childModel : element.getChildNodes()) {
			Object child = createInstance(swtObject, childModel);
			collector.add(child);
			if (child instanceof IIndexedElement) {
				((IIndexedElement) child).setIndex(swtObject, collector.size() - 1);
			}
		}
		return collector;
	}

	private String findNamespace(XamlNode context, String prefix) {
		while (context != null && !(context instanceof XamlElement)) {
			context = (XamlNode) context.eContainer();
		}
		if (context == null) {
			return null;
		}
		XamlElement element = (XamlElement) context;

		if (prefix != null) {
			prefix = (prefix.length() == 0 ? null : prefix);
		}

		String namespace = element.getNamespace();
		if (namespace != null) {
			return namespace;
		}
		XamlNode parent = (XamlNode) element.eContainer();
		return findNamespace(parent, prefix);
	}

	private Object createInstance(Object swtObject, XamlElement element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace) && IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		try {
			Class<?> type = NamespaceHelper.loadCLRClass(loader.getLoadingContext(), name, namespace);
			IMetaclass metaclass = loader.getMetaclass(name, namespace);
			if (type == null) {
				if (metaclass != null)
					type = metaclass.getType();
			}
			// type = expected type;
			// Need to support the
			String content = element.getValue();
			Object instance = null;
			if (content == null) {
				instance = metaclass.newInstance(new Object[] { swtObject });
				if (instance instanceof TableEditor) {
					// TODO should be moved into IMetaclass
					TableEditor tableEditor = (TableEditor) instance;
					if (swtObject instanceof TableItem) {
						TableItem item = (TableItem) swtObject;
						tableEditor.setItem(item);
						for (XamlNode doc : element.getChildNodes()) {
							Control control = (Control) doCreate(((TableItem) swtObject).getParent(), (XamlElement) doc, null, EMPTY_MAP);
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

			for (XamlElement doc : element.getChildNodes()) {
				doCreate(instance, doc, null, Collections.EMPTY_MAP);
			}
			return instance;
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	static protected int getColumnValue(XamlElement context) {
		XamlAttribute attribute = context.getAttribute(COLUMN);
		if (attribute != null) {
			String content = attribute.getValue();
			if (content != null) {
				return Integer.parseInt(content);
			}
		}
		return 0;
	}

	private void loadShellCLR(String className, Shell shell) {
		Class<?> type = ClassLoaderUtil.loadClass(loader.getLoadingContext(), className);
		if (type == null) {
			return;
		}
		try {
			Object instance = type.newInstance();
			loadData.setClr(instance);
			UserData.setCLR(shell, instance);
		} catch (Exception e) {
			LoggerManager.log(e);
		}
	}

	private Object loadCLR(String className, Object[] parameters, Class<?> currentTagType, Map<String, Object> options) {
		Class<?> type = ClassLoaderUtil.loadClass(loader.getLoadingContext(), className);
		if (type == null) {
			return null;
		}
		try {
			Object clr = options.get(XWTLoader.CLASS_PROPERTY);
			if (clr != null && type != null && type.isInstance(clr)) {
				loadData.setClr(clr);
				if (clr instanceof Widget) {
					UserData.setCLR((Widget) clr, clr);
				}
			} else if (currentTagType != null && currentTagType.isAssignableFrom(type)) {
				IMetaclass metaclass = loader.getMetaclass(type);
				Object instance = metaclass.newInstance(parameters);
				loadData.setClr(instance);
				// use x:Class's instance
				if (instance instanceof Widget) {
					UserData.setCLR((Widget) instance, instance);
				}
				return instance;
			} else {
				Object instance = type.newInstance();
				loadData.setClr(instance);
				if (instance instanceof Widget) {
					UserData.setCLR((Widget) instance, instance);
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return null;
	}

	public void initAttribute(IMetaclass metaclass, Object targetObject, XamlNode element, String namespace, String attrName) throws Exception {
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
					LoggerManager.log(new XWTException("Property \"" + segments[i] + "\" not found."));
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

	private void initSegmentAttribute(IMetaclass metaclass, String propertyName, Object target, XamlNode element, String namespace, String attrName) throws Exception {
		XamlAttribute attribute = element.getAttribute(attrName, namespace);
		if (attribute == null) {
			attribute = element.getAttribute(attrName);
		}
		IProperty property = null;
		boolean isAttached = false;
		{
			String groupName = attribute.getGroupName();
			if (groupName == null) {
				property = metaclass.findProperty(propertyName);
			} else {
				//
				if (groupName.length() > 1) {
					groupName = Character.toUpperCase(groupName.charAt(0)) + groupName.substring(1);
				}
				IMetaclass metaclassAttached = loader.getMetaclass(groupName, attribute.getNamespace());
				if (metaclassAttached != null) {
					property = metaclassAttached.findProperty(propertyName);
					isAttached = true;
				} else {
					LoggerManager.log(attribute.getNamespace() + " -> " + groupName + " is not found.");
					return;
				}
			}
		}
		if (propertyName.equals(IConstants.XAML_DATACONTEXT)) {
			property = null;
		}
		if (IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName) && ICommand.class.isAssignableFrom(property.getType()) && (target instanceof Widget)) {
			addCommandExecuteListener(attribute.getValue(), (Widget) target);
		}
		if (property == null) {
			IEvent event = metaclass.findEvent(attrName);
			if (event == null) {
				return;
			}
			// add events for controls and items.
			if (!(target instanceof Widget)) {
				return;
			}
			loadData.updateEvent((Widget) target, event, attribute.getValue());
			return;
		}

		try {
			String contentValue = attribute.getValue();
			if ("MenuItem".equalsIgnoreCase(element.getName()) && "Text".equalsIgnoreCase(attrName)) {
				XamlAttribute attributeAccelerator = element.getAttribute("Accelerator");
				if (attributeAccelerator != null) {
					contentValue = contentValue + '\t' + getContentValue(attributeAccelerator.getValue());
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
			XamlNode[] children = attribute.getChildNodes().toArray(new XamlNode[0]);
			boolean usingExistingValue = false;
			if (contentValue == null) {
				Class<?> type = property.getType();
				if (Collection.class.isAssignableFrom(type)) {
					value = getCollectionProperty(type, target, attribute, attrName);
				} else {
					Object directTarget = null;
					if (TableViewerColumn.class.isAssignableFrom(type) && attrName.equalsIgnoreCase("columns")) {
						children = NodesSorter.sortWithAttr(children, "Index").toArray(new XamlNode[children.length]);
					} else {
						try {
							Object propertyValue = property.getValue(target);
							if (UserData.getWidget(propertyValue) != null) {
								directTarget = propertyValue;
								// use the existing property value as parent, not need to add the constraint
								if (type != Table.class) {
									type = null;
									usingExistingValue = true;
								}
							}
						} catch (Exception e) {
						}
					}
					if (directTarget == null) {
						directTarget = target;
					}

					for (XamlNode child : children) {
						String name = child.getName();
						String ns = child.getNamespace();
						if (IConstants.XAML_X_STATIC.equalsIgnoreCase(name) && IConstants.XWT_X_NAMESPACE.equals(ns)) {
							value = getStaticValue(child);
						} else if (IConstants.XAML_STATICRESOURCES.equalsIgnoreCase(name) && IConstants.XWT_NAMESPACE.equals(ns)) {
							String key = child.getValue();
							value = new StaticResourceBinding(loadData.getCurrentWidget(), key);
						} else if ((IConstants.XWT_X_NAMESPACE.equals(ns) && IConstants.XAML_X_ARRAY.equalsIgnoreCase(name))) {
							value = getArrayProperty(property.getType(), directTarget, child, name);
						} else if (property.getType().isArray()) {
							value = getArrayProperty(property.getType(), directTarget, attribute, name);
							break;
						} else if (isAssignableFrom(element, TableColumn.class) && isAssignableFrom(child, TableEditor.class)) {
							value = child;
						} else if (TableViewerColumn.class.isAssignableFrom(property.getType()) && attribute.getValue() != null) {
							value = attribute.getValue();
						} else {
							value = doCreate(directTarget, (XamlElement) child, type, EMPTY_MAP);
							if (value == null && type != null && !(type == Table.class && "TableColumn".equals(child.getName()) && Table.class.isInstance(directTarget))) {
								throw new XWTException(child.getName() + " cannot be a content of " + type.getName() + " " + target.getClass().getName() + "." + property.getName());
							}
							if (value instanceof IDynamicBinding) {
								((IDynamicBinding) value).setType(attrName);
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
			if (!usingExistingValue) {
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
					if (isAttached) {
						UserData.setLocalData(target, property, value);
					} else {
						property.setValue(target, value);
					}
				} else {
					if (value == null) {
						value = property.getValue(target);
					}
					if (value != null) {
						// create children.
						for (XamlNode child : children) {
							String name = child.getName();
							String ns = child.getNamespace();
							if (!IConstants.XWT_X_NAMESPACE.equals(ns) || !IConstants.XAML_X_ARRAY.equalsIgnoreCase(name)) {
								Class<?> type = property.getType();
								if (!Collection.class.isAssignableFrom(type)) {
									doCreate(value, (XamlElement) child, null, EMPTY_MAP);
								}
							}
						}
					}
				}
			}

			if (attribute.attributeNames(IConstants.XWT_NAMESPACE).size() > 0) {
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
			ILoadingContext loadingContext = loader.getLoadingContext();
			URL resource = loadingContext.getResource(contentValue);
			if (resource == null) {
				// try {
				// resource = new URL(context.getResourcePath() + contentValue);
				// } catch (MalformedURLException e1) {
				// }
			}
			return resource.toString();
		}
		if (url != null) {
			return url.toString();
		}
		return contentValue;
	}

	private Class<?> getJavaType(XamlNode element) {
		if (!(element instanceof XamlElement)) {
			return null;
		}
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

	private boolean isAssignableFrom(XamlNode element, Class<?> type) {
		Class<?> targetType = getJavaType(element);
		if (targetType == null) {
			return false;
		}
		return targetType.isAssignableFrom(type);
	}

	private Object getStaticValue(XamlNode child) {
		XamlNode[] children = child.getChildNodes().toArray(new XamlNode[0]);
		if (children.length == 1) {
			XamlElement element = (XamlElement) children[0];
			if (element != null) {
				return ClassLoaderUtil.loadStaticMember(loader.getLoadingContext(), element);
			}
		}
		return null;
	}

	private String getImagePath(XamlAttribute attribute, String contentValue) {
		try {
			File file = new File(contentValue);
			if (file.exists()) {
				return file.toURI().toURL().toString();
			}
			if (!contentValue.startsWith("/")) {
				contentValue = "/" + contentValue;
			}
			ILoadingContext loadingContext = loader.getLoadingContext();
			URL resource = loadingContext.getResource(contentValue);
			if (resource == null) {
				URL resourcePath = loader.getResourcePath();
				String fPath = resourcePath.getFile();
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
		// {
		// Collection<Class<?>> types = new ArrayList<Class<?>>();
		// types.add(TableViewer.class);
		// DELAYED_ATTRIBUTES.put("labelprovider", types);
		// }
	}

}
