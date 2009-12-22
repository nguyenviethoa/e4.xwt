package org.eclipse.ve.internal.swt.targetvm.unix;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.GC;

public class DisposeUtil {

	/**
	 * in MAC Cocoa, we should not dispose the GC for an image. The image will dispose
	 * the gc automatically.
	 * 
	 * @param gc
	 */
	public static void dispose(GC gc) {
		if (!Platform.getWS().equals(Platform.WS_COCOA)) {
			gc.dispose();
		}
	}
}
