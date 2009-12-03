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
package org.eclipse.e4.xwt.tools.ui.designer.visuals;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.images.ImageCollectedRunnable;
import org.eclipse.e4.xwt.tools.ui.designer.visuals.images.ImageCollector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class ControlVisualInfo extends WidgetVisualInfo {

	private Image image;

	public ControlVisualInfo(Control control) {
		super(control);
	}

	public boolean isRightToLeft() {
		int style = getControl().getStyle();
		return ((style & SWT.RIGHT_TO_LEFT) != 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.xaml.ve.editor.images.IImageNotifier#refreshImage()
	 */
	public void refreshImage() {
		if (image != null) {
			image.dispose();
		}
		Control control = getControl();
		if (control == null || control.isDisposed() || isExclude()) {
			return;
		}
		if (!(control instanceof Shell) && !control.getVisible()) {
			notifyImageChanged(null);
			return;
		}
		ImageCollector.collectImage(control, new ImageCollectedRunnable() {
			public void imageCollected(Image newImage) {
				image = newImage;
				notifyImageChanged(image);
			}

			public void imageNotCollected() {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.visuals.WidgetVisualInfo#getBounds()
	 */
	public Rectangle getBounds() {
		if (isExclude()) {
			return new Rectangle(0, 0, 0, 0);
		}
		return super.getBounds();
	}

	private boolean isExclude() {
		Control control = getControl();
		if (control == null || control.isDisposed() || control.getLayoutData() == null) {
			return false;
		}
		Object layoutData = control.getLayoutData();
		return (layoutData instanceof GridData && ((GridData) layoutData).exclude) || (layoutData instanceof RowData && ((RowData) layoutData).exclude);
	}

	/**
	 * @return the control
	 */
	public Control getControl() {
		return (Control) getVisualable();
	}
}
