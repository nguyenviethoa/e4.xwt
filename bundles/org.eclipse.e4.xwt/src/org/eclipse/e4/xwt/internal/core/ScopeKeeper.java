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
package org.eclipse.e4.xwt.internal.core;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

public class ScopeKeeper implements DisposeListener {
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String, Object> nameMap = new HashMap<String, Object>();

	protected HashMap<Widget, HashMap<Object, HashMap<String, IObservable>>> bindingData = new HashMap<Widget, HashMap<Object, HashMap<String, IObservable>>>();

	protected Widget host;
	
	private final ScopeKeeper parent;

	public ScopeKeeper(ScopeKeeper parent, Widget host) {
		super();
		this.parent = parent;
		host.addDisposeListener(this);
		this.host = host;
	}

	public void widgetDisposed(DisposeEvent e) {
		Widget source = e.widget;
		bindingData.remove(source);
	}

	public void addNamedObject(String name, Object object) {
		nameMap.put(name, object);
	}

	public Object getNamedObject(String name) {
		Object object = nameMap.get(name);
		if (object != null)
			return object;
		return parent == null ? null : parent.getNamedObject(name);
	}

	public Collection<String> names() {
		return nameMap.keySet();
	}

	public boolean containsName(String name) {
		if (nameMap.containsKey(name))
			return true;
		return parent == null ? false : parent.containsName(name);
	}

	
	void addObservableValue(Widget widget, Object data, String property,
			IObservable value) {
		if (widget == null) {
			widget = host;
		}
		else {
			widget.addDisposeListener(this);
		}
			
		HashMap<Object, HashMap<String, IObservable>> widgetData = bindingData
				.get(widget);
		if (widgetData == null) {
			widgetData = new HashMap<Object, HashMap<String, IObservable>>();
			bindingData.put(widget, widgetData);
		}

		HashMap<String, IObservable> objectData = widgetData.get(data);
		if (objectData == null) {
			objectData = new HashMap<String, IObservable>();
			widgetData.put(data, objectData);
		}
		if (objectData.containsKey(property)) {
			throw new IllegalStateException();
		}
		objectData.put(property, value);
	}

	IObservableValue getObservableValue(Widget control, Object data,
			String property) {
		IObservable observable = getObservable(control, data, property);
		if (observable instanceof IObservableValue) {
			return (IObservableValue) observable;
		}
		return null;
	}

	IObservableList getObservableList(Widget control, Object data,
			String property) {
		IObservable observable = getObservable(control, data, property);
		if (observable instanceof IObservableList) {
			return (IObservableList) observable;
		}
		return null;
	}

	IObservableSet getObservableSet(Widget control, Object data,
			String property) {
		IObservable observable = getObservable(control, data, property);
		if (observable instanceof IObservableSet) {
			return (IObservableSet) observable;
		}
		return null;
	}

	IObservable getObservable(Widget control, Object data,
			String property) {
		for (HashMap<Object, HashMap<String, IObservable>> widgetData : bindingData.values()) {
			if (widgetData != null) {
				HashMap<String, IObservable> objectData = widgetData.get(data);
				if (objectData != null) {
					IObservable observable = objectData.get(property);
					if (observable != null) {
						return observable;
					}
				}
			}
		}
		return null;
	}
}
