package org.eclipse.e4.xwt.ui.utils;

import org.eclipse.swt.graphics.GC;

public class DisposeUtil {
	/**
	 * in MAC Cocoa, we should not dispose the GC for an image. The image will dispose
	 * the gc automatically.
	 * 
	 * @param gc
	 */
	public static void dispose(GC gc) {
		gc.dispose();
	}
}
