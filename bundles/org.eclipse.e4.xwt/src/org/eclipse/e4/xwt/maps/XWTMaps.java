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

package org.eclipse.e4.xwt.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;

/**
 * @author jliu
 */
public class XWTMaps {
	private static final Map<String, Integer> styles = new HashMap<String, Integer>();
	private static final Map<String, Integer> colors = new HashMap<String, Integer>();
	private static final Map<String, Integer> events = new HashMap<String, Integer>();
	private static final Map<String, Integer> accelerators = new HashMap<String, Integer>();

	private XWTMaps() {
	}

	private static void checkAndInit() {
		if (styles.isEmpty() || colors.isEmpty() || events.isEmpty() || accelerators.isEmpty()) {
			init();
		}
	}

	private static void init() {
		// styles
		styles.put("SWT.NONE", SWT.NONE);
		styles.put("SWT.FILL", SWT.FILL);
		styles.put("SWT.BORDER", SWT.BORDER);
		styles.put("SWT.PUSH", SWT.PUSH);
		styles.put("SWT.CHECK", SWT.CHECK);
		styles.put("SWT.RADIO", SWT.RADIO);
		styles.put("SWT.TOGGLE", SWT.TOGGLE);
		styles.put("SWT.ARROW", SWT.ARROW);
		styles.put("SWT.ARROW_DOWN", SWT.ARROW_DOWN);
		styles.put("SWT.ARROW_LEFT", SWT.ARROW_LEFT);
		styles.put("SWT.ARROW_RIGHT", SWT.ARROW_RIGHT);
		styles.put("SWT.ARROW_UP", SWT.ARROW_UP);
		styles.put("SWT.FLAT", SWT.FLAT);
		styles.put("SWT.DROP_DOWN", SWT.DROP_DOWN);
		styles.put("SWT.SIMPLE", SWT.SIMPLE);
		styles.put("SWT.READ_ONLY", SWT.READ_ONLY);
		styles.put("SWT.LEFT", SWT.LEFT);
		styles.put("SWT.UP", SWT.UP);
		styles.put("SWT.DOWN", SWT.DOWN);
		styles.put("SWT.CENTER", SWT.CENTER);
		styles.put("SWT.RIGHT", SWT.RIGHT);
		styles.put("SWT.H_SCROLL", SWT.H_SCROLL);
		styles.put("SWT.V_SCROLL", SWT.V_SCROLL);
		styles.put("SWT.DATE", SWT.DATE);
		styles.put("SWT.TIME", SWT.TIME);
		styles.put("SWT.CALENDAR", SWT.CALENDAR);
		styles.put("SWT.SHORT", SWT.SHORT);
		styles.put("SWT.MEDIUM", SWT.MEDIUM);
		styles.put("SWT.LONG", SWT.LONG);
		styles.put("SWT.WRAP", SWT.WRAP);
		styles.put("SWT.HORIZONTAL", SWT.HORIZONTAL);
		styles.put("SWT.VERTICAL", SWT.VERTICAL);
		styles.put("SWT.SEPARATOR", SWT.SEPARATOR);
		styles.put("SWT.SHADOW_IN", SWT.SHADOW_IN);
		styles.put("SWT.SHADOW_NONE", SWT.SHADOW_NONE);
		styles.put("SWT.SHADOW_OUT", SWT.SHADOW_OUT);
		styles.put("SWT.NO_TRIM", SWT.NO_TRIM);
		styles.put("SWT.CLOSE", SWT.CLOSE);
		styles.put("SWT.MIN", SWT.MIN);
		styles.put("SWT.MAX", SWT.MAX);
		styles.put("SWT.RESIZE", SWT.RESIZE);
		styles.put("SWT.TOOL", SWT.TOOL);
		styles.put("SWT.ON_TOP", SWT.ON_TOP);
		styles.put("SWT.MODELESS", SWT.MODELESS);
		styles.put("SWT.PRIMARY_MODAL", SWT.PRIMARY_MODAL);
		styles.put("SWT.APPLICATION_MODAL", SWT.APPLICATION_MODAL);
		styles.put("SWT.SYSTEM_MODAL", SWT.SYSTEM_MODAL);
		styles.put("SWT.TITLE", SWT.TITLE);
		styles.put("SWT.SINGLE", SWT.SINGLE);
		styles.put("SWT.MULTI", SWT.MULTI);
		styles.put("SWT.PASSWORD", SWT.PASSWORD);
		styles.put("SWT.SEARCH", SWT.SEARCH);
		styles.put("SWT.CANCEL", SWT.CANCEL);
		// styles: icon
		styles.put("SWT.ICON", SWT.ICON);
		styles.put("SWT.ICON_ERROR", SWT.ICON_ERROR);
		styles.put("SWT.ICON_INFORMATION", SWT.ICON_INFORMATION);
		styles.put("SWT.ICON_QUESTION", SWT.ICON_QUESTION);
		styles.put("SWT.ICON_WARNING", SWT.ICON_WARNING);
		styles.put("SWT.ICON_WORKING", SWT.ICON_WORKING);
		styles.put("SWT.SHADOW_ETCHED_IN", SWT.SHADOW_ETCHED_IN);
		styles.put("SWT.SHADOW_ETCHED_OUT", SWT.SHADOW_ETCHED_OUT);
		styles.put("SWT.BALLOON", SWT.BALLOON);
		styles.put("SWT.NO_BACKGROUND", SWT.NO_BACKGROUND);
		styles.put("SWT.NO_FOCUS", SWT.NO_FOCUS);
		styles.put("SWT.NO_MERGE_PAINTS", SWT.NO_MERGE_PAINTS);
		styles.put("SWT.NO_REDRAW_RESIZE", SWT.NO_REDRAW_RESIZE);
		styles.put("SWT.DOUBLE_BUFFERED", SWT.DOUBLE_BUFFERED);
		styles.put("SWT.SMOOTH", SWT.SMOOTH);
		styles.put("SWT.INDETERMINATE", SWT.INDETERMINATE);
		styles.put("SWT.FULL_SELECTION", SWT.FULL_SELECTION);
		styles.put("SWT.TOP", SWT.TOP);
		styles.put("SWT.BOTTOM", SWT.BOTTOM);
		styles.put("SWT.NORMAL", SWT.NORMAL);
		styles.put("SWT.ITALIC", SWT.ITALIC);
		styles.put("SWT.BOLD", SWT.BOLD);
		styles.put("SWT.LEFT_TO_RIGHT", SWT.LEFT_TO_RIGHT);
		styles.put("SWT.RIGHT_TO_LEFT", SWT.RIGHT_TO_LEFT);
		styles.put("SWT.BAR", SWT.BAR);
		styles.put("SWT.CASCADE", SWT.CASCADE);
		styles.put("SWT.NO_RADIO_GROUP", SWT.NO_RADIO_GROUP);
		styles.put("SWT.POP_UP", SWT.POP_UP);
		styles.put("SWT.INHERIT_DEFAULT", SWT.INHERIT_DEFAULT);
		styles.put("SWT.INHERIT_NONE", SWT.INHERIT_NONE);
		styles.put("SWT.INHERIT_FORCE", SWT.INHERIT_FORCE);

		// colors
		colors.put("SWT.COLOR_BLACK", SWT.COLOR_BLACK);
		colors.put("SWT.COLOR_BLUE", SWT.COLOR_BLUE);
		colors.put("SWT.COLOR_CYAN", SWT.COLOR_CYAN);
		colors.put("SWT.COLOR_DARK_BLUE", SWT.COLOR_DARK_BLUE);
		colors.put("SWT.COLOR_DARK_CYAN", SWT.COLOR_DARK_CYAN);
		colors.put("SWT.COLOR_DARK_GREEN", SWT.COLOR_DARK_GREEN);
		colors.put("SWT.COLOR_DARK_MAGENTA", SWT.COLOR_DARK_MAGENTA);
		colors.put("SWT.COLOR_DARK_RED", SWT.COLOR_DARK_RED);
		colors.put("SWT.COLOR_DARK_YELLOW", SWT.COLOR_DARK_YELLOW);
		colors.put("SWT.COLOR_GRAY", SWT.COLOR_GRAY);
		colors.put("SWT.COLOR_GREEN", SWT.COLOR_GREEN);
		colors.put("SWT.COLOR_INFO_BACKGROUND", SWT.COLOR_INFO_BACKGROUND);
		colors.put("SWT.COLOR_INFO_FOREGROUND", SWT.COLOR_INFO_FOREGROUND);
		colors.put("SWT.COLOR_LIST_BACKGROUND", SWT.COLOR_LIST_BACKGROUND);
		colors.put("SWT.COLOR_LIST_FOREGROUND", SWT.COLOR_LIST_FOREGROUND);
		colors.put("SWT.COLOR_LIST_SELECTION", SWT.COLOR_LIST_SELECTION);
		colors.put("SWT.COLOR_LIST_SELECTION_TEXT", SWT.COLOR_LIST_SELECTION_TEXT);
		colors.put("SWT.COLOR_MAGENTA", SWT.COLOR_MAGENTA);
		colors.put("SWT.COLOR_RED", SWT.COLOR_RED);
		colors.put("SWT.COLOR_TITLE_BACKGROUND", SWT.COLOR_TITLE_BACKGROUND);
		colors.put("SWT.COLOR_TITLE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		colors.put("SWT.COLOR_TITLE_FOREGROUND", SWT.COLOR_TITLE_FOREGROUND);
		colors.put("SWT.COLOR_TITLE_INACTIVE_BACKGROUND", SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		colors.put("SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
		colors.put("SWT.COLOR_TITLE_INACTIVE_FOREGROUND", SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
		colors.put("SWT.COLOR_WHITE", SWT.COLOR_WHITE);
		colors.put("SWT.COLOR_WIDGET_BACKGROUND", SWT.COLOR_WIDGET_BACKGROUND);
		colors.put("SWT.COLOR_WIDGET_BORDER", SWT.COLOR_WIDGET_BORDER);
		colors.put("SWT.COLOR_WIDGET_DARK_SHADOW", SWT.COLOR_WIDGET_DARK_SHADOW);
		colors.put("SWT.COLOR_WIDGET_FOREGROUND", SWT.COLOR_WIDGET_FOREGROUND);
		colors.put("SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW", SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		colors.put("SWT.COLOR_WIDGET_LIGHT_SHADOW", SWT.COLOR_WIDGET_LIGHT_SHADOW);
		colors.put("SWT.COLOR_WIDGET_NORMAL_SHADOW", SWT.COLOR_WIDGET_NORMAL_SHADOW);
		colors.put("SWT.COLOR_YELLOW", SWT.COLOR_YELLOW);

		// Events
		events.put("swt.activate", SWT.Activate);
		events.put("swt.arm", SWT.Arm);
		events.put("swt.close", SWT.Close);
		events.put("swt.collapse", SWT.Collapse);
		events.put("swt.deactivate", SWT.Deactivate);
		events.put("swt.defaultselection", SWT.DefaultSelection);
		events.put("swt.deiconify", SWT.Deiconify);
		events.put("swt.dispose", SWT.Dispose);
		events.put("swt.dragdetect", SWT.DragDetect);
		events.put("swt.eraseitem", SWT.EraseItem);
		events.put("swt.expand", SWT.Expand);
		events.put("swt.focusin", SWT.FocusIn);
		events.put("swt.focusout", SWT.FocusOut);
		events.put("swt.hardkeydown", SWT.HardKeyDown);
		events.put("swt.hardkeyup", SWT.HardKeyUp);
		events.put("swt.help", SWT.Help);
		events.put("swt.hide", SWT.Hide);
		events.put("swt.iconify", SWT.Iconify);
		events.put("swt.keydown", SWT.KeyDown);
		events.put("swt.keyup", SWT.KeyUp);
		events.put("swt.measureitem", SWT.MeasureItem);
		events.put("swt.menudetect", SWT.MenuDetect);
		events.put("swt.modify", SWT.Modify);
		events.put("swt.mousedoubleclick", SWT.MouseDoubleClick);
		events.put("swt.mousedown", SWT.MouseDown);
		events.put("swt.mouseenter", SWT.MouseEnter);
		events.put("swt.mouseexit", SWT.MouseExit);
		events.put("swt.mousehover", SWT.MouseHover);
		events.put("swt.mousemove", SWT.MouseMove);
		events.put("swt.mouseup", SWT.MouseUp);
		events.put("swt.mousewheel", SWT.MouseWheel);
		events.put("swt.move", SWT.Move);
		events.put("swt.paint", SWT.Paint);
		events.put("swt.paintitem", SWT.PaintItem);
		events.put("swt.resize", SWT.Resize);
		events.put("swt.selection", SWT.Selection);
		events.put("swt.setdata", SWT.SetData);
		events.put("swt.settings", SWT.Settings);
		events.put("swt.show", SWT.Show);
		events.put("swt.traverse", SWT.Traverse);
		events.put("swt.verify", SWT.Verify);
		events.put("swt.imecomposition", SWT.ImeComposition);
		
		// accelerators
		accelerators.put("SWT.ALT", SWT.ALT);
		accelerators.put("SWT.ARROW_UP", SWT.ARROW_UP);
		accelerators.put("SWT.ARROW_DOWN", SWT.ARROW_DOWN);
		accelerators.put("SWT.ARROW_LEFT", SWT.ARROW_LEFT);
		accelerators.put("SWT.ARROW_RIGHT", SWT.ARROW_RIGHT);
		accelerators.put("SWT.BREAK", SWT.BREAK);
		accelerators.put("SWT.CAPS_LOCK", SWT.CAPS_LOCK);
		accelerators.put("SWT.CENTER", SWT.CENTER);
		accelerators.put("SWT.CTRL", SWT.CTRL);
		accelerators.put("SWT.DEFAULT", SWT.DEFAULT);
		accelerators.put("SWT.EMBEDDED", SWT.EMBEDDED);
		accelerators.put("SWT.END", SWT.END);
		accelerators.put("SWT.F1", SWT.F1);
		accelerators.put("SWT.F2", SWT.F2);
		accelerators.put("SWT.F3", SWT.F3);
		accelerators.put("SWT.F4", SWT.F4);
		accelerators.put("SWT.F5", SWT.F5);
		accelerators.put("SWT.F6", SWT.F6);
		accelerators.put("SWT.F7", SWT.F7);
		accelerators.put("SWT.F8", SWT.F8);
		accelerators.put("SWT.F9", SWT.F9);
		accelerators.put("SWT.F10", SWT.F10);
		accelerators.put("SWT.F11", SWT.F11);
		accelerators.put("SWT.F12", SWT.F12);
		accelerators.put("SWT.F13", SWT.F13);
		accelerators.put("SWT.F14", SWT.F14);
		accelerators.put("SWT.F15", SWT.F15);
		accelerators.put("SWT.HELP", SWT.HELP);
		accelerators.put("SWT.HOME", SWT.HOME);
		accelerators.put("SWT.IMAGE_UNDEFINED", SWT.IMAGE_UNDEFINED);
		accelerators.put("SWT.KEYPAD_ADD", SWT.KEYPAD_ADD);
		accelerators.put("SWT.KEYPAD_CR", SWT.KEYPAD_CR);
		accelerators.put("SWT.KEYPAD_DECIMAL", SWT.KEYPAD_DECIMAL);
		accelerators.put("SWT.KEYPAD_DIVIDE", SWT.KEYPAD_DIVIDE);
		accelerators.put("SWT.KEYPAD_EQUAL", SWT.KEYPAD_EQUAL);
		accelerators.put("SWT.KEYPAD_MULTIPLY", SWT.KEYPAD_MULTIPLY);
		accelerators.put("SWT.KEYPAD_SUBTRACT", SWT.KEYPAD_SUBTRACT);
		accelerators.put("SWT.KEYPAD_0", SWT.KEYPAD_0);
		accelerators.put("SWT.KEYPAD_1", SWT.KEYPAD_1);
		accelerators.put("SWT.KEYPAD_2", SWT.KEYPAD_2);
		accelerators.put("SWT.KEYPAD_3", SWT.KEYPAD_3);
		accelerators.put("SWT.KEYPAD_4", SWT.KEYPAD_4);
		accelerators.put("SWT.KEYPAD_5", SWT.KEYPAD_5);
		accelerators.put("SWT.KEYPAD_6", SWT.KEYPAD_6);
		accelerators.put("SWT.KEYPAD_7", SWT.KEYPAD_7);
		accelerators.put("SWT.KEYPAD_8", SWT.KEYPAD_8);
		accelerators.put("SWT.KEYPAD_9", SWT.KEYPAD_9);
		accelerators.put("SWT.NUM_LOCK", SWT.NUM_LOCK);
		accelerators.put("SWT.PAUSE", SWT.PAUSE);
		accelerators.put("SWT.PAGE_DOWN", SWT.PAGE_DOWN);
		accelerators.put("SWT.PAGE_UP", SWT.PAGE_UP);
		accelerators.put("SWT.PRINT_SCREEN", SWT.PRINT_SCREEN);
		accelerators.put("SWT.SCROLL_LOCK", SWT.SCROLL_LOCK);
		accelerators.put("SWT.SHIFT", SWT.SHIFT);	
		
	}

	public static Collection<String> getStyleKeys() {
		checkAndInit();
		return styles.keySet();
	}

	public static int getStyle(String key) {
		if (key == null) {
			return SWT.NONE;
		}
		checkAndInit();
		String mapKey = key.toUpperCase();
		if (!mapKey.startsWith("SWT.")) {
			mapKey = "SWT." + mapKey;
		}
		Integer style = styles.get(mapKey);
		return (style == null ? SWT.NONE : style);
	}

	public static Collection<String> getColorKeys() {
		checkAndInit();
		return colors.keySet();
	}

	public static int getColor(String key) {
		if (key == null || key.equals("")) {
			return SWT.NONE;
		}
		checkAndInit();
		String mapKey = key.toUpperCase();
		if (!mapKey.startsWith("SWT.")) {
			mapKey = "SWT." + mapKey;
		}
		Integer color = colors.get(mapKey);
		return color == null ? SWT.NONE : color;
	}

	public static Collection<String> getEventKeys() {
		checkAndInit();
		return events.keySet();
	}

	public static int getEvent(String key) {
		if (key == null || key.equals("")) {
			return SWT.None;
		}
		checkAndInit();
		String mapKey = key.toLowerCase();
		if (!mapKey.startsWith("swt.")) {
			mapKey = "swt." + mapKey;
		}
		Integer event = events.get(mapKey);
		return event == null ? SWT.None : event;
	}

	public static Collection<String> getAcceleratorKeys() {
		checkAndInit();
		return accelerators.keySet();
	}

	public static int getAccelerator(String key) {
		char letter;
		if (key == null || key.equals("")) {
			return SWT.NONE;
		}
		checkAndInit();
		String mapKey = key.toUpperCase();
		if(key.length() == 1) {
			letter = key.charAt(0);
			if(letter >= 'A' && letter <= 'Z' || letter >= 'a' && letter <= 'z') {
				Integer accelerator = Integer.valueOf(letter);
				return accelerator == null ? SWT.NONE : accelerator;
			} 
		}
		if (!mapKey.startsWith("SWT.")) {
			mapKey = "SWT." + mapKey;
		}
		
		Integer accelerator = accelerators.get(mapKey);
		return accelerator == null ? SWT.NONE : accelerator;
	}
	
	public static int getValue(String key) {
		int value = getStyle(key);
		if (value == SWT.NONE) {
			value = getColor(key);
		}
		if (value == SWT.NONE) {
			value = getEvent(key);
		}
		if (value == SWT.NONE) {
			value = getAccelerator(key);
		}
		return value;
	}

}
