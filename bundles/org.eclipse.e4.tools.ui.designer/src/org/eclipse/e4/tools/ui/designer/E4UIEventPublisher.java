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
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.ui.internal.workbench.UIEventPublisher;

import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.UIEvents.EventTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * 
 * This class is copied from {@link UIEventPublisher}. Some hooks are added when
 * event is dispatched successfully.
 * 
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4UIEventPublisher extends EContentAdapter {

	private IEclipseContext context;
	private List<Adapter> publishedAdapters;

	public E4UIEventPublisher(IEclipseContext e4Context) {
		this.context = e4Context;
	}

	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);

		// Inhibit No-Ops
		if (notification.isTouch())
			return;

		// Format the event
		Map<String, Object> argMap = new HashMap<String, Object>();
		String topic = formatData(notification, argMap);

		if (topic != null) {
			//System.out.println("UI Model Event: " + topic + " args: " + argMap); //$NON-NLS-1$ //$NON-NLS-2$
			IEventBroker eventManager = (IEventBroker) context
					.get(IEventBroker.class.getName());
			eventManager.send(topic, argMap);
			dispatchPublishedEvent(notification);
		} else {
			System.out.println("Event of unknown type received from the model"); //$NON-NLS-1$
		}
	}

	private void dispatchPublishedEvent(Notification notification) {
		if (publishedAdapters == null) {
			return;
		}
		for (Adapter adapter : publishedAdapters) {
			adapter.notifyChanged(notification);
		}
	}

	public static String formatData(Notification notification,
			Map<String, Object> argMap) {
		// The unchecked casts below represent 'asserts'
		MApplicationElement appElement = (MApplicationElement) notification
				.getNotifier();
		EStructuralFeature feature = (EStructuralFeature) notification
				.getFeature();

		argMap.put(EventTags.TYPE, getEventType(notification));
		argMap.put(EventTags.ELEMENT, appElement);
		argMap.put(EventTags.ATTNAME, feature.getName());

		if (notification.getEventType() == Notification.SET
				|| notification.getEventType() == Notification.ADD
				|| notification.getEventType() == Notification.REMOVE) {
			argMap.put(EventTags.NEW_VALUE, notification.getNewValue());
			argMap.put(EventTags.OLD_VALUE, notification.getOldValue());
		}

		if (appElement instanceof MUIElement) {
			argMap.put(EventTags.WIDGET, ((MUIElement) appElement).getWidget());
		}
		return getTopic(feature, getEventType(notification));
	}

	private static String getEventType(Notification notification) {
		switch (notification.getEventType()) {
		case Notification.ADD:
			return EventTypes.ADD;
		case Notification.REMOVE:
			return EventTypes.REMOVE;
		case Notification.SET:
			return EventTypes.SET;
		}

		return "UNKNOWN"; //$NON-NLS-1$
	}

	/**
	 * Map the attribute back to the correct topic.
	 * 
	 * @param type
	 * @return fully qualified topic
	 */
	private static String getTopic(EStructuralFeature eFeature, String type) {
		EClass eContainingClass = eFeature.getEContainingClass();
		return UIEvents.UITopicBase + UIEvents.TOPIC_SEP
				+ eContainingClass.getEPackage().getName() + UIEvents.TOPIC_SEP
				+ eContainingClass.getName() + UIEvents.TOPIC_SEP
				+ eFeature.getName() + UIEvents.TOPIC_SEP + type;
	}

	public void addPublishedAdapter(Adapter adapter) {
		if (publishedAdapters == null) {
			publishedAdapters = new ArrayList<Adapter>();
		}
		if (!publishedAdapters.contains(adapter)) {
			publishedAdapters.add(adapter);
		}
	}

	public void removePublishedAdapter(Adapter adapter) {
		if (publishedAdapters == null) {
			return;
		}
		publishedAdapters.remove(adapter);
	}
}
