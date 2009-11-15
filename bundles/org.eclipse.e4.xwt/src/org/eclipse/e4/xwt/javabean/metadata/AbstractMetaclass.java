package org.eclipse.e4.xwt.javabean.metadata;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IEventGroup;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.core.IBinding;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.e4.xwt.javabean.metadata.properties.BeanProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.DynamicProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.EventProperty;
import org.eclipse.e4.xwt.javabean.metadata.properties.FieldProperty;
import org.eclipse.e4.xwt.jface.JFacesHelper;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.metadata.IObjectInitializer;
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
public abstract class AbstractMetaclass implements IMetaclass {
	public static IObjectInitializer[] EMPTY_INITIALIZERS = new IObjectInitializer[0];

	public static IProperty[] EMPTY_PROPERTIES = new IProperty[0];
	public static IEvent[] EMPTY_ROUTED_EVENTS = new IEvent[0];

	protected final Map<String, IProperty> propertyCache = new HashMap<String, IProperty>();
	protected Map<String, IEvent> routedEventCache = new HashMap<String, IEvent>();
	protected Map<String, IEventGroup> eventGroupCache = Collections.EMPTY_MAP;

	protected Class<?> type;
	protected String name;
	protected IMetaclass superClass;

	protected IXWTLoader xwtLoader;

	protected boolean buildTypedEvents;

	private boolean initialize = false;
	private IObjectInitializer[] initializers = EMPTY_INITIALIZERS;

	protected boolean shouldIgnored(Class<?> type, String propertyName,
			Class<?> propertyType) {
		String packageName = "";
		if (type.getPackage() != null) {
			packageName = type.getPackage().getName();
		}
		if (("data".equals(propertyName) && packageName
				.startsWith("org.eclipse.swt."))) {
			return true;
		}
		if ("class".equals(propertyName)) {
			return true;
		}
		if (("handle".equals(propertyName) && int.class == propertyType)
				|| ("monitor".equals(propertyName) && Monitor.class == propertyType)
				|| ("region".equals(propertyName) && Region.class == propertyType)
				|| ("parent".equals(propertyName) && Composite.class == propertyType)
				|| ("shell".equals(propertyName) && Shell.class == propertyType)
				|| ("display".equals(propertyName) && Display.class == propertyType)) {
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

	protected final IXWTLoader getXWTLoader() {
		return xwtLoader;
	}

	public IProperty addProperty(IProperty property) {
		String name = normalize(property.getName());
		return propertyCache.put(name, property);
	}
	
	public void removeProperty(String propertyName) {
		String name = normalize(propertyName);
		propertyCache.remove(name);
	}


	private void buildTypedEvents() {
		if (buildTypedEvents) {
			return;
		}
		if (isSubclassOf(getXWTLoader().getMetaclass(Widget.class))) {
			addTypedEvent(IEventConstants.ACTIVATE, SWT.Activate);
			addTypedEvent(IEventConstants.ARM, SWT.Arm);
			addTypedEvent(IEventConstants.CLOSE, SWT.Close);
			addTypedEvent(IEventConstants.COLLAPSE, SWT.Collapse);
			addTypedEvent(IEventConstants.DEACTIVATE, SWT.Deactivate);
			addTypedEvent(IEventConstants.DEFAULT_SELECTION,
					SWT.DefaultSelection);
			addTypedEvent(IEventConstants.DEICONIFY, SWT.Deiconify);
			addTypedEvent(IEventConstants.DISPOSE, SWT.Dispose);
			addTypedEvent(IEventConstants.DRAG_SELECT, SWT.DragDetect);
			addTypedEvent(IEventConstants.ERASE_ITEM, SWT.EraseItem);
			addTypedEvent(IEventConstants.EXPAND, SWT.Expand);
			addTypedEvent(IEventConstants.FOCUS_IN, SWT.FocusIn);
			addTypedEvent(IEventConstants.FOCUS_OUT, SWT.FocusOut);
			addTypedEvent(IEventConstants.HARD_KEY_DOWN, SWT.HardKeyDown);
			addTypedEvent(IEventConstants.HARD_KEY_UP, SWT.HardKeyUp);
			addTypedEvent(IEventConstants.HELP, SWT.Help);
			addTypedEvent(IEventConstants.HIDE, SWT.Hide);
			addTypedEvent(IEventConstants.ICONIFY, SWT.Iconify);
			addTypedEvent(IEventConstants.KEY_DOWN, SWT.KeyDown);
			addTypedEvent(IEventConstants.KEY_UP, SWT.KeyUp);
			addTypedEvent(IEventConstants.MEASURE_ITEM, SWT.MeasureItem);
			addTypedEvent(IEventConstants.MENU_DETECT, SWT.MenuDetect);
			addTypedEvent(IEventConstants.MODIFY, SWT.Modify);
			addTypedEvent(IEventConstants.MOUSE_DOUBLE_CLICK,
					SWT.MouseDoubleClick);
			addTypedEvent(IEventConstants.MOUSE_DOWN, SWT.MouseDown);
			addTypedEvent(IEventConstants.MOUSE_ENTER, SWT.MouseEnter);
			addTypedEvent(IEventConstants.MOUSE_EXIT, SWT.MouseExit);
			addTypedEvent(IEventConstants.MOUSE_HOVER, SWT.MouseHover);
			addTypedEvent(IEventConstants.MOUSE_MOVE, SWT.MouseMove);
			addTypedEvent(IEventConstants.MOUSE_UP, SWT.MouseUp);
			addTypedEvent(IEventConstants.MOUSE_WHEEL, SWT.MouseWheel);
			addTypedEvent(IEventConstants.MOVE, SWT.Move);
			addTypedEvent(IEventConstants.PAINT, SWT.Paint);
			addTypedEvent(IEventConstants.PAINT_ITEM, SWT.PaintItem);
			addTypedEvent(IEventConstants.RESIZE, SWT.Resize);
			addTypedEvent(IEventConstants.SELECTION, SWT.Selection); // sash
			addTypedEvent(IEventConstants.SET_DATA, SWT.SetData);
			// addTypedEvent ("Settings", SWT.Settings); // note: this event
			// only goes to Display
			addTypedEvent(IEventConstants.SHOW, SWT.Show);
			addTypedEvent(IEventConstants.TRAVERSE, SWT.Traverse);
			addTypedEvent(IEventConstants.VERIFY, SWT.Verify);
			addTypedEvent(IEventConstants.IME_COMPOSITION, SWT.ImeComposition);
		}
		buildTypedEvents = true;
	}

	private void addTypedEvent(String name, int eventType) {
		String eventName = IEventConstants.getEventName(name);
		if (!routedEventCache.containsKey(eventName)) {
			TypedEvent typedEvent = new TypedEvent(name, eventType);
			routedEventCache.put(eventName, typedEvent);
			String eventPropertyName = IEventConstants
					.getEventPropertyName(name);
			String eventDataName = IEventConstants
					.getEventPropertyDataName(name);
			addProperty(new EventProperty(eventPropertyName, typedEvent,
					eventDataName));
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
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#findEvent(java.lang.String)
	 */
	public IEvent findEvent(String name) {
		assertInitialize();
		return routedEventCache.get(normalize(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#findProperty(java.lang.String)
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
				Method setter = DynamicProperty.createSetter(type,
						propertyType, name);
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
		return propertyCache.values().toArray(
				new IProperty[propertyCache.size()]);
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
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#isAssignableFrom(com.soyatec
	 * .xaswt.core.metadata.IMetaclass)
	 */
	public boolean isAssignableFrom(IMetaclass metaclass) {
		return getType().isAssignableFrom(metaclass.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#isInstance(java.lang.Object)
	 */
	public boolean isInstance(Object object) {
		return type.isInstance(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#isSubclassOf(com.soyatec.xaswt
	 * .core.metadata.IMetaclass)
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
	 * @see
	 * com.soyatec.xaswt.core.metadata.IMetaclass#isSuperclassOf(com.soyatec
	 * .xaswt.core.metadata.IMetaclass)
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
		Object object = doNewInstance(parameters);
		if (parameters != null && parameters.length > 0) {
			try {
				updateContainment(parameters[0], object);
				initialize(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	private void updateContainment(Object parent, Object control)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		if (control != null && parent != null && !(parent instanceof Widget)) {
			//
			// Add to default property identified by the type
			//
			IMetaclass parentMetaclass = XWT.getMetaclass(parent);
			IProperty[] properties = parentMetaclass.getProperties();
			IProperty useProperty = null;
			int count = 0;
			Class<?> childType = control.getClass();
			for (IProperty property : properties) {
				Class<?> propertyType = property.getType();
				if (propertyType == null || propertyType == Object.class) {
					continue;
				}
				if (propertyType.isArray()) {
					Class<?> dataType = propertyType.getComponentType();
					if (dataType.isAssignableFrom(childType)) {
						if (useProperty == null) {
							useProperty = property;
						}
						count++;
					}
				} else if (Collection.class.isAssignableFrom(propertyType)) {
					if (useProperty == null) {
						useProperty = property;
					}
					count++;
				} else if (propertyType.isAssignableFrom(childType)) {
					if (useProperty == null) {
						useProperty = property;
					}
					count++;
				}
			}
			if (count == 1) {
				Class<?> propertyType = useProperty.getType();
				if (propertyType.isArray()) {
					Object[] existingValue = (Object[]) useProperty
							.getValue(parent);
					Class<?> dataType = propertyType.getComponentType();
					Object[] value = null;
					if (existingValue == null) {
						value = (Object[]) Array.newInstance(dataType, 1);
						value[0] = control;
					} else {
						value = (Object[]) Array.newInstance(dataType,
								existingValue.length + 1);
						System.arraycopy(existingValue, 0, value, 0,
								existingValue.length);
						value[existingValue.length] = control;
					}
					useProperty.setValue(parent, value);
				} else if (Collection.class.isAssignableFrom(propertyType)
						&& !(control instanceof IBinding)) {
					Collection existingValue = (Collection) useProperty
							.getValue(parent);
					if (existingValue == null) {
						existingValue = new ArrayList();
					}
					existingValue.add(control);
					useProperty.setValue(parent, existingValue);
				} else if (propertyType.isAssignableFrom(childType)) {
					useProperty.setValue(parent, control);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyatec.xaswt.core.metadata.IMetaclass#newInstance()
	 */
	public Object doNewInstance(Object[] parameters) {
		assertInitialize();
		try {
			if (parameters.length == 0
					|| (!(parameters[0] instanceof Widget || JFacesHelper
							.isViewer(parameters[0])))) {
				return getType().newInstance();
			}
		} catch (Exception e1) {
		}

		try {
			Object swtObject = null;
			Object parent = parameters[0];
			Widget directParent = UserData.getWidget(parent);
			if (directParent == null) {
				directParent = UserData.getTreeParent(parent);
			}
			if (directParent == null) {
				throw new IllegalStateException();
			}
			if (Control.class.isAssignableFrom(getType())
					&& !(directParent instanceof Composite)) {
				directParent = getXWTLoader().findCompositeParent(directParent);
			}

			Object styleValue = null;
			if (parameters.length == 2
					&& parameters[1] != null
					&& (parameters[1].getClass() == int.class || parameters[1]
							.getClass() == Integer.class)) {
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
						if (styleValue == null) {
							swtObject = constructor.newInstance(new Object[] {
									parent});
							break;
						}
					} else if (parameterTypes[1].isAssignableFrom(int.class)) {
						if (styleValue == null)
							swtObject = constructor.newInstance(new Object[] {
									parent, 0});
						else
							swtObject = constructor.newInstance(new Object[] {
									parent, styleValue });
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

					if (parameterTypes[0].isAssignableFrom(directParent
							.getClass())) {
						if (parameterTypes.length == 1) {
							swtObject = constructor
									.newInstance(new Object[] { directParent });
							break;
						} else if (parameterTypes[1]
								.isAssignableFrom(int.class)) {
							if (styleValue == null)
								swtObject = constructor
										.newInstance(new Object[] {
												directParent, 0 });
							else
								swtObject = constructor
										.newInstance(new Object[] {
												directParent, styleValue });
							break;
						}
					}
				}
			}
			if (swtObject == null) {
				if (defaultConstructor == null) {
					try {
						swtObject = getType().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						throw new UnsupportedOperationException("Constructor "
								+ getType().getName() + " no found.");
					}
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
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor p : propertyDescriptors) {
				String propertyName = p.getName();
				Class<?> propertyType = p.getPropertyType();
				if (shouldIgnored(type, propertyName, propertyType)
						|| propertyCache
								.containsKey(propertyName.toLowerCase())) {
					continue;
				}
				if (p.getPropertyType() != null) {
					IProperty property = (superClass != null ? superClass
							.findProperty(p.getName().toLowerCase()) : null);
					if (property != null && !property.isDefault()) {
						addProperty(property);
					} else {
						if (p.getWriteMethod() != null
								|| !p.getPropertyType().isPrimitive()) {
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

				if (!propertyCache.containsKey(normalize(propertyName))
						&& !Modifier.isFinal(f.getModifiers())
						&& Modifier.isPublic(f.getModifiers())) {
					addProperty(new FieldProperty(f));
				}
			}

			for (EventSetDescriptor eventSetDescriptor : beanInfo
					.getEventSetDescriptors()) {
				String name = IEventConstants.getEventName(eventSetDescriptor
						.getName());
				BeanEvent event = new BeanEvent(eventSetDescriptor.getName(),
						eventSetDescriptor);
				routedEventCache.put(name, event);
				String propertyName = IEventConstants
						.getEventPropertyName(eventSetDescriptor.getName());
				String propertyDataName = IEventConstants
						.getEventPropertyDataName(eventSetDescriptor.getName());
				addProperty(new EventProperty(propertyName, event,
						propertyDataName));
			}
			if (isWidgetType(type)) {
				routedEventCache.put(normalize(IEventConstants.XWT_LOADED),
						new LoadedEvent(IEventConstants.XWT_LOADED));
			}

			markInitialized();
			buildTypedEvents();
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

	public void addEventGroup(IEventGroup eventGroup) {
		if (eventGroupCache == Collections.EMPTY_MAP) {
			eventGroupCache = new HashMap<String, IEventGroup>();
		}
		for (String string : eventGroup.getEventNames()) {
			if (eventGroupCache.containsKey(string)) {
				throw new IllegalArgumentException("Event \"" + string
						+ "\" already existis in a group.");
			}
			String key = normalize(string);
			if ("menudetecteventevent".equals(key)) {
				System.out.println(string);
			}

			eventGroupCache.put(key, eventGroup);
		}
	}

	public IEventGroup getEventGroup(String event) {
		IEventGroup eventGroup = eventGroupCache.get(event);
		if (eventGroup == null && superClass != null) {
			return superClass.getEventGroup(event);
		}
		return eventGroup;
	}

	public void addInitializer(IObjectInitializer initializer) {
		for (int i = 0; i < initializers.length; i++) {
			if (initializers[i] == initializer) {
				return;
			}
		}
		IObjectInitializer[] oldValue = initializers;
		initializers = new IObjectInitializer[oldValue.length + 1];
		System.arraycopy(oldValue, 0, initializers, 0, oldValue.length);
		initializers[oldValue.length] = initializer;
	}

	public void removeInitializer(IObjectInitializer initializer) {
		for (int i = 0; i < initializers.length; i++) {
			if (initializers[i] == initializer) {
				IObjectInitializer[] oldValue = initializers;
				initializers = new IObjectInitializer[oldValue.length - 1];
				System.arraycopy(oldValue, 0, initializers, 0, i);
				System.arraycopy(oldValue, i + 1, initializers, i,
						oldValue.length - i - 1);
				return;
			}
		}
	}
	
	public IObjectInitializer[] getInitializers() {
		return initializers;
	}
	
	public void initialize(Object instance) {
		if (superClass != null) {
			superClass.initialize(instance);
		}

		for (int i = 0; i < initializers.length; i++) {
			if (initializers[i] != null) {
				initializers[i].initialize(instance);
			}
		}
	}
}
