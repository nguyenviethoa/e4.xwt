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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoaderManager;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.tools.ui.designer.swt.CoolBarHelper;
import org.eclipse.e4.xwt.tools.ui.designer.swt.SWTTools;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTModelUtil;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTUtility;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class XWTProxy {

	public static final Point DEFAULT_SIZE = new Point(200, 100);

	private final ResourceVisitor resourceVisitor;
	private Shell device;

	private Map<XamlNode, Object> componentsMap = new HashMap<XamlNode, Object>();

	private Object rootComponent = null;
	private Map<String, Object> options;

	public XWTProxy(IFile file) {
		// Register XWTVisualLoader when the designer is initialized, so we can share to use this Loader.
		IXWTLoader xwtLoader = XWTLoaderManager.getActive();
		if (xwtLoader == null || !(xwtLoader instanceof XWTVisualLoader)) {
			xwtLoader = new XWTVisualLoader(file);
			XWTLoaderManager.setActive(xwtLoader, true);
		}
		resourceVisitor = new ResourceVisitor((XWTVisualLoader) xwtLoader);
		options = new HashMap<String, Object>();
		options.put(IXWTLoader.DESIGN_MODE_ROPERTY, Boolean.TRUE);
	}

	public Object load(XamlElement node, Map<String, Object> options) {
		Object result = resourceVisitor.createCLRElement(node, options);
		if (result instanceof Widget) {
			buildComponentMap((Widget) result);
		}
		return result;
	}

	public Object load(XamlDocument document) {
		if (document == null) {
			return null;
		}
		XamlElement element = document.getRootElement();
		if (element == null) {
			return null;
		}
		if (device == null || device.isDisposed()) {
			device = new Shell(SWT.NO_TRIM);
		}
		device.setSize(0, 0);
		componentsMap.clear();
		if (!"shell".equalsIgnoreCase(element.getName())) {
			Shell shell = new Shell(device);
			Point offset = SWTTools.getOffset(shell);
			shell.setLocation(-offset.x, -offset.y);
			shell.setSize(DEFAULT_SIZE);
			options.put(IXWTLoader.CONTAINER_PROPERTY, shell);
		} else {
			options.put(IXWTLoader.CONTAINER_PROPERTY, device);
		}
		rootComponent = load(element, options);
		if (rootComponent == null) {
			return null;
		}
		Control control = null;
		if (rootComponent instanceof Shell) {
			Shell shell = (Shell) rootComponent;
			// 1.location
			XamlAttribute bounds = element.getAttribute("bounds");
			XamlAttribute location = element.getAttribute("location");
			if (bounds == null && location == null) {
				shell.setLocation(0, 0);
			}
		}
		if (rootComponent instanceof Control) {
			control = (Control) rootComponent;
			layout(control);
		} else if (rootComponent instanceof Viewer) {
			control = ((Viewer) rootComponent).getControl();
		}
		layout(control);
		// control.getShell().open();
		device.open();
		device.setVisible(false);
		buildComponentMap(rootComponent);
		return rootComponent;
	}

	public void layout(Control control) {
		if (control == null || control.isDisposed() || control == device) {
			return;
		}
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			Layout layout = composite.getLayout();
			if (layout != null) {
				composite.layout(true, true);
			} /*
			 * else { control.pack(); }
			 */
			Control[] childs = composite.getChildren();
			for (int i = 0; i < childs.length; i++) {
				if (childs[i] instanceof CoolBar)
					CoolBarHelper.layout((CoolBar) childs[i]);
			}
		}
		if (control instanceof CoolBar) {
			CoolBarHelper.layout((CoolBar) control);
		}
		if (control == rootComponent && autoSize(getModel(control))) {
			Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			if (control instanceof Composite) {
				Composite composite = (Composite) control;
				if (composite.getChildren().length == 0) {
					size.x = Math.max(size.x, DEFAULT_SIZE.x);
					size.y = Math.max(size.y, DEFAULT_SIZE.y);
				}
			}
			control.setSize(size);

			if (control instanceof Composite) {

				((Composite) control).layout();

			}
		}
		layout(control.getParent());
	}

	public Object getRoot() {
		return rootComponent;
	}

	protected boolean autoSize(XamlNode xamlNode) {
		if (xamlNode == null) {
			return false;
		}
		XamlAttribute bounds = xamlNode.getAttribute("bounds");
		if (bounds == null) {
			bounds = xamlNode.getAttribute("bounds", IConstants.XWT_NAMESPACE);
		}
		XamlAttribute size = xamlNode.getAttribute("size");
		if (size == null) {
			size = xamlNode.getAttribute("size", IConstants.XWT_NAMESPACE);
		}
		return bounds == null && size == null;
	}

	public boolean shouldPack(XamlElement element) {
		return autoSize(element) && !element.getChildNodes().isEmpty();
	}

	public Object createWidget(Object parent, XamlElement node) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(IXWTLoader.CONTAINER_PROPERTY, parent);
		return load(node, options);
	}

	// public void orderParent(Widget widget) {
	// Widget parent = null;
	// if (widget instanceof Control) {
	// parent = ((Control) widget).getParent();
	// } else if (widget instanceof Item) {
	// try {
	// Method getParentMethod = widget.getClass().getDeclaredMethod("getParent", new Class<?>[0]);
	// Object obj = getParentMethod.invoke(widget, new Object[0]);
	// if (obj != null && obj instanceof Widget) {
	// parent = (Widget) obj;
	// }
	// } catch (Exception e) {
	// }
	// }
	// if (parent != null) {
	// WidgetOrder.order(parent);
	// }
	// }

	public Object getComponent(EObject node) {
		return componentsMap.get(node);
	}

	public Object getComponent(EObject node, boolean loadOnDemand) {
		Object component = getComponent(node);
		if (!loadOnDemand) {
			return component;
		} else if (isNull(component)) {
			EObject container = node.eContainer();
			while (container != null && !(container instanceof XamlElement)) {
				container = container.eContainer();
			}
			if (container != null && container instanceof XamlElement) {
				Object parentComponent = getComponent(container, loadOnDemand);
				if (!isNull(parentComponent)) {
					component = createWidget(parentComponent, (XamlElement) node);
				}
			}
		}
		return component;
	}

	public boolean isNull(Object component) {
		if (component == null) {
			return true;
		} else if (component instanceof Widget) {
			return ((Widget) component).isDisposed();
		} else if (component instanceof Viewer) {
			return ((Viewer) component).getControl() == null || ((Viewer) component).getControl().isDisposed();
		}
		return false;
	}

	private void buildComponentMap(Object component) {
		if (component == null) {
			return;
		}
		XamlNode key = null;
		Widget widget = null;
		if (component instanceof Viewer) {
			widget = ((Viewer) component).getControl();
			key = (XamlNode) ((Viewer) component).getData(ResourceVisitor.ELEMENT_KEY);
			if (key != null) {
				componentsMap.put(key, component);
				key = XWTModelUtil.getAdaptableAttribute(key, "control", IConstants.XWT_NAMESPACE);
			}
		} else if (component instanceof Widget) {
			widget = (Widget) component;
			key = (XamlNode) widget.getData(ResourceVisitor.ELEMENT_KEY);
		}
		if (key != null) {
			componentsMap.put(key, widget);
		}
		if (widget != null && !widget.isDisposed()) {
			Widget[] children = SWTTools.getChildren(widget);
			for (Widget child : children) {
				Object viewer = UserData.getLocalViewer(child);
				if (viewer != null) {
					buildComponentMap(viewer);
				} else {
					buildComponentMap(child);
				}
			}
		}
	}

	public Object doCreate(XamlElement element) {
		try {
			return resourceVisitor.doCreate(null, element, null, Collections.EMPTY_MAP);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param attrValue
	 * @return
	 */
	public static Object createValue(XamlElement element) {
		return new XWTProxy(null).doCreate(element);
	}

	public boolean isDisposed() {
		return device == null || device.isDisposed();
	}

	public boolean reset() {
		return dispose();
	}

	public boolean dispose() {
		return destroy(rootComponent) && destroy(device);
	}

	public boolean destroy(Object component) {
		if (component == null) {
			return false;
		} else if (component instanceof Widget) {
			Widget widget = (Widget) component;
			XamlNode model = getModel(widget);
			if (model != null) {
				componentsMap.remove(model);
			}
			if (widget instanceof Menu) {
				Decorations parent = ((Menu) widget).getParent();
				if (parent instanceof Shell) {
					Menu menuBar = ((Shell) parent).getMenuBar();
					if (menuBar == widget) {
						((Shell) parent).setMenuBar(null);
					}
				}
			}
			widget.dispose();
		} else if (component instanceof Viewer) {
			destroy(((Viewer) component).getControl());
		}
		component = null;
		return true;
	}

	/**
	 * This method only deal with general attribute. The style attribute is not its duty.
	 * 
	 * @param object
	 * @param attribute
	 */
	public boolean removeValue(Object object, XamlAttribute attribute) {
		// TODO: At present, only the properties of a Widget Object can be removed(replaced with a default one). We should take care of JFace Viewers.
		if (!(object instanceof Widget) && "style".equals(attribute.getName())) {
			return false;
		}
		IMetaclass metaclass = XWT.getMetaclass(object);
		if (metaclass == null) {
			return false;
		}
		Object defaultValue = getDefaultValue(object, attribute);
		try {
			IProperty[] properties = metaclass.getProperties();
			for (IProperty property : properties) {
				if (property.getName().equals(attribute.getName())) {
					property.setValue(object, defaultValue);
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public Object getDefaultValue(Object object, XamlAttribute attribute) {
		if (object == null || attribute == null || attribute.getName() == null) {
			return null;
		}
		String name = attribute.getName();
		XamlNode model = null;
		if (object instanceof Widget) {
			model = getModel((Widget) object);
		}
		if (model == null) {
			EObject container = attribute.eContainer();
			if (container instanceof XamlElement) {
				model = (XamlElement) container;
			}
		}
		if (model == null) {
			return null;
		}
		XamlElement newModel = (XamlElement) EcoreUtil.copy(model);
		IMetaclass metaclass = XWTUtility.getMetaclass(newModel);
		if (metaclass == null || !Control.class.isAssignableFrom(metaclass.getType())) {
			return null;
		}
		Shell shell = new Shell(device);
		Object tempObj = createWidget(shell, (XamlElement) EcoreUtil.copy(model));
		if (tempObj == null || !(tempObj instanceof Widget)) {
			return false;
		}
		try {
			if (tempObj instanceof Widget && "style".equalsIgnoreCase(name) && IConstants.XWT_X_NAMESPACE.endsWith(attribute.getNamespace())) {
				return ((Widget) tempObj).getStyle();
			}
			IProperty p = metaclass.findProperty(name);
			if (p != null) {
				return p.getValue(tempObj);
			}
		} catch (Exception e) {
		} finally {
			destroy(tempObj);
			destroy(shell);
		}
		return null;
	}

	public boolean initAttribute(Object component, XamlAttribute attribute) {
		try {
			XamlElement container = (XamlElement) attribute.eContainer();
			IMetaclass metaclass = XWTUtility.getMetaclass(container);
			String attrName = attribute.getName();
			String namespace = attribute.getNamespace();
			if (container != null && metaclass != null && attrName != null) {
				resourceVisitor.initAttribute(metaclass, component, container, namespace, attrName);
				buildComponentMap(component);
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getClr() {
		if (rootComponent == null) {
			return null;
		}
		Widget widget = null;
		if (rootComponent instanceof Widget) {
			widget = (Widget) rootComponent;
		} else if (rootComponent instanceof Viewer) {
			widget = ((Viewer) rootComponent).getControl();
		}
		if (widget != null && !widget.isDisposed()) {
			Object clr = UserData.getCLR(widget);
			if (clr != null) {
				return clr.getClass().getName();
			}
		}
		return null;
	}

	public static XamlNode getModel(Object component) {
		if (component == null) {
			return null;
		}
		if (component instanceof Widget) {
			return (XamlNode) ((Widget) component).getData(ResourceVisitor.ELEMENT_KEY);
		} else if (component instanceof Viewer) {
			return (XamlNode) ((Viewer) component).getData(ResourceVisitor.ELEMENT_KEY);
		}
		return null;
	}

	public boolean removeLayout(Composite composite) {
		if (composite == null || composite.isDisposed()) {
			return false;
		}
		composite.setLayout(null);
		for (Control control : composite.getChildren()) {
			control.setLayoutData(null);
		}
		return true;
	}

	public boolean recreate(Widget widget, boolean fromParent) {
		if (widget == null || widget.isDisposed()) {
			return false;
		}
		XamlNode model = getModel(widget);
		if (model == null) {
			return false;
		}
		if (widget == rootComponent) {
			destroy(widget);
			componentsMap.clear();
			return load(model.getOwnerDocument()) != null;
		}
		// recreate by using ResourceVistor.
		EObject parentModel = model.eContainer();
		Object parent = getComponent(parentModel);
		if (fromParent) {
			if (parent == null) {
				return false;
			} else if (parent instanceof Widget) {
				return recreate((Widget) parent, false);
			}
		} else if (parent != null && model instanceof XamlElement) {
			try {
				destroy(widget);
				Widget newWidget = (Widget) createWidget(parent, (XamlElement) model);
				if (newWidget instanceof Control) {
					layout((Control) newWidget);
				}
				if (parent instanceof Widget) {
					((Widget) parent).getDisplay().update();
				}
				if (parent instanceof Control) {
					layout((Control) parent);
				}
				return true;
			} catch (Exception e) {
			}
		} else if (parentModel != null) {
			// If parentModel is a XamlAttribute, we need to retrieve a parent widget for recreating.
			parentModel = parentModel.eContainer();
			parent = getComponent(parentModel);
			while (parent == null) {
				if (parentModel == null) {
					break;
				}
				parentModel = parentModel.eContainer();
				parent = getComponent(parentModel);
			}
			if (parent != null && parent instanceof Widget) {
				return recreate((Widget) parent, false);
			}
		}

		return false;
	}
}
