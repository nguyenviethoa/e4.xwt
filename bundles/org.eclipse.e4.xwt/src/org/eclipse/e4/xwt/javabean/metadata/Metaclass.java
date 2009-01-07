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
package org.eclipse.e4.xwt.javabean.metadata;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.e4.xwt.utils.JFacesHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class Metaclass implements IMetaclass {
	public static IProperty[] EMPTY_PROPERTIES = new IProperty[0];
	public static IEvent[] EMPTY_ROUTED_EVENTS = new IEvent[0];

	protected final Map<String, IProperty> propertyCache = new HashMap<String, IProperty>();
	protected Map<String, IEvent> routedEventCache = new HashMap<String, IEvent>();

	private Class<?> type;
	private String name;
	private IMetaclass superClass;

	private boolean buildTypedEvents;

	public Metaclass(Class<?> type, IMetaclass superClass) {
		this.type = type;
		this.name = type.getSimpleName();
		this.superClass = superClass;

		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor p : propertyDescriptors) {
				if (p.getPropertyType() != null)
					addProperty(new BeanProperty(p));
			}
			for (Field f : type.getDeclaredFields()) {
				if (!propertyCache.containsKey(normalize(f.getName())) && !Modifier.isFinal(f.getModifiers())) {
					addProperty(new BeanProperty(f));
				}
			}

			for (EventSetDescriptor eventSetDescriptor : beanInfo.getEventSetDescriptors()) {
				BeanEvent event = new BeanEvent(eventSetDescriptor.getName(), eventSetDescriptor);
				routedEventCache.put(normalize(eventSetDescriptor.getName() + "Event"), event);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IProperty addProperty(IProperty p) {
		return propertyCache.put(normalize(p.getName()), p);
	}

	public IProperty addArrayProperty(IProperty p) {
		if (p.getType() != null && p.getType().isArray()) {
			String arrayProp = normalize(p.getName() + "array");
			if (!propertyCache.containsKey(arrayProp)) {
				return propertyCache.put(arrayProp, p);
			}
		}
		return p;
	}

	public IProperty getArrayProperty(IProperty property) {
		Class<?> t = property.getClass();
		IProperty arrayProp = null;
		if (t == null || !t.isArray()) {
			String arrayPropName = normalize(property.getName() + "array");
			arrayProp = propertyCache.get(arrayPropName);
		}
		if (arrayProp == null) {
			arrayProp = property;
		}
		return arrayProp;
	}

	private void buildTypedEvents() {
		if (buildTypedEvents) {
			return;
		}
		if (isSubclassOf(XWT.getMetaclass(Widget.class))) {
			addTypedEvent("Activate", SWT.Activate);
			addTypedEvent("Arm", SWT.Arm);
			addTypedEvent("Close", SWT.Close);
			addTypedEvent("Collapse", SWT.Collapse);
			addTypedEvent("Deactivate", SWT.Deactivate);
			addTypedEvent("DefaultSelection", SWT.DefaultSelection);
			addTypedEvent("Deiconify", SWT.Deiconify);
			addTypedEvent("Dispose", SWT.Dispose);
			addTypedEvent("DragDetect", SWT.DragDetect);
			addTypedEvent("EraseItem", SWT.EraseItem);
			addTypedEvent("Expand", SWT.Expand);
			addTypedEvent("FocusIn", SWT.FocusIn);
			addTypedEvent("FocusOut", SWT.FocusOut);
			addTypedEvent("HardKeyDown", SWT.HardKeyDown);
			addTypedEvent("HardKeyUp", SWT.HardKeyUp);
			addTypedEvent("Help", SWT.Help);
			addTypedEvent("Hide", SWT.Hide);
			addTypedEvent("Iconify", SWT.Iconify);
			addTypedEvent("KeyDown", SWT.KeyDown);
			addTypedEvent("KeyUp", SWT.KeyUp);
			addTypedEvent("MeasureItem", SWT.MeasureItem);
			addTypedEvent("MenuDetect", SWT.MenuDetect);
			addTypedEvent("Modify", SWT.Modify);
			addTypedEvent("MouseDoubleClick", SWT.MouseDoubleClick);
			addTypedEvent("MouseDown", SWT.MouseDown);
			addTypedEvent("MouseEnter", SWT.MouseEnter);
			addTypedEvent("MouseExit", SWT.MouseExit);
			addTypedEvent("MouseHover", SWT.MouseHover);
			addTypedEvent("MouseMove", SWT.MouseMove);
			addTypedEvent("MouseUp", SWT.MouseUp);
			addTypedEvent("MouseWheel", SWT.MouseWheel);
			addTypedEvent("Move", SWT.Move);
			addTypedEvent("Paint", SWT.Paint);
			addTypedEvent("PaintItem", SWT.PaintItem);
			addTypedEvent("Resize", SWT.Resize);
			addTypedEvent("Selection", SWT.Selection); // sash
			addTypedEvent("SetData", SWT.SetData);
			// addTypedEvent ("Settings", SWT.Settings); // note: this event only goes to Display
			addTypedEvent("Show", SWT.Show);
			addTypedEvent("Traverse", SWT.Traverse);
			addTypedEvent("Verify", SWT.Verify);
			addTypedEvent("ImeComposition", SWT.ImeComposition);
		}
		buildTypedEvents = true;
	}

	private void addTypedEvent(String name, int eventType) {
		if (!routedEventCache.containsKey(normalize(name + "Event"))) {
			routedEventCache.put(normalize(name + "Event"), new TypedEvent(name, eventType));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#findDefaultProperty()
	 */
	public IProperty findDefaultProperty() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#findEvent(java.lang.String)
	 */
	public IEvent findEvent(String name) {
		buildTypedEvents();
		return routedEventCache.get(normalize(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#findProperty(java.lang.String)
	 */
	public IProperty findProperty(String name) {
		IProperty property = propertyCache.get(normalize(name));
		if(property == null && superClass != null){
			property = superClass.findProperty(name);
		}
		if (property == null) {
			try {
				Method getter = DynamicProperty.createGetter(type, name);
				Class<?> propertyType = getter.getReturnType();
				Method setter = DynamicProperty.createSetter(type, propertyType, name);
				return new DynamicProperty(propertyType, setter, getter, name);
			} catch (Exception e) {
				return null;
			}

		}
		return property;
	}

	private String normalize(String name) {
		return name.toLowerCase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getEvents()
	 */
	public IEvent[] getEvents() {
		return routedEventCache.values().toArray(new IEvent[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getProperties()
	 */
	public IProperty[] getProperties() {
		return propertyCache.values().toArray(new IProperty[propertyCache.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getSuperClass()
	 */
	public IMetaclass getSuperClass() {
		return superClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getType()
	 */
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#isAbstract()
	 */
	public boolean isAbstract() {
		return Modifier.isAbstract(type.getModifiers());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#isAssignableFrom(com.soyatec .xaswt.core.metadata.IMetaclass)
	 */
	public boolean isAssignableFrom(IMetaclass metaclass) {
		return getType().isAssignableFrom(metaclass.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#isInstance(java.lang.Object)
	 */
	public boolean isInstance(Object object) {
		return type.isInstance(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#isSubclassOf(com.soyatec.xaswt .core.metadata.IMetaclass)
	 */
	public boolean isSubclassOf(IMetaclass metaclass) {
		if (metaclass == null) {
			return false;
		}
		if (superClass == metaclass) {
			return true;
		}
		if (superClass != null) {
			return superClass.isSubclassOf(metaclass);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#isSuperclassOf(com.soyatec .xaswt.core.metadata.IMetaclass)
	 */
	public boolean isSuperclassOf(IMetaclass metaclass) {
		return metaclass.isSubclassOf(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#newInstance()
	 */
	public Object newInstance(Object[] parameters) {
		try {
			if (parameters.length == 0 || !(parameters[0] instanceof Widget)) {
				return getType().newInstance();
			}
		} catch (Exception e1) {
		}

		try {

			Object swtObject = null;
			Widget parent = null;
			Widget directParent = null;
			if (parameters[0] instanceof Widget) {
				directParent = parent = (Widget) parameters[0];
			} else if (JFacesHelper.isViewer(parameters[0])) {
				directParent = parent = JFacesHelper.getControl(parameters[0]);

			} else
				throw new IllegalStateException();
			if (Control.class.isAssignableFrom(getType()) && !(parent instanceof Composite)) {
				directParent = XWT.findCompositeParent(parent);
			}

			Object styleValue = null;
			if (parameters.length == 2 && parameters[1] != null && (parameters[1].getClass() == int.class || parameters[1].getClass() == Integer.class)) {
				styleValue = parameters[1];
			}

			Constructor defaultConstructor = null;
			for (Constructor constructor : getType().getConstructors()) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if (parameterTypes.length > 2 || parameterTypes.length == 0) {
					if (parameterTypes.length == 0) {
						defaultConstructor = constructor;
					}
					continue;
				}

				if (parameterTypes[0].isAssignableFrom(directParent.getClass())) {
					if (parameterTypes.length == 1) {
						swtObject = constructor.newInstance(new Object[] { directParent });
						break;
					} else if (parameterTypes[1].isAssignableFrom(int.class)) {
						if (styleValue == null)
							swtObject = constructor.newInstance(new Object[] { directParent, 0 });
						else
							swtObject = constructor.newInstance(new Object[] { directParent, styleValue });
						break;
					}
				}
			}
			if (swtObject == null) {
				if (defaultConstructor == null) {
					throw new UnsupportedOperationException("Constructor no found.");
				}
				swtObject = defaultConstructor.newInstance();
			}
			return swtObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
