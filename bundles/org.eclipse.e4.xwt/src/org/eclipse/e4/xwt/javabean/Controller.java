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

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IEventGroup;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class Controller implements Listener {
	int waterMark = 0;
	protected int[] eventTypes = null;
	protected String[] names = null;
	protected Method[] handlers = null;
	protected Object[] receivers = null;

	protected void fireEvent(Event e) {
		int eventType = e.type;
		if (eventTypes == null) {
			return;
		}

		for (int i = 0; i < eventTypes.length; i++) {
			if (eventTypes[i] == eventType) {
				if (handlers[i] != null) {
					handlers[i].setAccessible(true);
					if (!invokeEvent(i, e)) {
						return;
					}
				}
			}
		}
	}

	protected boolean invokeEvent(int i, Event e) {
		Object object = receivers[i];
		String name = names[i];
		IMetaclass metaclass = XWT.getMetaclass(object);
		IEventGroup eventGroup = metaclass.getEventGroup(name);
		if (eventGroup != null) {
			eventGroup.handleBefore(object, name);
		}
		try {
			handlers[i].invoke(object, e);
			if (eventGroup != null) {
				eventGroup.handleAfter(object, name);
			}
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public void addEvent(int eventType, String name, IEvent event, Widget control, Object receiver, Method method) {
		if (eventTypes == null) {
			eventTypes = new int[3];
			handlers = new Method[3];
			names = new String[3];
			receivers = new Object[3];
		}
		if (waterMark >= eventTypes.length) {
			int[] oldEventTypes = eventTypes;
			Method[] oldHandlers = handlers;
			Object[] oldReceivers = receivers;
			Object[] oldNames = names;

			eventTypes = new int[waterMark + 3];
			handlers = new Method[waterMark + 3];
			receivers = new Object[waterMark + 3];
			names = new String[waterMark + 3];

			System.arraycopy(oldEventTypes, 0, eventTypes, 0, waterMark);
			System.arraycopy(oldHandlers, 0, handlers, 0, waterMark);
			System.arraycopy(oldReceivers, 0, receivers, 0, waterMark);
			System.arraycopy(oldNames, 0, names, 0, waterMark);
		}

		eventTypes[waterMark] = eventType;
		handlers[waterMark] = method;
		receivers[waterMark++] = receiver;
		names[waterMark++] = name;

		control.addListener(eventType, this);
	}

	public void setEvent(IEvent event, Widget control, Object receiver, Method method) {
		String name = event.getName();
		if (IEventConstants.KEY_DOWN.equalsIgnoreCase(name)) {
			addEvent(SWT.KeyDown, IEventConstants.KEY_DOWN, event, control, receiver, method);
		} else if (IEventConstants.KEY_UP.equalsIgnoreCase(name)) {
			addEvent(SWT.KeyUp, IEventConstants.KEY_UP, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_DOWN.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseDown, IEventConstants.MOUSE_DOWN, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_UP.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseUp, IEventConstants.MOUSE_UP, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_MOVE.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseMove, IEventConstants.MOUSE_MOVE, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_ENTER.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseEnter, IEventConstants.MOUSE_ENTER, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_EXIT.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseExit, IEventConstants.MOUSE_EXIT, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_DOUBLE_CLICK.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseDoubleClick, IEventConstants.MOUSE_DOUBLE_CLICK, event, control, receiver, method);
		} else if (IEventConstants.PAINT.equalsIgnoreCase(name)) {
			addEvent(SWT.Paint, IEventConstants.PAINT, event, control, receiver, method);
		} else if (IEventConstants.MOVE.equalsIgnoreCase(name)) {
			addEvent(SWT.Move, IEventConstants.MOVE, event, control, receiver, method);
		} else if (IEventConstants.RESIZE.equalsIgnoreCase(name)) {
			addEvent(SWT.Resize, IEventConstants.RESIZE, event, control, receiver, method);
		} else if (IEventConstants.DISPOSE.equalsIgnoreCase(name)) {
			addEvent(SWT.Dispose, IEventConstants.DISPOSE, event, control, receiver, method);
		} else if (IEventConstants.SELECTION.equalsIgnoreCase(name)) {
			addEvent(SWT.Selection, IEventConstants.SELECTION, event, control, receiver, method);
		} else if (IEventConstants.DEFAULT_SELECTION.equalsIgnoreCase(name)) {
			addEvent(SWT.DefaultSelection, IEventConstants.DEFAULT_SELECTION, event, control, receiver, method);
		} else if (IEventConstants.FOCUS_IN.equalsIgnoreCase(name)) {
			addEvent(SWT.FocusIn, IEventConstants.FOCUS_IN, event, control, receiver, method);
		} else if (IEventConstants.FOCUS_OUT.equalsIgnoreCase(name)) {
			addEvent(SWT.FocusOut, IEventConstants.FOCUS_OUT, event, control, receiver, method);
		} else if (IEventConstants.EXPAND.equalsIgnoreCase(name)) {
			addEvent(SWT.Expand, IEventConstants.EXPAND, event, control, receiver, method);
		} else if (IEventConstants.COLLAPSE.equalsIgnoreCase(name)) {
			addEvent(SWT.Collapse, IEventConstants.COLLAPSE, event, control, receiver, method);
		} else if (IEventConstants.ICONIFY.equalsIgnoreCase(name)) {
			addEvent(SWT.Iconify, IEventConstants.ICONIFY, event, control, receiver, method);
		} else if (IEventConstants.DEICONIFY.equalsIgnoreCase(name)) {
			addEvent(SWT.Deiconify, IEventConstants.DEICONIFY, event, control, receiver, method);
		} else if (IEventConstants.CLOSE.equalsIgnoreCase(name)) {
			addEvent(SWT.Close, IEventConstants.CLOSE, event, control, receiver, method);
		} else if (IEventConstants.SHOW.equalsIgnoreCase(name)) {
			addEvent(SWT.Show, IEventConstants.SHOW, event, control, receiver, method);
		} else if (IEventConstants.HIDE.equalsIgnoreCase(name)) {
			addEvent(SWT.Hide, IEventConstants.HIDE, event, control, receiver, method);
		} else if (IEventConstants.MODIFY.equalsIgnoreCase(name)) {
			addEvent(SWT.Modify, IEventConstants.MODIFY, event, control, receiver, method);
		} else if (IEventConstants.VERIFY.equalsIgnoreCase(name)) {
			addEvent(SWT.Verify, IEventConstants.VERIFY, event, control, receiver, method);
		} else if (IEventConstants.ACTIVATE.equalsIgnoreCase(name)) {
			addEvent(SWT.Activate, IEventConstants.ACTIVATE, event, control, receiver, method);
		} else if (IEventConstants.DEACTIVATE.equalsIgnoreCase(name)) {
			addEvent(SWT.Deactivate, IEventConstants.DEACTIVATE, event, control, receiver, method);
		} else if (IEventConstants.HELP.equalsIgnoreCase(name)) {
			addEvent(SWT.Help, IEventConstants.HELP, event, control, receiver, method);
		} else if (IEventConstants.DRAG_SELECT.equalsIgnoreCase(name)) {
			addEvent(SWT.DragDetect, IEventConstants.DRAG_SELECT, event, control, receiver, method);
		} else if (IEventConstants.ARM.equalsIgnoreCase(name)) {
			addEvent(SWT.Arm, IEventConstants.ARM, event, control, receiver, method);
		} else if (IEventConstants.TRAVERSE.equalsIgnoreCase(name)) {
			addEvent(SWT.Traverse, IEventConstants.TRAVERSE, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_HOVER.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseHover, IEventConstants.MOUSE_HOVER, event, control, receiver, method);
		} else if (IEventConstants.HARD_KEY_DOWN.equalsIgnoreCase(name)) {
			addEvent(SWT.HardKeyDown, IEventConstants.HARD_KEY_DOWN, event, control, receiver, method);
		} else if (IEventConstants.HARD_KEY_UP.equalsIgnoreCase(name)) {
			addEvent(SWT.HardKeyUp, IEventConstants.HARD_KEY_UP, event, control, receiver, method);
		} else if (IEventConstants.MENU_DETECT.equalsIgnoreCase(name)) {
			addEvent(SWT.MenuDetect, IEventConstants.MENU_DETECT, event, control, receiver, method);
		} else if (IEventConstants.MOUSE_WHEEL.equalsIgnoreCase(name)) {
			addEvent(SWT.MouseWheel, IEventConstants.MOUSE_WHEEL, event, control, receiver, method);
		} else if (IEventConstants.SETTINGS.equalsIgnoreCase(name)) {
			addEvent(SWT.Settings, IEventConstants.SETTINGS, event, control, receiver, method);
		} else if (IEventConstants.ERASE_ITEM.equalsIgnoreCase(name)) {
			addEvent(SWT.EraseItem, IEventConstants.ERASE_ITEM, event, control, receiver, method);
		} else if (IEventConstants.MEASURE_ITEM.equalsIgnoreCase(name)) {
			addEvent(SWT.MeasureItem, IEventConstants.MEASURE_ITEM, event, control, receiver, method);
		} else if (IEventConstants.PAINT_ITEM.equalsIgnoreCase(name)) {
			addEvent(SWT.PaintItem, IEventConstants.PAINT_ITEM, event, control, receiver, method);
		}
		// case SWT.PaintItem:
		// firePaintItem(e);
		// break;
		// case DND.DragStart:
		// dnde = new DNDEventWrapper(e);
		// fireGiveFeedback(dnde);
		// break;
		// case DND.DragEnter:
		// dnde = new DNDEventWrapper(e);
		// fireDragEnter(dnde);
		// break;
		// case DND.DragLeave:
		// dnde = new DNDEventWrapper(e);
		// fireDragLeaves(dnde);
		// break;
		// case DND.DragOver:
		// dnde = new DNDEventWrapper(e);
		// fireDragOver(dnde);
		// break;
		// case DND.Drop:
		// dnde = new DNDEventWrapper(e);
		// fireDrop(dnde);
		// break;
		// }
	}

	public void handleEvent(Event e) {
		fireEvent(e);
	}
}
