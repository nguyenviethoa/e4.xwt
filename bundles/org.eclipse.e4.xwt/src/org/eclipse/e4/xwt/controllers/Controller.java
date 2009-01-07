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
package org.eclipse.e4.xwt.controllers;

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class Controller implements Listener {
	int waterMark = 0;
	protected int[] eventTypes = null;
	protected Method[] handlers = null;
	protected Object[] receivers = null;

	public void fireEvent(Event e) {
		int eventType = e.type;
		if (eventTypes == null) {
			return;
		}

		for (int i = 0; i < eventTypes.length; i++) {
			if (eventTypes[i] == eventType) {
				if (handlers[i] != null) {
					try {
						handlers[i].setAccessible(true);
						handlers[i].invoke(receivers[i], e);
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
				}
			}
		}
	}

	public void addEvent(int eventType, IEvent event, Widget control, Object receiver, Method method) {
		if (eventTypes == null) {
			eventTypes = new int[3];
			handlers = new Method[3];
			receivers = new Object[3];
		}
		if (waterMark >= eventTypes.length) {
			int[] oldEventTypes = eventTypes;
			Method[] oldHandlers = handlers;
			Object[] oldReceivers = receivers;

			eventTypes = new int[waterMark + 3];
			handlers = new Method[waterMark + 3];
			receivers = new Object[waterMark + 3];

			System.arraycopy(oldEventTypes, 0, eventTypes, 0, waterMark);
			System.arraycopy(oldHandlers, 0, handlers, 0, waterMark);
			System.arraycopy(oldReceivers, 0, receivers, 0, waterMark);
		}

		eventTypes[waterMark] = eventType;
		handlers[waterMark] = method;
		receivers[waterMark] = receiver;

		control.addListener(eventType, this);
	}

	public void setEvent(IEvent event, Widget control, Object receiver, Method method) {
		String name = event.getName();
		if ("KeyDown".equalsIgnoreCase(name)) {
			addEvent(SWT.KeyDown, event, control, receiver, method);
		} else if ("KeyUp".equalsIgnoreCase(name)) {
			addEvent(SWT.KeyUp, event, control, receiver, method);
		} else if ("MouseDown".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseDown, event, control, receiver, method);
		} else if ("MouseUp".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseUp, event, control, receiver, method);
		} else if ("MouseMove".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseMove, event, control, receiver, method);
		} else if ("MouseEnter".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseEnter, event, control, receiver, method);
		} else if ("MouseExit".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseExit, event, control, receiver, method);
		} else if ("MouseDoubleClick".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseDoubleClick, event, control, receiver, method);
		} else if ("Paint".equalsIgnoreCase(name)) {
			addEvent(SWT.Paint, event, control, receiver, method);
		} else if ("Move".equalsIgnoreCase(name)) {
			addEvent(SWT.Move, event, control, receiver, method);
		} else if ("Resize".equalsIgnoreCase(name)) {
			addEvent(SWT.Resize, event, control, receiver, method);
		} else if ("Dispose".equalsIgnoreCase(name)) {
			addEvent(SWT.Dispose, event, control, receiver, method);
		} else if ("Dispose".equalsIgnoreCase(name)) {
			addEvent(SWT.Dispose, event, control, receiver, method);
		} else if ("Selection".equalsIgnoreCase(name)) {
			addEvent(SWT.Selection, event, control, receiver, method);
		} else if ("DefaultSelection".equalsIgnoreCase(name)) {
			addEvent(SWT.DefaultSelection, event, control, receiver, method);
		} else if ("FocusIn".equalsIgnoreCase(name)) {
			addEvent(SWT.FocusIn, event, control, receiver, method);
		} else if ("FocusOut".equalsIgnoreCase(name)) {
			addEvent(SWT.FocusOut, event, control, receiver, method);
		} else if ("Expand".equalsIgnoreCase(name)) {
			addEvent(SWT.Expand, event, control, receiver, method);
		} else if ("Collapse".equalsIgnoreCase(name)) {
			addEvent(SWT.Collapse, event, control, receiver, method);
		} else if ("Iconify".equalsIgnoreCase(name)) {
			addEvent(SWT.Iconify, event, control, receiver, method);
		} else if ("Deiconify".equalsIgnoreCase(name)) {
			addEvent(SWT.Deiconify, event, control, receiver, method);
		} else if ("Close".equalsIgnoreCase(name)) {
			addEvent(SWT.Close, event, control, receiver, method);
		} else if ("Show".equalsIgnoreCase(name)) {
			addEvent(SWT.Show, event, control, receiver, method);
		} else if ("Hide".equalsIgnoreCase(name)) {
			addEvent(SWT.Hide, event, control, receiver, method);
		} else if ("Modify".equalsIgnoreCase(name)) {
			addEvent(SWT.Modify, event, control, receiver, method);
		} else if ("Verify".equalsIgnoreCase(name)) {
			addEvent(SWT.Verify, event, control, receiver, method);
		} else if ("Activate".equalsIgnoreCase(name)) {
			addEvent(SWT.Activate, event, control, receiver, method);
		} else if ("Deactivate".equalsIgnoreCase(name)) {
			addEvent(SWT.Deactivate, event, control, receiver, method);
		} else if ("Help".equalsIgnoreCase(name)) {
			addEvent(SWT.Help, event, control, receiver, method);
		} else if ("DragDetect".equalsIgnoreCase(name)) {
			addEvent(SWT.DragDetect, event, control, receiver, method);
		} else if ("Arm".equalsIgnoreCase(name)) {
			addEvent(SWT.Arm, event, control, receiver, method);
		} else if ("Traverse".equalsIgnoreCase(name)) {
			addEvent(SWT.Traverse, event, control, receiver, method);
		} else if ("MouseHover".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseHover, event, control, receiver, method);
		} else if ("HardKeyDown".equalsIgnoreCase(name)) {
			addEvent(SWT.HardKeyDown, event, control, receiver, method);
		} else if ("HardKeyUp".equalsIgnoreCase(name)) {
			addEvent(SWT.HardKeyUp, event, control, receiver, method);
		} else if ("MenuDetect".equalsIgnoreCase(name)) {
			addEvent(SWT.MenuDetect, event, control, receiver, method);
		} else if ("MouseWheel".equalsIgnoreCase(name)) {
			addEvent(SWT.MouseWheel, event, control, receiver, method);
		} else if ("Settings".equalsIgnoreCase(name)) {
			addEvent(SWT.Settings, event, control, receiver, method);
		} else if ("EraseItem".equalsIgnoreCase(name)) {
			addEvent(SWT.EraseItem, event, control, receiver, method);
		} else if ("MeasureItem".equalsIgnoreCase(name)) {
			addEvent(SWT.MeasureItem, event, control, receiver, method);
		} else if ("PaintItem".equalsIgnoreCase(name)) {
			addEvent(SWT.PaintItem, event, control, receiver, method);
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
