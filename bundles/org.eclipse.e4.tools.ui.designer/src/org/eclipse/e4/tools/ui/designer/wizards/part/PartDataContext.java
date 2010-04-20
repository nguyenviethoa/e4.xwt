/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards.part;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EClass;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartDataContext {

	// Event properties.
	public static final String VALUE = "PartDataContext.value";
	public static final String TYPE = "PartDataContext.type";
	public static final String SOURCE = "PartDataContext.source";
	public static final String PROPERTIES = "PartDataContext.properties";
	public static final String MASTER_PROPERTIES = "PartDataContext.masterProperties";

	/**
	 * Data Context instance for new Part.
	 */
	private Object value;

	/**
	 * Data Context type for new Part, type is Java Class or EClass;
	 */
	private Object type;

	/**
	 * The source of the Data Context, for EObject, it should be the URI of
	 * resource.
	 */
	private IFile source;

	/**
	 * Properties for generating Data Context UIs.
	 */
	private List<Object> properties;

	/**
	 * Properties which will be represented as Master for some Details pages.
	 */
	private List<Object> masterableProperties;

	private Map<Object, List<String>> eventHandlers;

	private final PropertyChangeSupport support;

	public PartDataContext() {
		this(null);
	}

	public PartDataContext(Object value) {
		support = new PropertyChangeSupport(this);
		setValue(value);
	}

	public void setValue(Object newValue) {
		Object oldValue = this.value;
		this.value = newValue;
		// clear and reset.
		properties = null;
		type = null;
		getType();
		getProperties();
		getMasterProperties();
		support.firePropertyChange(VALUE, oldValue, this.value);
	}

	public Object getValue() {
		return value;
	}

	public void setSource(IFile newSource) {
		Object oldValue = this.source;
		this.source = newSource;
		support.firePropertyChange(SOURCE, oldValue, this.source);
	}

	public IFile getSource() {
		return source;
	}

	public void setType(Object newType) {
		Object oldValue = this.type;
		this.type = newType;
		// clear and reset.
		properties = null;
		value = null;
		getProperties();
		getMasterProperties();
		// send event.
		support.firePropertyChange(TYPE, oldValue, this.type);
	}

	public Object getType() {
		if (type == null && value != null) {
			type = PDC.getType(value);
		}
		return type;
	}

	public void setProperties(List<Object> newProperties) {
		Object oldValue = this.properties;
		this.properties = newProperties;
		support.firePropertyChange(PROPERTIES, oldValue, this.properties);
	}

	public void addProperty(Object property) {
		Object oldValue = this.properties;
		if (properties == null) {
			properties = new ArrayList<Object>();
		}
		if (!properties.contains(property)) {
			properties.add(property);
			support.firePropertyChange(PROPERTIES, oldValue, this.properties);
		}
	}

	public void removeProperty(Object property) {
		Object oldValue = this.properties;
		if (properties == null) {
			return;
		}
		if (properties.remove(property)) {
			support.firePropertyChange(PROPERTIES, oldValue, this.properties);
		}
	}

	public List<String> getPropertyNames() {
		List<Object> properties = getProperties();
		if (properties == null || properties.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> propertyNames = new ArrayList<String>(properties.size());
		for (Object property : properties) {
			propertyNames.add(PDC.getPropertyName(property));
		}
		return propertyNames;
	}

	public List<Object> getProperties() {
		if (properties == null) {
			properties = PDC.collectBasicProperties(getType());
		}
		return properties;
	}

	public boolean containsProperty(Object property) {
		if (properties == null || property == null) {
			return false;
		}
		return properties.contains(property);
	}

	public void setMasterProperties(List<Object> masterProperties) {
		Object oldValue = this.masterableProperties;
		this.masterableProperties = masterProperties;
		support.firePropertyChange(MASTER_PROPERTIES, oldValue,
				this.masterableProperties);
	}

	public List<Object> getMasterProperties() {
		if (properties == null) {
			masterableProperties = null;
		} else if (masterableProperties != null) {
			for (Iterator<Object> iterator = masterableProperties.iterator(); iterator
					.hasNext();) {
				Object type = iterator.next();
				if (!properties.contains(type)) {
					iterator.remove();
				}
			}
		}
		return masterableProperties;
	}

	public void addMasterProperty(Object property) {
		Object oldValue = this.masterableProperties;
		if (masterableProperties == null) {
			masterableProperties = new ArrayList<Object>();
		}
		if (!masterableProperties.contains(property)) {
			masterableProperties.add(property);
			support.firePropertyChange(MASTER_PROPERTIES, oldValue,
					this.masterableProperties);
		}
	}

	public void removeMasterProperty(Object property) {
		if (masterableProperties == null) {
			return;
		}
		Object oldValue = this.masterableProperties;
		if (masterableProperties.remove(property)) {
			support.firePropertyChange(MASTER_PROPERTIES, oldValue,
					this.masterableProperties);
		}
	}

	public boolean containsMaster(Object property) {
		if (masterableProperties == null || property == null) {
			return false;
		}
		return masterableProperties.contains(property);
	}

	/**
	 * Master-Detail indication for code generation, return TRUE if and only if
	 * <code>masterableProperties</code> is not empty.
	 * 
	 * Note: There's no need to generating codes for each masterable property,
	 * only one event of <code>SWT.Selection</code> can be generated for
	 * providing selection.
	 */
	public boolean hasMasterProperties() {
		List<Object> masterProperties = getMasterProperties();
		return masterProperties != null && masterProperties.size() > 0;
	}

	public String getDisplayName() {
		Object type = getType();
		if (type == null) {
			return "";
		}
		if (type instanceof Class<?>) {
			return ((Class<?>) type).getSimpleName();
		} else if (type instanceof EClass) {
			return ((EClass) type).getName();
		}
		return "";
	}

	public boolean isPropertyMany(Object property) {
		if (property == null) {
			return false;
		}
		return PDC.isMany(getType(), PDC.getPropertyName(property));
	}

	public void addEventHandler(Object property, String event, String handler) {
		if (property == null || event == null || handler == null) {
			return;
		}
		if (eventHandlers == null) {
			eventHandlers = new HashMap<Object, List<String>>();
		}
		List<String> handlers = eventHandlers.get(property);
		if (handlers == null) {
			eventHandlers.put(property, handlers = new ArrayList<String>());
		}
		if (!event.endsWith("Event")) {
			event += "Event";
		}
		String eventHandler = event + "=\"" + handler + "\"";
		handlers.add(eventHandler);
	}

	public List<String> getEventHandlers(Object property) {
		if (eventHandlers == null || property == null) {
			return null;
		}
		return eventHandlers.get(property);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

}
