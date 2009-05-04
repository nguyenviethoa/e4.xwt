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
package org.eclipse.e4.xwt.dataproviders.observable;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.util.Util;

/**
 * Notes: Binding type is java.lang.String.
 * 
 * @author jliu (jin.liu@soyatec.com)
 */
public class XWTObservableValue extends AbstractObservableValue {

	public static final String VALUE_CHANGED_EVENT = "EVENT_NODE_VALUE_CHANGED";

	private Object observed;
	private Object valueType;

	private EventManager eventManager;
	private EventListener eventListener;

	private boolean updating = false;

	/**
	 * 
	 */
	public XWTObservableValue(Object valueType, Object observed) {
		super(XWT.realm);
		this.valueType = valueType;
		this.observed = observed;
		init();
	}

	/**
	 * Add listener to observed.
	 */
	private void init() {
		if (eventListener == null) {
			eventListener = new EventListener() {
				public void handleEvent(Event evt) {
					if (!updating) {
						final ValueDiff diff = Diffs.createValueDiff(evt.getOldValue(), evt.getNewValue());
						getRealm().exec(new Runnable() {
							public void run() {
								fireValueChange(diff);
							}
						});
					}
				}
			};
		}
		eventManager = EventManager.getEventManager(observed, getRealm());
		eventManager.addEventListener(VALUE_CHANGED_EVENT, eventListener);

	}

	/**
	 * @return the observed
	 */
	public Object getObserved() {
		return observed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doSetValue(java.lang.Object)
	 */
	protected void doSetValue(Object value) {
		updating = true;
		Object oldValue = doGetValue();
		value = convert(value);
		doSetApprovedValue(value);
		if (!Util.equals(oldValue, value)) {
			fireValueChange(Diffs.createValueDiff(oldValue, value));
			eventManager.dispatchEvent(new Event(observed, oldValue, value, VALUE_CHANGED_EVENT));
		}
		updating = false;
	}

	/**
	 * @param value
	 * @return
	 */
	protected Object convert(Object value) {
		if (value != null) {
			IConverter c = XWT.findConvertor(value.getClass(), (Class<?>) getValueType());
			if (c != null) {
				return c.convert(value);
			}
		}
		return value;
	}

	/**
	 * @param value
	 */
	protected void doSetApprovedValue(Object value) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
	 */
	protected Object doGetValue() {
		return observed.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
	 */
	public Object getValueType() {
		return valueType;
	}

}
