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
package org.eclipse.e4.xwt.tools.ui.designer.editor.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.xwt.javabean.metadata.BeanEvent;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.tools.ui.designer.editor.XWTDesigner;
import org.eclipse.e4.xwt.tools.ui.designer.editor.actions.AddEventHandlerAction;
import org.eclipse.e4.xwt.tools.ui.designer.resources.ImageShop;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTUtility;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;

public class EventMenuManager extends MenuManager {

	private static Map<String, List<String>> eventsMap = new HashMap<String, List<String>>();
	static {
		ArrayList<String> events = new ArrayList<String>();
		events.add("Active");
		events.add("Close");
		events.add("Deactive");
		events.add("Deiconify");
		events.add("Iconify");
		eventsMap.put("shell", events);

		events = new ArrayList<String>();
		events.add("Move");
		events.add("Resize");
		eventsMap.put("control", events);

		events = new ArrayList<String>();
		events.add("Collapse");
		events.add("Expand");
		eventsMap.put("tree", events);

		events = new ArrayList<String>();
		events.add("Collapse");
		events.add("Expand");
		eventsMap.put("item", events);

		events = new ArrayList<String>();
		events.add("Selection");
		events.add("DefaultSelection");
		eventsMap.put("selection", events);

		events = new ArrayList<String>();
		events.add("FocusIn");
		events.add("FocusOut");
		eventsMap.put("focus", events);

		events = new ArrayList<String>();
		events.add("HardKeyUp");
		events.add("HardKeyDown");
		eventsMap.put("hardKey", events);

		events = new ArrayList<String>();
		events.add("KeyUp");
		events.add("KeyDown");
		eventsMap.put("key", events);

		events = new ArrayList<String>();
		events.add("Traverse");
		eventsMap.put("traverse", events);

		events = new ArrayList<String>();
		events.add("MouseDoubleClick");
		events.add("MouseUp");
		events.add("MouseDown");
		eventsMap.put("mouse", events);

		events = new ArrayList<String>();
		events.add("MouseMove");
		eventsMap.put("mouseMove", events);

		events = new ArrayList<String>();
		events.add("MouseWheel");
		eventsMap.put("mouseWheel", events);

		events = new ArrayList<String>();
		events.add("MouseEnter");
		events.add("MouseEit");
		events.add("MouseHover");
		eventsMap.put("mouseTrack", events);

		events = new ArrayList<String>();
		events.add("Show");
		events.add("Hide");
		eventsMap.put("menu", events);

		events = new ArrayList<String>();
		events.add("EraseItem");
		events.add("MeasureItem");
		events.add("PaintItem");
		eventsMap.put("item", events);

		events = new ArrayList<String>();
		events.add("Arm");
		eventsMap.put("arm", events);

		events = new ArrayList<String>();
		events.add("Dispose");
		eventsMap.put("dispose", events);

		events = new ArrayList<String>();
		events.add("Help");
		eventsMap.put("help", events);

		events = new ArrayList<String>();
		events.add("Paint");
		eventsMap.put("paint", events);

		events = new ArrayList<String>();
		events.add("Settings");
		eventsMap.put("settings", events);

		events = new ArrayList<String>();
		events.add("Verify");
		eventsMap.put("verify", events);

		events = new ArrayList<String>();
		events.add("Modify");
		eventsMap.put("modify", events);

		events = new ArrayList<String>();
		events.add("DragDetect");
		eventsMap.put("dragDetect", events);

		events = new ArrayList<String>();
		events.add("MenuDetect");
		eventsMap.put("menuDetect", events);
	}
	private EditPart editpart;
	private XWTDesigner designer;

	public EventMenuManager(EditPart editpart, XWTDesigner designer) {
		super("Add event handler");
		this.editpart = editpart;
		this.designer = designer;
		createMenus(this);
	}

	/**
	 * @param editpart
	 * @param editor
	 */
	private void createMenus(MenuManager parent) {
		List<String> eventNames = new ArrayList<String>();
		IEvent[] beanEvents = getBeanEvents(editpart);
		if (beanEvents.length != 0) {
			for (IEvent event : beanEvents) {
				String name = event.getName().trim();
				if (eventsMap.get(name) != null) {
					eventNames.add(name);
				} else {
					System.err.println(name + " Not Defined...");
				}
			}
		}

		Collections.sort(eventNames);

		for (String type : eventNames) {
			ImageDescriptor image = ImageShop.getImageDescriptor(ImageShop.IMG_EVENT);
			MenuManager menu = new MenuManager(type, image, null);
			List<String> events = eventsMap.get(type);
			for (String event : events) {
				menu.add(new AddEventHandlerAction(editpart, designer, event));
			}
			parent.add(menu);
		}
	}

	private IEvent[] getBeanEvents(EditPart editpart) {
		if (editpart == null) {
			return new IEvent[0];
		}
		Object model = editpart.getModel();
		if (model instanceof XamlNode) {
			IMetaclass metaclass = XWTUtility.getMetaclass((XamlNode) model);
			if (metaclass != null) {
				IEvent[] events = metaclass.getEvents();
				List<IEvent> result = new ArrayList<IEvent>();
				for (IEvent event : events) {
					if (event instanceof BeanEvent) {
						result.add(event);
					}
				}
				return result.toArray(new IEvent[result.size()]);
			}
		}
		return new IEvent[0];
	}

}
