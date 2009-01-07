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
package org.eclipse.e4.xwt.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.maps.XWTMaps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author jliu
 */
public class ResourceManager {

	public static ResourceManager resources = new ResourceManager();

	private Map<String, Color> key2Colors = new HashMap<String, Color>();
	private Map<String, Font> key2Fonts = new HashMap<String, Font>();
	private Map<URL, Image> key2Images = new HashMap<URL, Image>();

	/**
	 * Default constructor.
	 */
	private ResourceManager() {
	}

	public Color getColor(String colorStr) {
		Color color = key2Colors.get(colorStr);
		if (color != null) {
			return color;
		}
		key2Colors.put(colorStr, color = ColorTool.getColor(colorStr));
		return color;
	}

	public Font getFont(String fontStr) {
		Font font = key2Fonts.get(fontStr);
		if (font != null) {
			return font;
		}
		key2Fonts.put(fontStr, font = FontTool.getFont(fontStr));
		return font;
	}

	public Image getImage(String imagePath) {
		if (imagePath == null) {
			return null;
		}
		Image image;
		try {
			URL file = new URL(imagePath);
			image = key2Images.get(file);
			if (image == null) {
				key2Images.put(file, image = ImageTool.getImage(file));
			}
		} catch (Exception e) {
			return null;
		}
		return image;
	}

	public void dispose() {

		// dispose colors.
		Collection<Color> colors = key2Colors.values();
		for (Color color : colors) {
			if (color != null) {
				color.dispose();
			}
		}
		key2Colors.clear();

		// dispose fonts.
		Collection<Font> fonts = key2Fonts.values();
		for (Font font : fonts) {
			if (font != null) {
				font.dispose();
			}
		}
		key2Colors.clear();

		// dispose images
		Collection<Image> images = key2Images.values();
		for (Image image : images) {
			if (image != null) {
				image.dispose();
			}
		}
	}

	static class ColorTool {
		static Color getColor(String colorStr) {
			if (colorStr.toLowerCase().startsWith("swt.")) {
				return getSWTColor(colorStr);
			} else if (colorStr.startsWith("#")) {
				try {
					int rgb = Integer.parseInt(colorStr.substring(1), 16);
					return getColor(rgb);
				} catch (NumberFormatException e) {
					return null;
				}
			} else if (colorStr.indexOf(",") != -1) {
				List<String> rgbs = new ArrayList<String>();
				StringTokenizer stk = new StringTokenizer(colorStr, ",");
				while (stk.hasMoreTokens()) {
					rgbs.add(stk.nextToken());
				}
				if (rgbs.size() == 3) {
					try {
						int r = Integer.parseInt(rgbs.get(0).trim());
						int g = Integer.parseInt(rgbs.get(1).trim());
						int b = Integer.parseInt(rgbs.get(2).trim());
						return getColor(r, g, b);
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}
			return getSWTColor(colorStr);
		}

		static Color getColor(int red, int green, int blue) {
			if (red > 255 || green > 255 || blue > 255 || red < 0 || green < 0
					|| blue < 0) {
				return null;
			}
			return new Color(Display.getDefault(), red, green, blue);
		}

		static Color getColor(int rgb) {
			int value = 0xff000000 | rgb;
			int red = (value >> 16) & 0xFF;
			int green = (value >> 8) & 0xFF;
			int blue = (value >> 0) & 0xFF;
			return getColor(red, green, blue);
		}

		static Color getSWTColor(String colorStr) {
			if (!(colorStr.toLowerCase().startsWith("swt."))) {
				if (!(colorStr.toLowerCase().startsWith("color_"))) {
					colorStr = "COLOR_" + colorStr;
				}
				colorStr = "SWT." + colorStr;
			}
			int swtColor = XWTMaps.getColor(colorStr);
			return getSWTColor(swtColor);
		}

		static Color getSWTColor(int swtValue) {
			return Display.getDefault().getSystemColor(swtValue);
		}
	}

	static class FontTool {

		static Font getFont(String fontStr) {
			if (fontStr.indexOf(",") != -1) {
				StringTokenizer stk = new StringTokenizer(fontStr, ",");
				String name = null;
				int height = 0;
				int style = SWT.NORMAL;
				while (stk.hasMoreTokens()) {
					String token = stk.nextToken().trim();
					if (token.equalsIgnoreCase("normal")
							|| token.equalsIgnoreCase("blod")
							|| token.equalsIgnoreCase("italic")
							|| token.contains("|")) {
						IConverter convertor = XWT.findConvertor(String.class,
								Integer.class);
						if (convertor != null) {
							style = (Integer) convertor.convert(token);
						}
					} else if (isInt(token)) {
						height = Integer.parseInt(token);
					} else {
						name = token;
					}
				}
				return getFont(name, height, style);
			}
			return getFont(fontStr, 12, SWT.NORMAL);
		}

		static boolean isInt(String value) {
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}

		static Font getFont(String name, int height, int style) {
			if (name == null) {
				return null;
			}
			return new Font(Display.getDefault(), name, height, style);
		}

		static Font getSWTFont() {
			return Display.getDefault().getSystemFont();
		}
	}

	static class ImageTool {

		static Image getImage(String resource) {
			return getImage(ResourceManager.class.getResource(resource));
		}

		static Image getImage(URL url) {
			if (url == null) {
				return null;
			}
			try {
				return new Image(Display.getDefault(), url.openStream());
			} catch (IOException e) {
				return null;
			}
		}

	}
}
