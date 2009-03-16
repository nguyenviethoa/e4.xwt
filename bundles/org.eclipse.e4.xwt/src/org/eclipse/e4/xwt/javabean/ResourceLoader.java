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
import java.util.Set;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IIndexedElement;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.XWTMaps;
import org.eclipse.e4.xwt.dataproviders.IXMLDataProvider;
import org.eclipse.e4.xwt.impl.Core;
import org.eclipse.e4.xwt.impl.IBinding;
import org.eclipse.e4.xwt.impl.IDataContextControl;
import org.eclipse.e4.xwt.impl.IRenderingContext;
import org.eclipse.e4.xwt.impl.IUserDataConstants;
import org.eclipse.e4.xwt.impl.IVisualElementLoader;
import org.eclipse.e4.xwt.impl.NameScope;
import org.eclipse.e4.xwt.input.ICommand;
import org.eclipse.e4.xwt.javabean.metadata.Metaclass;
import org.eclipse.e4.xwt.javabean.metadata.BindingMetaclass.Binding;
import org.eclipse.e4.xwt.javabean.metadata.properties.PropertiesConstants;
import org.eclipse.e4.xwt.javabean.metadata.properties.TableItemProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.utils.ClassLoaderUtil;
import org.eclipse.e4.xwt.utils.DocumentObjectSorter;
import org.eclipse.e4.xwt.utils.LoggerManager;
import org.eclipse.e4.xwt.utils.NamespaceHelper;
import org.eclipse.e4.xwt.utils.ObjectUtil;
import org.eclipse.e4.xwt.utils.TableEditorHelper;
import org.eclipse.e4.xwt.utils.UserDataHelper;
import org.eclipse.e4.xwt.xml.Attribute;
import org.eclipse.e4.xwt.xml.DocumentObject;
import org.eclipse.e4.xwt.xml.Element;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
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

	protected Object scopedObject;
	protected NameScope nameScoped;
	protected LoadingData loadData = new LoadingData();

	class LoadingData {
		protected LoadingData parent;
		protected Object clr;
		protected Collection<IStyle> styles = Collections.EMPTY_LIST;
		private Object loadedObject = null;
		private Method loadedMethod = null;
		private Widget widget = null;

		public LoadingData getParent() {
			return parent;
		}

		public LoadingData() {
		}

		public LoadingData(LoadingData loadingData) {
			this.loadedObject = loadingData.loadedObject;
			this.loadedMethod = loadingData.loadedMethod;
			this.widget = loadingData.widget;
			this.parent = loadingData;
			this.styles = loadingData.styles;
			this.clr = loadingData.clr;
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
							this.widget = control;
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
			if (loadedObject != null && loadedMethod != null && widget != null) {
				Event event = new Event();
				event.doit = true;
				event.widget = widget;
				try {
					loadedMethod.invoke(loadedObject, new Object[] { event });
				} catch (Exception e) {
					throw new XWTException("");
				}
				loadedObject = null;
				loadedMethod = null;
				widget = null;
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
	public ResourceLoader(IRenderingContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.IVisualElementLoader#createCLRElement(org.eclipse. e4.xwt.Element, org.eclipse.e4.xwt.ILoadData, org.eclipse.e4.xwt.IResourceDictionary)
	 */
	public Object createCLRElement(Element element, Map<String, Object> options) {
		try {
			Composite parent = (Composite) options.get(XWT.CONTAINER_PROPERTY);
			if (!XWT.getTrackings().isEmpty()) {
				dataBindingTrack = new DataBindingTrack();
			}
			parentLoader = (ResourceLoader) options.get(RESOURCE_LOADER_PROPERTY);
			options.remove(RESOURCE_LOADER_PROPERTY);
			ResourceDictionary resourceDictionary = (ResourceDictionary) options.get(XWT.RESOURCE_DICTIONARY_PROPERTY);
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
				org.eclipse.e4.xwt.ILogger log = XWT.getLogger();
				log.addMessage(dataBindingMessage, Tracking.DATABINDING);
				log.printInfo(dataBindingMessage, Tracking.DATABINDING, XWT.getTrackings());
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

	protected Object doCreate(Object parent, Element element, Class<?> constraintType, Map<String, Object> options) throws Exception {
		int styles = -1;
		if (options.containsKey(XWT.INIT_STYLE_PROPERTY)) {
			styles = (Integer) options.get(XWT.INIT_STYLE_PROPERTY);
		}

		ResourceDictionary dico = (ResourceDictionary) options.get(XWT.RESOURCE_DICTIONARY_PROPERTY);
		Object dataContext = options.get(XWT.DATACONTEXT_PROPERTY);
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace)) {
			if (IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
				return null;
			}
			if ("Type".equalsIgnoreCase(name) && constraintType != null && constraintType == Class.class) {
				DocumentObject[] children = element.getChildren();
				if (children != null && children[0] instanceof Element) {
					Element type = (Element) children[0];
					IMetaclass metaclass = XWT.getMetaclass(type.getName(), type.getNamespace());
					if (metaclass != null) {
						return metaclass.getType();
					}
				}
			}
			return null;
		}
		IMetaclass metaclass = XWT.getMetaclass(name, namespace);
		if (constraintType != null && !(IBinding.class.isAssignableFrom(metaclass.getType())) && (!constraintType.isAssignableFrom(metaclass.getType()))) {
			if (!constraintType.isArray() || !constraintType.getComponentType().isAssignableFrom(metaclass.getType()))
				return null;
		}
		// ...
		trace("load: " + metaclass.getName());
		Display display = Display.getDefault();
		Object swtObject = null;
		Integer styleValue = getStyleValue(element, styles);

		if (parent == null || metaclass.getType() == Shell.class) {
			if (dataBindingTrack != null) {
				dataBindingTrack.addWidgetElement(element);
			}
			Shell shell = null;
			if (styles == -1) {
				styleValue = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE;
			}
			shell = new Shell(display, styleValue);
			swtObject = shell;

			if (metaclass.getType() != Shell.class) {
				shell.setLayout(new FillLayout());
				return doCreate(swtObject, element, constraintType, options);
			} else if (dataContext != null) {
				setDataContext(metaclass, swtObject, dico, dataContext);
			}
			pushStack();
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
					nestedOptions.put(XWT.CONTAINER_PROPERTY, parent);
					if (styleValue != null) {
						nestedOptions.put(XWT.INIT_STYLE_PROPERTY, styleValue);
					}
					nestedOptions.put(XWT.DATACONTEXT_PROPERTY, childDataContext);
					nestedOptions.put(RESOURCE_LOADER_PROPERTY, this);
					swtObject = XWT.loadWithOptions(file, nestedOptions);
					if (swtObject == null) {
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
				swtObject = metaclass.newInstance(parameters);
				if (swtObject == null) {
					return null;
				}
			}
		}

		// set first data context and resource dictionary
		setDataContext(metaclass, swtObject, dico, dataContext);

		applyStyles(element, swtObject);

		// get databindingMessages
		if (swtObject instanceof Binding) {
			//
			// TODO To move in ObjectDataProvider
			//
			Binding newInstance = (Binding) swtObject;
			String path = null;
			Attribute attr = element.getAttribute("Path");
			if (null == attr)
				attr = element.getAttribute("path");
			if (null != attr)
				path = attr.getContent();
			Object dataContext2 = null;
			try {
				dataContext2 = newInstance.getValue();
				if (path != null && path.length() > 0) {
					String[] paths = path.trim().split("\\.");
					if (paths.length > 1) {
						String path1 = "";
						for (int i = 0; i < paths.length - 1; i++) {
							path1 = paths[i];
							if (dataContext2 != null) {
								dataContext2 = DataBindingTrack.getObserveData(dataContext2, path1);
							}
						}
						BeansObservables.observeValue(dataContext2, paths[paths.length - 1]);
					} else if (paths.length == 1) {
						BeansObservables.observeValue(dataContext2, path);
					}
				}
			} catch (Exception ex) {
				LoggerManager.log(ex);
			}
		}

		if (dataBindingTrack != null) {
			dataBindingTrack.tracking(swtObject, element, dataContext);
		}

		// set parent relationship and viewer
		if (swtObject instanceof Widget) {
			if (parent != null) {
				((Widget) swtObject).setData(IUserDataConstants.XWT_PARENT_KEY, parent);
			}
		} else if (JFacesHelper.isViewer(swtObject)) {
			Control control = JFacesHelper.getControl(swtObject);
			control.setData(IUserDataConstants.XWT_PARENT_KEY, parent);
			control.setData(IUserDataConstants.XWT_VIEWER_KEY, swtObject);
		} else if (swtObject instanceof TableItemProperty.Cell) {
			((TableItemProperty.Cell) swtObject).setParent((TableItem) parent);
		}

		for (String key : options.keySet()) {
			if (XWT.CONTAINER_PROPERTY.equalsIgnoreCase(key) || XWT.INIT_STYLE_PROPERTY.equalsIgnoreCase(key) || XWT.DATACONTEXT_PROPERTY.equalsIgnoreCase(key) || XWT.RESOURCE_DICTIONARY_PROPERTY.equalsIgnoreCase(key)) {
				continue;
			}
			IProperty property = metaclass.findProperty(key);
			if (property == null) {
				throw new XWTException("Property " + key + " not found.");
			}
			property.setValue(swtObject, options.get(key));
		}
		if (scopedObject == null && swtObject instanceof Widget) {
			scopedObject = swtObject;
			nameScoped = new NameScope((parent == null ? null : XWT.findNameContext((Widget) parent)));
			UserDataHelper.bindNameContext((Widget) swtObject, nameScoped);
		}

		List<String> delayedAttributes = new ArrayList<String>();
		init(metaclass, swtObject, element, delayedAttributes);
		if (swtObject instanceof Composite) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((Composite) swtObject, (Element) doc, null, Collections.EMPTY_MAP); // TODO
				// cast
			}
		} else if (swtObject instanceof Menu) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((Menu) swtObject, (Element) doc, null, Collections.EMPTY_MAP); // TODO
				// cast
			}
		} else if (swtObject instanceof TreeItem) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((TreeItem) swtObject, (Element) doc, null, Collections.EMPTY_MAP); // TODO
				// cast
			}
		} else if (swtObject instanceof ControlEditor) {
			for (DocumentObject doc : element.getChildren()) {
				Object editor = doCreate(parent, (Element) doc, null, Collections.EMPTY_MAP);
				if (editor != null && editor instanceof Control) {
					((ControlEditor) swtObject).setEditor((Control) editor);
					((Control) editor).setData(PropertiesConstants.DATA_CONTROLEDITOR_OF_CONTROL, swtObject);
				}
			}
		} else if (swtObject instanceof IXMLDataProvider) {
			for (DocumentObject doc : element.getChildren()) {
				if ("XData".equalsIgnoreCase(doc.getName()) && IConstants.XWT_X_NAMESPACE.equals(doc.getNamespace())) {
					String content = getDocContent(doc);
					if (content != null) {
						((IXMLDataProvider) swtObject).setXDataContent(content);
					}
				}
			}
		}
		for (String delayed : delayedAttributes) {
			initAttribute(metaclass, swtObject, element, null, delayed);
		}
		popStack();
		return swtObject;
	}

	private String getDocContent(DocumentObject object) {
		String content = object.getContent();
		if (content != null) {
			return content;
		}
		StringBuilder sb = new StringBuilder();
		DocumentObject[] children = object.getChildren();
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			sb.append("<");
			sb.append(name + " ");
			if (children[i] instanceof Element) {
				String[] attributeNames = ((Element) children[i]).attributeNames();
				for (String attrName : attributeNames) {
					sb.append(attrName + "=\"");
					sb.append(((Element) children[i]).getAttribute(attrName).getContent() + "\"");
				}
			}
			sb.append(">");
			sb.append(getDocContent(children[i]));
			sb.append("</" + name + ">");
		}
		return content = sb.toString();
	}

	private void setDataContext(IMetaclass metaclass, Object swtObject, ResourceDictionary dico, Object dataContext) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Widget widget = null;
		IMetaclass widgetMetaclass = metaclass;
		if (JFacesHelper.isViewer(swtObject)) {
			widget = JFacesHelper.getControl(swtObject);
			widgetMetaclass = XWT.getMetaclass(widget.getClass());
		} else if (swtObject instanceof Widget) {
			widget = (Widget) swtObject;
		}
		if (widget != null) {
			if (dico != null) {
				widget.setData(IUserDataConstants.XWT_RESOURCES_KEY, dico);
			}
			if (dataContext != null) {
				if (widget instanceof IDataContextControl) {
					IDataContextControl contextControl = (IDataContextControl) widget;
					contextControl.setDataContext(dataContext);
				} else {
					IProperty property = widgetMetaclass.findProperty(IConstants.XAML_DATACONTEXT);
					if (property != null) {
						property.setValue(widget, dataContext);
					} else {
						throw new XWTException("DataContext is missing in " + widgetMetaclass.getType().getName());
					}
				}
			}
		}
	}

	private void applyStyles(Element element, Object targetObject) throws Exception {
		if (targetObject instanceof Widget) {
			Widget composite = (Widget) targetObject;
			ResourceDictionary dico = (ResourceDictionary) composite.getData(IUserDataConstants.XWT_RESOURCES_KEY);
			if (dico == null) {
				dico = new ResourceDictionary();
				composite.setData(IUserDataConstants.XWT_RESOURCES_KEY, dico);
			}

			Attribute attribute = element.getAttribute(IConstants.XAML_RESOURCES);
			if (attribute == null) {
				attribute = element.getAttribute(IConstants.XWT_NAMESPACE, IConstants.XAML_RESOURCES);
			}
			if (attribute != null) {
				for (DocumentObject doc : attribute.getChildren()) {
					Element elem = (Element) doc;
					Object doCreate = doCreate(composite, elem, null, EMPTY_MAP);
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

		for (IStyle style : loadData.getStyles()) {
			style.applyStyle(targetObject);
		}
	}

	private int getColumnIndex(Element columnElement) {
		String name = columnElement.getName();
		String namespace = columnElement.getNamespace();
		IMetaclass metaclass = XWT.getMetaclass(name, namespace);
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

	protected Object getDataContext(Element element, Widget swtObject) {
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

	protected Object getStaticResourceContext(Element element, Widget swtObject) {
		String key = element.getContent();
		return new StaticResourceBinding(swtObject, key);
	}

	protected void pushStack() {
		loadData = new LoadingData(loadData);
	}

	protected void popStack() {
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
			return (Integer) XWT.findConvertor(String.class, Integer.class).convert(attribute.getContent());
		}
		return styles | (Integer) XWT.findConvertor(String.class, Integer.class).convert(attribute.getContent());
	}

	private void init(IMetaclass metaclass, Object targetObject, Element element, List<String> delayedAttributes) throws Exception {
		// editors for TableItem,
		{
			if (targetObject instanceof TableItem) {
				installTableEditors((TableItem) targetObject);
			}
		}

		// x:Class
		{
			Attribute classAttribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_CLASS);
			if (classAttribute != null) {
				String className = classAttribute.getContent();
				loadCLR(className, targetObject);
			}
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
		for (String attrName : element.attributeNames()) {
			{
				if ("name".equalsIgnoreCase(attrName) && (targetObject instanceof Widget)) {
					Attribute attribute = element.getAttribute(attrName);
					String namespace = attribute.getNamespace();
					if (IConstants.XWT_NAMESPACE.equals(namespace)) {
						nameScoped.addObject(attribute.getContent(), targetObject);
						continue;
					}
				}
			}

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
					if ("class".equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if ("name".equalsIgnoreCase(attrName)) {
						nameScoped.addObject(element.getAttribute(namespace, attrName).getContent(), targetObject);
					} else if ("dataContext".equalsIgnoreCase(attrName)) {
						continue; // done before
					} else if ("array".equalsIgnoreCase(attrName)) {
						IProperty property = metaclass.findProperty(attrName);
						Class<?> type = property.getType();
						Object value = getArrayProperty(type, targetObject, element, attrName);
						if (value != null) {
							property.setValue(targetObject, value);
						}
					} else if ("Resources".equalsIgnoreCase(attrName)) {
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
				if ("name".equalsIgnoreCase(attrName) && (targetObject instanceof Widget)) {
					continue;
				}
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, namespace, attrName);
					done.add(attrName);
				}
			}

			for (String attrName : element.attributeNames()) {
				if ("name".equalsIgnoreCase(attrName) && (targetObject instanceof Widget)) {
					continue;
				}
				if (!done.contains(attrName)) {
					initAttribute(metaclass, targetObject, element, namespace, attrName);
					done.add(attrName);
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

	protected Class<?> resolveType(DocumentObject objectContext, String value) {
		int index = value.indexOf(':');
		String prefix = null;
		String name = value;
		if (index != -1) {
			prefix = value.substring(0, index);
			name = value.substring(index);
		}
		if (prefix == null) {
			return ClassLoaderUtil.loadClass(context.getLoadingContext(), name);
		}
		String namespace = findNamespace(objectContext, prefix);
		if (namespace == null) {
			LoggerManager.log(new XWTException("Namespace \"" + prefix + "\" is not defined"));
		}
		try {
			return NamespaceHelper.loadCLRClass(context.getLoadingContext(), name, namespace);
		} catch (ClassNotFoundException e) {
			LoggerManager.log(e);
		}
		return null;
	}

	protected String findNamespace(DocumentObject context, String prefix) {
		while (context != null && !(context instanceof Element)) {
			context = context.getParent();
		}
		if (context == null) {
			return null;
		}
		Element element = (Element) context;

		if (prefix != null) {
			prefix = (prefix.trim().length() == 0 ? null : prefix);
		}

		for (String attrName : element.attributeNames()) {
			if (prefix == null) {
				if (attrName.equals(IConstants.XML_NS)) {
					return element.getAttribute(attrName).getContent();
				}
				continue;
			}
			if (attrName.startsWith(IConstants.XML_NS)) {
				String suffix = attrName.substring(IConstants.XML_NS.length());
				if (suffix.length() > 0 && suffix.charAt(0) == ':') {
					suffix = suffix.substring(1);
					if (prefix.equals(suffix)) {
						return element.getAttribute(attrName).getContent();
					}
				}
			}
		}
		DocumentObject parent = element.getParent();
		return findNamespace(parent, prefix);
	}

	protected Object createInstance(Object swtObject, Element element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace) && IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		try {
			Class<?> type = NamespaceHelper.loadCLRClass(context.getLoadingContext(), name, namespace);
			IMetaclass metaclass = XWT.getMetaclass(name, namespace);
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
					instance = constructor.newInstance(XWT.convertFrom(type, content));
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

	protected boolean loadCLR(String className, Object currentObject) {
		Class<?> type = ClassLoaderUtil.loadClass(context.getLoadingContext(), className);
		try {
			if (currentObject.getClass() != type) {
				Object instance = type.newInstance();
				loadData.setClr(instance);
				if (currentObject instanceof Widget) {
					UserDataHelper.setCLR((Widget) currentObject, instance);
				}
			} else {
				loadData.setClr(currentObject);
				if (currentObject instanceof Widget) {
					UserDataHelper.setCLR((Widget) currentObject, currentObject);
				}
			}
			return true;
		} catch (Exception e) {
			LoggerManager.log(e);
		}
		return false;
	}

	protected void trace(String message) {
		// System.out.println(message);
	}

	private void initAttribute(IMetaclass metaclass, Object swtObject, Element element, String namespace, String attrName) throws Exception {
		trace("Set attribute: " + metaclass.getName() + "." + attrName);
		if (attrName.indexOf('.') != -1) {
			String[] segments = attrName.split("\\.");
			IMetaclass currentMetaclass = metaclass;
			Object target = swtObject;
			for (int i = 0; i < segments.length - 1; i++) {
				IProperty property = currentMetaclass.findProperty(segments[i]);
				if (property != null) {
					target = property.getValue(target);
					if (target == null) {
						LoggerManager.log(new XWTException("Property \"" + segments[i] + "\" is null."));
					}
					currentMetaclass = XWT.getMetaclass(target);
				} else {
					LoggerManager.log(new XWTException("Property \"" + segments[i] + "\" not found."));
				}
			}
			initSegmentAttribute(currentMetaclass, segments[segments.length - 1], target, element, namespace, attrName);
			return;
		}
		initSegmentAttribute(metaclass, attrName, swtObject, element, namespace, attrName);
	}

	private void addCommandExecuteListener(String commandName, final Widget targetButton) {
		final ICommand commandObj = XWT.getCommand(commandName);
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
		setStyleProperty(target, element, metaclass);
		if (propertyName.equals(IConstants.XAML_DATACONTEXT)) {
			property = null;
		}
		if (propertyName.trim().equalsIgnoreCase("Resources")) {
			setDictionary(target, attribute);
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
			if (contentValue != null && ("Image".equalsIgnoreCase(attrName) || "BackgroundImage".equalsIgnoreCase(attrName))) {
				contentValue = getImagePath(attribute, contentValue);
			}
			if (contentValue != null && "Source".equalsIgnoreCase(attrName) && target instanceof IXMLDataProvider) {
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
							setStyleProperty(target, child, metaclass);
							return;
						} else if ((IConstants.XWT_X_NAMESPACE.equals(ns) && IConstants.XAML_X_ARRAY.equalsIgnoreCase(name))) {
							// property = ((Metaclass)
							// metaclass).getArrayProperty(property);
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
						}
					}
				}
			}
			if (contentValue != null && value == null && !IConstants.XAML_COMMAND.equalsIgnoreCase(propertyName)) {
				value = XWT.convertFrom(property.getType(), contentValue);
			}
			if (value != null) {
				if (!property.getType().isAssignableFrom(value.getClass()) || value instanceof IBinding) {
					IConverter converter = XWT.findConvertor(value.getClass(), property.getType());
					if (converter != null) {
						value = converter.convert(value);
					} else {
						LoggerManager.log(new XWTException("Convertor " + value.getClass().getSimpleName() + "->" + property.getType().getSimpleName() + " is not found"));
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
				IMetaclass propertyMetaclass = XWT.getMetaclass(property.getType());
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

	protected Class<?> getJavaType(DocumentObject element) {
		String name = element.getName();
		String namespace = element.getNamespace();
		if (IConstants.XWT_X_NAMESPACE.equalsIgnoreCase(namespace) && IConstants.XAML_X_NULL.equalsIgnoreCase(name)) {
			return null;
		}
		IMetaclass metaclass = XWT.getMetaclass(name, namespace);
		if (metaclass == null) {
			return null;
		}
		return metaclass.getType();
	}

	protected boolean isAssignableFrom(DocumentObject element, Class<?> type) {
		Class<?> targetType = getJavaType(element);
		if (targetType == null) {
			return false;
		}
		return targetType.isAssignableFrom(type);
	}

	private void setStyleProperty(Object target, DocumentObject element, IMetaclass metaclass) {
		Object value = null;
		String key = null;
		if (element != null) {
			key = element.getContent();
			if (key == null) {
				key = element.getName();
			}
			if (key == null) {
				return;
			}
		}
		if (getDictionary(target) != null) {
			ResourceDictionary dico = getDictionary(target);
			if (dico.containsKey(key)) {
				StyleSetterMap styleSetterMap = (StyleSetterMap) dico.get(key);
				Set<String> keySet = styleSetterMap.keySet();
				for (String propName : keySet) {
					String propValue = styleSetterMap.get(propName);
					IProperty prop = metaclass.findProperty(propName);
					if (prop != null && propValue != null) {
						value = XWT.convertFrom(prop.getType(), propValue);
						try {
							prop.setValue(target, value);
						} catch (Exception e) {
							LoggerManager.log(e);
						}
					}
				}
			}
		}
	}

	private void setDictionary(Object target, Attribute attribute) {
		Widget composite = (Widget) target;
		ResourceDictionary dico = null;
		if (composite != null) {
			dico = (ResourceDictionary) composite.getData(IUserDataConstants.XWT_STYLE_KEY);
		}
		if (dico == null) {
			dico = new ResourceDictionary();
			composite.setData(IUserDataConstants.XWT_STYLE_KEY, dico);
		}
		if (attribute != null) {
			for (DocumentObject doc : attribute.getChildren()) {
				Element element = (Element) doc;
				Attribute keyAttribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, IConstants.XAML_X_KEY);
				Attribute targetTypeAttribute = element.getAttribute(IConstants.XAML_X_TARGET_TYPE);
				// x:key
				if (keyAttribute != null) {
					setSetterMap(dico, element, keyAttribute.getContent());
				}
				// TargetType
				if (targetTypeAttribute != null) {
					setTargetTypeDictionary(dico, targetTypeAttribute, element);
				}
			}
		}
	}

	private void setTargetTypeDictionary(ResourceDictionary dico, Element attribute, Element element) {
		if (attribute.getContent() != null) {
			setSetterMap(dico, element, attribute.getContent());
		} else {
			DocumentObject[] children = attribute.getChildren();
			if (children.length == 1) {
				Element typeElement = (Element) children[0];
				setTargetTypeDictionary(dico, typeElement, element);
			} else {
				setSetterMap(dico, element, attribute.getName());
			}
		}
	}

	private void setSetterMap(ResourceDictionary dico, Element element, String content) {
		if (content != null) {
			String strContent = content;
			if (strContent.contains(":")) {
				int position = strContent.indexOf(":");

				String prefix = strContent.substring(0, position);
				strContent = removeSubString(strContent, prefix + ":");
			}
			Map<String, String> contents = new HashMap<String, String>();
			for (DocumentObject docs : element.getChildren()) {
				Element setter = (Element) docs;
				Attribute attribute = setter.getAttribute("Property");
				if (attribute == null) {
					continue; // Not for styles, maybe here are the codes of define databindings,
				}
				String setterProperty = attribute.getContent();
				attribute = setter.getAttribute("Value");
				if (attribute == null) {
					continue;// Not for styles
				}
				String setterValue = attribute.getContent();
				contents.put(setterProperty, setterValue);
			}
			if (!contents.isEmpty()) {
				StyleSetterMap setterMap = new StyleSetterMap(strContent);
				setterMap.putAll(contents);
				dico.put(strContent, setterMap);
			}
		}
	}

	private ResourceDictionary getDictionary(Object target) {
		if (target instanceof Widget) {
			Widget composite = (Widget) target;
			if (composite instanceof Control) {
				ResourceDictionary dico = (ResourceDictionary) composite.getData(IUserDataConstants.XWT_STYLE_KEY);
				if (dico != null) {
					return dico;
				} else {
					composite = ((Control) composite).getParent();
					if (composite != null) {
						return getDictionary(composite);
					}
				}
			}
		}
		return null;
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
				resource = new URL(context.getResourcePath() + contentValue);
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

	protected Object getCLRObject() {
		return loadData.getClr();
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
		{
			Collection<Class<?>> types = new ArrayList<Class<?>>();
			types.add(TableViewer.class);
			DELAYED_ATTRIBUTES.put("labelprovider", types);
		}
	}

}
