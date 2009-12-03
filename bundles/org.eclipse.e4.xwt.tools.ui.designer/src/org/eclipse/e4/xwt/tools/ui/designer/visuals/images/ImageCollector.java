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
package org.eclipse.e4.xwt.tools.ui.designer.visuals.images;

import org.eclipse.e4.xwt.tools.ui.imagecapture.swt.ImageCapture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class ImageCollector {

	public static void collectImage(Control control, ImageCollectedRunnable imageRunnable) {
		if (control == null || control.isDisposed() || imageRunnable == null) {
			return;
		}
		Rectangle bounds = control.getBounds();
		if (bounds.isEmpty()) {
			imageRunnable.imageNotCollected();
		} else {
			Image image = null;
			// If the toolBar is located on a CoolBar, the background was lost by using print() method.
			if (control instanceof Shell || control instanceof ToolBar) {
				image = ImageCapture.getInstance().capture(control);
			} else {
				image = new Image(control.getDisplay(), bounds.width, bounds.height);
				GC gc = new GC(image);
				control.print(gc);
				gc.dispose();
			}
			if (image != null) {
				imageRunnable.imageCollected(image);
				final Image forDispose = image;
				control.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						forDispose.dispose();
					}
				});
			} else {
				imageRunnable.imageNotCollected();
			}
		}
	}

	private void saveImage(Image image, String path) {
		ImageLoader imageLoader = new ImageLoader();
		ImageData imageData = image.getImageData();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.IMAGE_JPEG);
	}

}
