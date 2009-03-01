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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IIndexedElement;
import org.eclipse.e4.xwt.ILoadData;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.ResourceDictionary;
import org.eclipse.e4.xwt.Tracking;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.XWTMaps;
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
 * @author jliu
 */
public class ResourceLoader implements IVisualElementLoader {
	private static final HashMap<String, Collection<Class<?>>> DELAYED_ATTRIBUTES = new HashMap<String, Collection<Class<?>>>();
	private static final String COLUMN = "Column";

	protected static Stack<Object> stackCLR = new Stack<Object>();
	protected IRenderingContext context;

	private Object loadedObject = null;
	private Method loadedMethod = null;
	private Widget widget = null;

	protected Object scopedObject;
	protected NameScope nameScoped;

	private String bindingError = "";
	private Set<String> errorElements = new HashSet<String>();

	private List<Element> widgetList;
	private Map<Element, String> bindingMap;

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
	public Object createCLRElement(Element element, ILoadData loadData) {
		try {
			Composite parent = loadData.getParent();
			widgetList = new ArrayList<Element>();
			bindingMap = new HashMap<Element, String>();
			Object control = doCreate(parent, element, null, loadData.getStyles(), loadData.getResourceDictionary(), loadData.getDataContext());
			// get databinding messages and print into console view
			if (XWT.isTracking(Tracking.DATABINDING)) {
				String dataBindingMessage = getDataBindMessage();
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

	public Object doCreate(Object parent, Element element, Class<?> constraintType, int styles) throws Exception {
		return doCreate(parent, element, constraintType, styles, null, null);
	}

	public Object doCreate(Object parent, Element element, Class<?> constraintType, int styles, ResourceDictionary dico, Object dataContext) throws Exception {
		String bindingMessage = "";
		String name = element.getName();
		String namespace = element.getNamespace();
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
			Shell shell = null;
			if (styles == -1) {
				styleValue = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE;
			}
			shell = new Shell(display, styleValue);
			swtObject = shell;
			Attribute dataContextAttribute = element.getAttribute("DataContext");
			if (dataContextAttribute != null) {
				DocumentObject documentObject = dataContextAttribute.getChildren()[0];
				if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
					widgetList.add(element);
				}
			}

			if (metaclass.getType() != Shell.class) {
				shell.setLayout(new FillLayout());
				return doCreate(swtObject, element, constraintType, styles, dico, dataContext);
			}
		} else {
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
					swtObject = XWT.load((Composite) parent, file, styleValue == null ? -1 : styleValue, childDataContext);
					if (swtObject == null) {
						return null;
					}
				} else
					throw new XWTException("Cannot add user control: Parent is not a composite");
			} else {
				Object[] parameters = null;
				if (type == TableViewerColumn.class) {
					int columnIndex = getColumnIndex(element);
					parameters = (styleValue != null ? new Object[] { parent, styleValue, columnIndex } : new Object[] { parent, SWT.NONE, columnIndex });
				} else {
					parameters = (styleValue != null ? new Object[] { parent, styleValue } : new Object[] { parent });
				}
				swtObject = metaclass.newInstance(parameters);
				if (swtObject == null) {
					return null;
				}
				// set first data context and resource dictionary
				{
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

				if (swtObject instanceof Binding) {
					String error = "";
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
										dataContext2 = getObserveData(dataContext2, path1);
									}
								}
								BeansObservables.observeValue(dataContext2, paths[paths.length - 1]);
							} else if (paths.length == 1) {
								BeansObservables.observeValue(dataContext2, path);
							}
						} else {

						}
					} catch (Exception ex) {
						errorElements.add(element.getParent().getParent().getId());
						bindingError = "-> error";
						error = "-> error";
					}
					if (dataContext2 != null) {
						bindingMessage = " (DataContext=" + dataContext2.getClass().getSimpleName() + ", Path=" + path + ")" + error + "\n";
						bindingMap.put((Element) element.getParent().getParent(), bindingMessage);
					}
				} else if (swtObject instanceof Widget) {
					Attribute textAttribute = element.getAttribute("Text");
					Attribute dataContextAttribute = element.getAttribute("DataContext");
					if (textAttribute != null) {
						DocumentObject[] objs = textAttribute.getChildren();
						if (objs.length > 0) {
							for (DocumentObject obj : objs) {
								if (obj.getName().equals("Binding")) {
									widgetList.add(element);
									break;
								}
							}
						}
					} else if (dataContextAttribute != null) {
						DocumentObject documentObject = dataContextAttribute.getChildren()[0];
						if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
							widgetList.add(element);
						}
					}
					if (dataContext != null) {
						widgetList.add(element);
						bindingMessage = " (DataContext=" + dataContext.getClass().getSimpleName() + ")\n";
						bindingMap.put(element, bindingMessage);
					}
				} else if (JFacesHelper.isViewer(swtObject)) {
					if (dataContext != null) {
						bindingMessage = " (DataContext=" + dataContext.getClass().getSimpleName() + ")\n";
						bindingMap.put(element, bindingMessage);
					}
				} else if (element.attributeNames(IConstants.XWT_X_NAMESPACE).length > 0) {
					// ??
					if (element.getParent() != null && element.getParent().getParent() != null) {
						bindingMessage = " (DataContext=" + name + ")\n";
						bindingMap.put((Element) element.getParent().getParent(), bindingMessage);
					}
				}
			}

			// set parent relationship and viewer
			if (swtObject instanceof Widget) {
				((Widget) swtObject).setData(IUserDataConstants.XWT_PARENT_KEY, parent);
			} else if (JFacesHelper.isViewer(swtObject)) {
				Control control = JFacesHelper.getControl(swtObject);
				control.setData(IUserDataConstants.XWT_PARENT_KEY, parent);
				control.setData(IUserDataConstants.XWT_VIEWER_KEY, swtObject);
			} else if (swtObject instanceof TableItemProperty.Cell) {
				((TableItemProperty.Cell) swtObject).setParent((TableItem) parent);
			}
		}
		if (scopedObject == null && swtObject instanceof Widget) {
			scopedObject = swtObject;
			nameScoped = new NameScope((parent == null ? null : XWT.findNameContext((Widget) parent)));
			UserDataHelper.bindNameContext((Widget) swtObject, nameScoped);
		}

		List<String> delayedAttributes = new ArrayList<String>();
		boolean stacked = init(metaclass, swtObject, element, delayedAttributes);
		if (swtObject instanceof Composite) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((Composite) swtObject, (Element) doc, null, -1); // TODO
				// cast
			}
		} else if (swtObject instanceof Menu) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((Menu) swtObject, (Element) doc, null, -1); // TODO
				// cast
			}
		} else if (swtObject instanceof TreeItem) {
			for (DocumentObject doc : element.getChildren()) {
				doCreate((TreeItem) swtObject, (Element) doc, null, -1); // TODO
				// cast
			}
		} else if (swtObject instanceof ControlEditor) {
			for (DocumentObject doc : element.getChildren()) {
				Object editor = doCreate(parent, (Element) doc, null, -1);
				if (editor != null && editor instanceof Control) {
					((ControlEditor) swtObject).setEditor((Control) editor);
					((Control) editor).setData(PropertiesConstants.DATA_CONTROLEDITOR_OF_CONTROL, swtObject);
				}
			}
		}
		for (String delayed : delayedAttributes) {
			initAttribute(metaclass, swtObject, element, null, delayed);
		}
		if (stacked) {
			popStack();
		}
		if (loadedObject != null && loadedMethod != null && widget != null) {
			Event event = new Event();
			event.doit = true;
			event.widget = widget;
			loadedMethod.invoke(loadedObject, new Object[] { event });
			loadedObject = null;
			loadedMethod = null;
			widget = null;
		}
		return swtObject;
	}

	private int getColumnIndex(Element columnElement) {
		String name = columnElement.getName();
		String namespace = columnElement.getNamespace();
		IMetaclass metaclass = XWT.getMetaclass(name, namespace);
		int index = -1;
		Class<?> type = metaclass.getType();
		if (type == TableViewerColumn.class) {
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
				TableEditor tableEditor = (TableEditor) doCreate(table, editor, null, -1);
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
						return doCreate(swtObject, (Element) documentObject, null, -1);
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

	protected void popStack() {
		if (stackCLR.isEmpty()) {
			return;
		}
		Object object = stackCLR.pop();
		Method method = ObjectUtil.findDeclaredMethod(object.getClass(), "initializeComponent");
		if (method == null) {
			method = ObjectUtil.findDeclaredMethod(object.getClass(), "InitializeComponent");
		}
		if (method != null) {
			try {
				method.setAccessible(true);
				method.invoke(object);
			} catch (Exception e) {
				LoggerManager.log(e);
			}
		}
	}

	private Integer getStyleValue(Element element, int styles) {
		Attribute attribute = element.getAttribute(IConstants.XWT_X_NAMESPACE, "Style");
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

	private boolean init(IMetaclass metaclass, Object targetObject, Element element, List<String> delayedAttributes) throws Exception {
		boolean stacked = false;

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
				if (loadCLR(className, targetObject)) {
					stacked = true;
				}
			}
		}

		// x:DataContext
		{
			Attribute dataContextAttribute = element.getAttribute("DataContext");
			if (dataContextAttribute != null) {
				IProperty property = metaclass.findProperty("DataContext");
				Widget composite = (Widget) targetObject;
				DocumentObject documentObject = dataContextAttribute.getChildren()[0];
				if (IConstants.XAML_STATICRESOURCES.equals(documentObject.getName()) || IConstants.XAML_DYNAMICRESOURCES.equals(documentObject.getName())) {
					String key = documentObject.getContent();
					property.setValue(composite, new StaticResourceBinding(composite, key));
				} else if (IConstants.XAML_BINDING.equals(documentObject.getName())) {
					Object object = doCreate(targetObject, (Element) documentObject, null, -1);
					property.setValue(composite, object);
				} else {
					LoggerManager.log(new UnsupportedOperationException(documentObject.getName()));
				}
			}
		}

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
			else
				initAttribute(metaclass, targetObject, element, null, attrName);
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
						Widget composite = (Widget) targetObject;
						ResourceDictionary dico = (ResourceDictionary) composite.getData(IUserDataConstants.XWT_RESOURCES_KEY);
						if (dico == null) {
							dico = new ResourceDictionary();
							composite.setData(IUserDataConstants.XWT_RESOURCES_KEY, dico);
						}

						Attribute attribute = element.getAttribute(namespace, attrName);
						for (DocumentObject doc : attribute.getChildren()) {
							Element elem = (Element) doc;
							Object doCreate = doCreate(composite, elem, null, -1);
							Attribute keyAttribute = elem.getAttribute(namespace, "Key");
							dico.put(keyAttribute.getContent(), doCreate);
						}
					} else {
						initAttribute(metaclass, targetObject, element, namespace, attrName);
					}
				}
				continue;
			}

			for (String attrName : element.attributeNames(namespace)) {
				if ("name".equalsIgnoreCase(attrName) && (targetObject instanceof Widget)) {
					continue;
				}
				initAttribute(metaclass, targetObject, element, namespace, attrName);
			}
		}

		return stacked;
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
							Control control = (Control) doCreate(((TableItem) swtObject).getParent(), (Element) doc, null, -1);
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
			boolean stacked = init(metaclass, instance, element, delayedAttributes);
			for (String delayed : delayedAttributes) {
				initAttribute(metaclass, instance, element, null, delayed);
			}

			if (stacked) {
				popStack();
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
				stackCLR.push(instance);
				if (currentObject instanceof Widget) {
					UserDataHelper.setCLR((Widget) currentObject, instance);
				}
			} else {
				stackCLR.push(currentObject);
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
		IProperty property = metaclass.findProperty(propertyName);
		setStyleProperty(target, element, metaclass);
		if (propertyName.trim().equals(IConstants.XAML_DATACONTEXT)) {
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
			Widget control = (Widget) target;
			Controller eventController = (Controller) control.getData(IUserDataConstants.XWT_CONTROLLER_KEY);
			if (eventController == null) {
				eventController = new Controller();
				control.setData(IUserDataConstants.XWT_CONTROLLER_KEY, eventController);
			}

			int last = stackCLR.size() - 1;
			Method method = null;
			Object clrObject = null;
			String methodName = attribute.getContent();
			for (int i = last; i >= 0; i--) {
				Object receiver = stackCLR.get(i);
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
						loadedObject = receiver;
						loadedMethod = method;
						loadedMethod.setAccessible(true);
						widget = control;
					}
					break;
				}
			}
			if (method == null) {
				LoggerManager.log(new XWTException("Event handler \"" + methodName + "\" is not found."));
			}
			eventController.setEvent(event, control, clrObject, method);
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
			Object value = null;
			List<Object> values = null;
			DocumentObject[] children = attribute.getChildren();
			if (contentValue == null) {
				Class<?> type = property.getType();
				if (Collection.class.isAssignableFrom(type)) {
					value = getCollectionProperty(type, target, attribute, attrName);
				} else {
					if (type == TableViewerColumn.class && attrName.equalsIgnoreCase("columns")) {
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
						} else if ("TableColumn".equalsIgnoreCase(element.getName()) && "TableEditor".equalsIgnoreCase(child.getName())) {
							value = child;
						} else if (property.getType() == TableViewerColumn.class && attribute.getContent() != null) {
							value = attribute.getContent();
						} else {
							value = doCreate(target, (Element) child, type, -1);
						}
					}
				}
			}
			if (contentValue != null && value == null && !"Command".equalsIgnoreCase(propertyName)) {
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
			} else if (values != null) {
				property.setValue(target, values.toArray());
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
								doCreate(value, (Element) child, null, -1);
							}
						}
					}
				}
			}

			if (attribute.attributeNames().length > 0) {
				IMetaclass propertyMetaclass = XWT.getMetaclass(property.getType());
				if (value == null) {
					value = property.getValue(target);
				}
				if (value != null) {
					List<String> delayedAttributes = new ArrayList<String>();
					boolean stacked = init(propertyMetaclass, value, attribute, delayedAttributes);
					for (String delayed : delayedAttributes) {
						initAttribute(metaclass, target, element, null, delayed);
					}

					if (stacked) {
						popStack();
					}
				}
			}
		} catch (Exception e) {
			LoggerManager.log(e);
		}
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
			if (strContent.contains("j:")) {
				strContent = removeSubString(strContent, "j:");
			}
			if (strContent.contains("java:")) {
				strContent = removeSubString(strContent, "java:");
			}
			StyleSetterMap setterMap = new StyleSetterMap(strContent);
			for (DocumentObject docs : element.getChildren()) {
				Element setter = (Element) docs;
				String setterProperty = setter.getAttribute("Property").getContent();
				String setterValue = setter.getAttribute("Value").getContent();
				setterMap.put(setterProperty, setterValue);
			}
			dico.put(strContent, setterMap);
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
		if (stackCLR.isEmpty()) {
			return null;
		}
		return stackCLR.peek();
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

	private String getCurrentWidgetPosition(Element element) {

		if (element.getParent() != null) {
			return " " + getCurrentWidgetPosition((Element) element.getParent());
		}
		return "";
	}

	private int validateParentElementError(Element bindingElement) {
		int isError = 0;
		if (!bindingError.equals("")) {
			Object parentObj = bindingElement.getParent();
			while (parentObj != null) {
				bindingElement = (Element) parentObj;
				if (errorElements.contains(bindingElement.getId())) {
					isError = 1;
					break;
				}
				parentObj = bindingElement.getParent();
			}
		}
		return isError;
	}

	private Object getObserveData(Object dataContext, String path) {
		try {
			Class<?> dataContextClass = dataContext.getClass();
			String getMethiodName = "get" + path.substring(0, 1).toUpperCase() + path.substring(1);
			Method getMethod = dataContextClass.getDeclaredMethod(getMethiodName, new Class[] {});
			if (getMethod != null) {
				return getMethod.invoke(dataContext, new Object[] {});
			}
		} catch (SecurityException e) {
			LoggerManager.log(e);
		} catch (NoSuchMethodException e) {
			LoggerManager.log(e);
		} catch (IllegalArgumentException e) {
			LoggerManager.log(e);
		} catch (IllegalAccessException e) {
			LoggerManager.log(e);
		} catch (InvocationTargetException e) {
			LoggerManager.log(e);
		}
		return null;
	}

	private String getDataBindMessage() {
		StringBuffer message = new StringBuffer("");
		Iterator<Element> widgetIt = widgetList.iterator();
		Set<Element> keys = bindingMap.keySet();
		while (widgetIt.hasNext()) {
			Element element = widgetIt.next();
			int parentHasError = validateParentElementError(element);
			if (parentHasError == 0) {
				String content = getCurrentWidgetPosition(element);
				if (!message.toString().equals("")) {
					content += "+";
				}
				content = content + element.getName();
				message.append(content);
				if (keys.contains(element)) {
					message.append(bindingMap.get(element));
				}
			}
		}
		return message.toString();
	}
}
