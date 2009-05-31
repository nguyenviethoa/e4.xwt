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
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.internal.jface.JFacesHelper;
import org.eclipse.e4.xwt.javabean.metadata.properties.BeanProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.FieldProperty;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * @author xye (xiaowei.ye@soyatec.com)
 */
public class AbstractMetaclass implements IMetaclass {

	public static IProperty[] EMPTY_PROPERTIES = new IProperty[0];
	public static IEvent[] EMPTY_ROUTED_EVENTS = new IEvent[0];

	protected final Map<String, IProperty> propertyCache = new HashMap<String, IProperty>();
	protected Map<String, IEvent> routedEventCache = new HashMap<String, IEvent>();

	public static final String LOADED = "Loaded";

	protected Class<?> type;
	protected String name;
	protected IMetaclass superClass;

	protected XWTLoader xwtLoader;
	
	protected boolean buildTypedEvents;

	private boolean initialize = false;

	protected boolean shouldIgnored(Class<?> type, String propertyName, Class<?> propertyType) {
		String packageName = "";
		if (type.getPackage() != null) {
			packageName = type.getPackage().getName();
		}
		if (("data".equals(propertyName) && packageName.startsWith("org.eclipse.swt."))) {
			return true;
		}
		if ("class".equals(propertyName)) {
			return true;
		}
		if (("handle".equals(propertyName) && int.class == propertyType) || ("monitor".equals(propertyName) && Monitor.class == propertyType) || ("region".equals(propertyName) && Region.class == propertyType) || ("parent".equals(propertyName) && Composite.class == propertyType) || ("shell".equals(propertyName) && Shell.class == propertyType) || ("display".equals(propertyName) && Display.class == propertyType)) {
			return true;
		}
		return false;
	}

	protected boolean isWidgetType(Class<?> type) {
		Class<?> superClass = type.getSuperclass();
		if (superClass != null) {
			if (superClass.getName().equalsIgnoreCase(Widget.class.getName())) {
				return true;
			} else {
				return isWidgetType(superClass);
			}
		}
		return false;
	}

	protected final XWTLoader getXWTLoader()
	{
		return xwtLoader;
	}
	
	public IProperty addProperty(IProperty property) {
		String name = normalize(property.getName());
		return propertyCache.put(name, property);
	}

	private void buildTypedEvents() {
		if (buildTypedEvents) {
			return;
		}
		if (isSubclassOf(getXWTLoader().getMetaclass(Widget.class))) {
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
			// addTypedEvent ("Settings", SWT.Settings); // note: this event
			// only goes to Display
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
		assertInitialize();
		buildTypedEvents();
		return routedEventCache.get(normalize(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#findProperty(java.lang.String)
	 */
	public IProperty findProperty(String name) {
		assertInitialize();
		IProperty property = propertyCache.get(normalize(name));
		if (property == null && superClass != null) {
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

	protected String normalize(String name) {
		return name.toLowerCase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#getEvents()
	 */
	public IEvent[] getEvents() {
		assertInitialize();
		buildTypedEvents();
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
		assertInitialize();
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
		assertInitialize();
		if (metaclass == null) {
			return false;
		}
		if (this == metaclass) {
			return true;
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
		assertInitialize();
		try {
			if (parameters.length == 0 || (!(parameters[0] instanceof Widget || JFacesHelper.isViewer(parameters[0])))) {
				return getType().newInstance();
			}
		} catch (Exception e1) {
		}

		try {

			Object swtObject = null;
			Object parent = parameters[0];
			Widget directParent = null;
			if (parent instanceof Widget) {
				directParent = (Widget) parent;
			} else if (JFacesHelper.isViewer(parent)) {
				directParent = JFacesHelper.getControl(parent);
			} else
				throw new IllegalStateException();
			if (Control.class.isAssignableFrom(getType()) && !(directParent instanceof Composite)) {
				directParent = getXWTLoader().findCompositeParent(directParent);
			}

			Object styleValue = null;
			if (parameters.length == 2 && parameters[1] != null && (parameters[1].getClass() == int.class || parameters[1].getClass() == Integer.class)) {
				styleValue = parameters[1];
			}

			Constructor<?> defaultConstructor = null;

			for (Constructor<?> constructor : getType().getConstructors()) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if (parameterTypes.length > 2 || parameterTypes.length == 0) {
					if (parameterTypes.length == 0) {
						defaultConstructor = constructor;
					}
					continue;
				}

				if (parameterTypes[0].isAssignableFrom(parent.getClass())) {
					if (parameterTypes.length == 1) {
						swtObject = constructor.newInstance(new Object[] { parent });
						break;
					} else if (parameterTypes[1].isAssignableFrom(int.class)) {
						if (styleValue == null)
							swtObject = constructor.newInstance(new Object[] { parent, 0 });
						else
							swtObject = constructor.newInstance(new Object[] { parent, styleValue });
						break;
					}
				}
			}

			if (swtObject == null) {
				for (Constructor<?> constructor : getType().getConstructors()) {
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

	public Class<?> getDataContextType() {
		return Object.class;
	}

	private void assertInitialize() {
		initialize(type, superClass);
	}

	protected void initialize(Class<?> type, IMetaclass superClass) {
		if (isInitialize()) {
			return;
		}
		try {
			BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor p : propertyDescriptors) {
				String propertyName = p.getName();
				Class<?> propertyType = p.getPropertyType();
				if (shouldIgnored(type, propertyName, propertyType)) {
					continue;
				}
				if (p.getPropertyType() != null) {
					IProperty property = (superClass != null ? superClass.findProperty(p.getName()) : null);
					if (property != null && !property.isDefault()) {
						addProperty(property);
					} else {
						if (p.getWriteMethod() != null || !p.getPropertyType().isPrimitive()) {
							addProperty(new BeanProperty(p));
						}
					}
				}
			}
			for (Field f : type.getDeclaredFields()) {
				String propertyName = f.getName();
				Class<?> propertyType = f.getType();
				if (shouldIgnored(type, propertyName, propertyType)) {
					continue;
				}

				if (!propertyCache.containsKey(normalize(propertyName)) && !Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
					addProperty(new FieldProperty(f));
				}
			}

			for (EventSetDescriptor eventSetDescriptor : beanInfo.getEventSetDescriptors()) {
				BeanEvent event = new BeanEvent(eventSetDescriptor.getName(), eventSetDescriptor);
				routedEventCache.put(normalize(eventSetDescriptor.getName() + "Event"), event);
			}
			if (isWidgetType(type)) {
				routedEventCache.put(normalize(LOADED), new LoadedEvent(LOADED));
			}

			markInitialized();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void markInitialized() {
		initialize = true;
	}

	private boolean isInitialize() {
		return initialize;
	}
}
