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
package org.eclipse.e4.xwt.javabean;

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.IEventConstants;
import org.eclipse.e4.xwt.IEventInvoker;
import org.eclipse.e4.xwt.internal.core.IEventController;
import org.eclipse.e4.xwt.metadata.IEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class Controller implements Listener, IEventController {
	int waterMark = 0;
	protected int[] eventTypes = null;
	protected String[] names = null;
	protected Object[] handlers = null;
	protected Object[] receivers = null;
	protected Object[] args = null;

	protected void fireEvent(Event e) {
		int eventType = e.type;
		if (eventTypes == null) {
			return;
		}

		for (int i = 0; i < eventTypes.length; i++) {
			if (eventTypes[i] == eventType) {
				Object handler = handlers[i];
				if (handler instanceof IEventInvoker) {
					IEventInvoker eventInvoker = (IEventInvoker) handler;
					try {
						eventInvoker.invoke(args[i], e);
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
				}
				else {
					Method method = (Method) handler;
					try {
						method.setAccessible(true);
						// support old style
						if (method.getParameterTypes().length == 1) {
							method.invoke(receivers[i], e);
						} else {
							method.invoke(receivers[i], args[i], e);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.javabean.IEventHandler#hasEvent(java.lang.Object, org.eclipse.e4.xwt.metadata.IEvent)
	 */
	public boolean hasEvent(Object receiver, IEvent event) {
		if (receivers == null) {
			return false;
		}
		for (int i = 0; i < receivers.length; i++) {
			if (receivers[i] == receiver
					&& names[i].equalsIgnoreCase(event.getName())) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.javabean.IEventHandler#addEvent(int, java.lang.String, org.eclipse.e4.xwt.metadata.IEvent, org.eclipse.swt.widgets.Widget, java.lang.Object, java.lang.Object, java.lang.reflect.Method)
	 */
	public void addEvent(int eventType, String name, IEvent event,
			Widget control, Object receiver, Object arg, Method method) {
		doAddEvent(eventType, name, event, control, receiver, arg, method);
	}

	public void addEvent(int eventType, String name, IEvent event,
			Widget control, Object arg, IEventInvoker eventInvoker) {
		doAddEvent(eventType, name, event, control, null, arg, eventInvoker);
	}

	protected void doAddEvent(int eventType, String name, IEvent event,
				Widget control, Object receiver, Object arg, Object method) {
		if (eventTypes == null) {
			eventTypes = new int[3];
			handlers = new Method[3];
			names = new String[3];
			receivers = new Object[3];
			args = new Object[3];
		} else {
			for (int i = 0; i < eventTypes.length; i++) {
				if (eventTypes[i] == eventType && handlers[i] == method
						&& receivers[i] == receivers && args[i] == arg) {
					return;
				}
			}
		}
		if (waterMark >= eventTypes.length) {
			int[] oldEventTypes = eventTypes;
			Object[] oldHandlers = handlers;
			Object[] oldReceivers = receivers;
			Object[] oldNames = names;
			Object[] oldArgs = args;

			eventTypes = new int[waterMark + 3];
			handlers = new Object[waterMark + 3];
			receivers = new Object[waterMark + 3];
			names = new String[waterMark + 3];
			args = new Object[waterMark + 3];

			System.arraycopy(oldEventTypes, 0, eventTypes, 0, waterMark);
			System.arraycopy(oldHandlers, 0, handlers, 0, waterMark);
			System.arraycopy(oldReceivers, 0, receivers, 0, waterMark);
			System.arraycopy(oldArgs, 0, args, 0, waterMark);
			System.arraycopy(oldNames, 0, names, 0, waterMark);
		}

		eventTypes[waterMark] = eventType;
		handlers[waterMark] = method;
		receivers[waterMark] = receiver;
		args[waterMark] = arg;
		names[waterMark++] = name;

		if (eventType == IEventConstants.XWT_SWT_LOADED) {
			Listener[] listeners = control.getListeners(SWT.Paint);
			if (listeners.length > 0) {
				for (Listener listener : listeners) {
					control.removeListener(SWT.Paint, listener); 									
				}
				control.addListener(SWT.Paint, new LoadedEventListener(control)); 				
				for (Listener listener : listeners) {
					control.addListener(SWT.Paint, listener); 									
				}
			}
			else {
				control.addListener(SWT.Paint, new LoadedEventListener(control)); 				
			}
		}
		control.addListener(eventType, this);
	}

	class LoadedEventListener implements Listener {
		protected Widget control;
		
		public LoadedEventListener(Widget control) {
			this.control = control;
		}
		public void handleEvent(Event event) {
			Event loadedEvent = new Event();
			loadedEvent.button = event.button;
			loadedEvent.character = event.character;
			loadedEvent.count = event.count;
			loadedEvent.data = event.data;
			loadedEvent.detail = event.count;
			loadedEvent.display = event.display;
			loadedEvent.doit = event.doit;
			loadedEvent.end = event.end;
			loadedEvent.gc = event.gc;
			loadedEvent.height = event.height;
			loadedEvent.index = event.index;
			loadedEvent.item = event.item;
			loadedEvent.keyCode = event.keyCode;
			loadedEvent.keyLocation = event.keyLocation;
			loadedEvent.start = event.start;
			loadedEvent.stateMask = event.stateMask;
			loadedEvent.text = event.text;
			loadedEvent.time = event.time;
			loadedEvent.widget = event.widget;
			loadedEvent.width = event.width;
			loadedEvent.x = event.x;
			loadedEvent.type = IEventConstants.XWT_SWT_LOADED;
			control.removeListener(SWT.Paint, this);
			Controller.this.handleEvent(loadedEvent);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.javabean.IEventHandler#setEvent(org.eclipse.e4.xwt.metadata.IEvent, org.eclipse.swt.widgets.Widget, java.lang.Object, java.lang.Object, java.lang.reflect.Method)
	 */
	public void setEvent(IEvent event, Widget control, Object receiver,
			Object arg, Method method) {
		String name = event.getName();
		int eventType = getEventTypeByName(name);
		if (eventType != SWT.None) {
			addEvent(eventType, name, event, control, receiver, arg, method);
		}
	}

	public void setEvent(IEvent event, Widget control, 
			Object arg, IEventInvoker eventInvoker) {
		String name = event.getName();
		int eventType = getEventTypeByName(name);
		if (eventType != SWT.None) {
			doAddEvent(eventType, name, event, control, null, arg, eventInvoker);
		}
	}

	public static int getEventTypeByName(String name) {
		if (IEventConstants.KEY_DOWN.equalsIgnoreCase(name)) {
			return SWT.KeyDown;
		} else if (IEventConstants.KEY_UP.equalsIgnoreCase(name)) {
			return SWT.KeyUp;
		} else if (IEventConstants.MOUSE_DOWN.equalsIgnoreCase(name)) {
			return SWT.MouseDown;
		} else if (IEventConstants.MOUSE_UP.equalsIgnoreCase(name)) {
			return SWT.MouseUp;
		} else if (IEventConstants.MOUSE_MOVE.equalsIgnoreCase(name)) {
			return SWT.MouseMove;
		} else if (IEventConstants.MOUSE_ENTER.equalsIgnoreCase(name)) {
			return SWT.MouseEnter;
		} else if (IEventConstants.MOUSE_EXIT.equalsIgnoreCase(name)) {
			return SWT.MouseExit;
		} else if (IEventConstants.MOUSE_DOUBLE_CLICK.equalsIgnoreCase(name)) {
			return SWT.MouseDoubleClick;
		} else if (IEventConstants.PAINT.equalsIgnoreCase(name)) {
			return SWT.Paint;
		} else if (IEventConstants.MOVE.equalsIgnoreCase(name)) {
			return SWT.Move;
		} else if (IEventConstants.RESIZE.equalsIgnoreCase(name)) {
			return SWT.Resize;
		} else if (IEventConstants.DISPOSE.equalsIgnoreCase(name)) {
			return SWT.Dispose;
		} else if (IEventConstants.SELECTION.equalsIgnoreCase(name)) {
			return SWT.Selection;
		} else if (IEventConstants.DEFAULT_SELECTION.equalsIgnoreCase(name)) {
			return SWT.DefaultSelection;
		} else if (IEventConstants.FOCUS_IN.equalsIgnoreCase(name)) {
			return SWT.FocusIn;
		} else if (IEventConstants.FOCUS_OUT.equalsIgnoreCase(name)) {
			return SWT.FocusOut;
		} else if (IEventConstants.EXPAND.equalsIgnoreCase(name)) {
			return SWT.Expand;
		} else if (IEventConstants.COLLAPSE.equalsIgnoreCase(name)) {
			return SWT.Collapse;
		} else if (IEventConstants.ICONIFY.equalsIgnoreCase(name)) {
			return SWT.Iconify;
		} else if (IEventConstants.DEICONIFY.equalsIgnoreCase(name)) {
			return SWT.Deiconify;
		} else if (IEventConstants.CLOSE.equalsIgnoreCase(name)) {
			return SWT.Close;
		} else if (IEventConstants.SHOW.equalsIgnoreCase(name)) {
			return SWT.Show;
		} else if (IEventConstants.HIDE.equalsIgnoreCase(name)) {
			return SWT.Hide;
		} else if (IEventConstants.MODIFY.equalsIgnoreCase(name)) {
			return SWT.Modify;
		} else if (IEventConstants.VERIFY.equalsIgnoreCase(name)) {
			return SWT.Verify;
		} else if (IEventConstants.ACTIVATE.equalsIgnoreCase(name)) {
			return SWT.Activate;
		} else if (IEventConstants.DEACTIVATE.equalsIgnoreCase(name)) {
			return SWT.Deactivate;
		} else if (IEventConstants.HELP.equalsIgnoreCase(name)) {
			return SWT.Help;
		} else if (IEventConstants.DRAG_SELECT.equalsIgnoreCase(name)) {
			return SWT.DragDetect;
		} else if (IEventConstants.ARM.equalsIgnoreCase(name)) {
			return SWT.Arm;
		} else if (IEventConstants.TRAVERSE.equalsIgnoreCase(name)) {
			return SWT.Traverse;
		} else if (IEventConstants.MOUSE_HOVER.equalsIgnoreCase(name)) {
			return SWT.MouseHover;
		} else if (IEventConstants.HARD_KEY_DOWN.equalsIgnoreCase(name)) {
			return SWT.HardKeyDown;
		} else if (IEventConstants.HARD_KEY_UP.equalsIgnoreCase(name)) {
			return SWT.HardKeyUp;
		} else if (IEventConstants.MENU_DETECT.equalsIgnoreCase(name)) {
			return SWT.MenuDetect;
		} else if (IEventConstants.MOUSE_WHEEL.equalsIgnoreCase(name)) {
			return SWT.MouseWheel;
		} else if (IEventConstants.SETTINGS.equalsIgnoreCase(name)) {
			return SWT.Settings;
		} else if (IEventConstants.ERASE_ITEM.equalsIgnoreCase(name)) {
			return SWT.EraseItem;
		} else if (IEventConstants.MEASURE_ITEM.equalsIgnoreCase(name)) {
			return SWT.MeasureItem;
		} else if (IEventConstants.PAINT_ITEM.equalsIgnoreCase(name)) {
			return SWT.PaintItem;
		} else if (IEventConstants.XWT_LOADED.equalsIgnoreCase(name) || IEventConstants.XWT_LOADED_EVENT.equalsIgnoreCase(name)) {
			return IEventConstants.XWT_SWT_LOADED;
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
		return SWT.None;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.javabean.IEventHandler#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event e) {
		fireEvent(e);
	}
}
