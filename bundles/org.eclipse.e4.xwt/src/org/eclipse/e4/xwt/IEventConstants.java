/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt;

public interface IEventConstants {
	String KEY_DOWN = "KeyDown";
	String KEY_UP = "KeyUp";
	String[] KEY_GROUP = new String[] {KEY_DOWN, KEY_UP};
	
	String MOUSE_DOWN = "MouseDown";
	String MOUSE_UP = "MouseUp";
	String[] MOUSE_GROUP = new String[] {MOUSE_DOWN, MOUSE_UP};
	
	String MOUSE_MOVE = "MouseMove";
	String MOUSE_ENTER = "MouseEnter";
	String MOUSE_EXIT = "MouseExit";
	String MOUSE_HOVER = "MouseHover";
	String[] MOUSE_MOVING_GROUP = new String[] {MOUSE_ENTER, MOUSE_EXIT};

	String MOUSE_DOUBLE_CLICK = "MouseDoubleClick";

	String PAINT = "Paint";
	String MOVE = "Move";
	String RESIZE = "Resize";
	String DISPOSE = "Dispose";
	
	String SELECTION = "Selection";
	String DEFAULT_SELECTION = "DefaultSelection";

	String FOCUS_IN = "FocusIn";
	String FOCUS_OUT = "FocusOut";
	String[] FOCUS_GROUP = new String[] {FOCUS_IN, FOCUS_OUT};

	String EXPAND = "Expand";
	String COLLAPSE = "Collapse";
	String[] EXPAND_GROUP = new String[] {EXPAND, COLLAPSE};

	String ICONIFY = "Iconify";
	String DEICONIFY = "Deiconify";
	String CLOSE = "Close";
	String SHOW = "Show";
	String HIDE = "Hide";
	String[] WINDOW_GROUP = new String[] {ICONIFY, DEICONIFY, CLOSE, SHOW, HIDE};

	String MODIFY = "Modify";
	String VERIFY = "Verify";
	
	String ACTIVATE = "Activate";
	String DEACTIVATE = "Deactivate";
	String[] ACTIVATION_GROUP = new String[] {ACTIVATE, DEACTIVATE};

	String HELP = "Help";
	
	String DRAG_SELECT = "DragDetect";

	String ARM = "Arm";
	
	String TRAVERSE = "Traverse";

	String HARD_KEY_DOWN = "HardKeyDown";
	String HARD_KEY_UP = "HardKeyUp";
	String[] HARD_KEY = new String[] {HARD_KEY_DOWN, HARD_KEY_UP};

	String MENU_DETECT = "MenuDetect";

	String MOUSE_WHEEL = "MouseWheel";
	
	String SETTINGS = "Settings";

	String ERASE_ITEM = "EraseItem";
	String MEASURE_ITEM = "MeasureItem";

	String PAINT_ITEM = "PaintItem";
	String SET_DATA = "SetData";
	String IME_COMPOSITION = "ImeComposition";
}
