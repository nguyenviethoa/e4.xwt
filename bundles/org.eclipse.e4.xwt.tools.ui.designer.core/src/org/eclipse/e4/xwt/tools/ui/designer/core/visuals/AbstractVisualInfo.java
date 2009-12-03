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
package org.eclipse.e4.xwt.tools.ui.designer.core.visuals;

import org.eclipse.e4.xwt.tools.ui.designer.core.images.IImageListener;
import org.eclipse.e4.xwt.tools.ui.designer.core.images.ImageNotifierSupport;
import org.eclipse.swt.graphics.Image;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class AbstractVisualInfo implements IVisualInfo {

	private Object visualable;
	private ImageNotifierSupport imageSupport = new ImageNotifierSupport();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.images.IImageNotifier#addImageListener(org.soyatec.tools.designer.images.IImageListener)
	 */
	public void addImageListener(IImageListener listener) {
		imageSupport.addImageListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.images.IImageNotifier#hasImageListeners()
	 */
	public boolean hasImageListeners() {
		return imageSupport.hasImageListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.images.IImageNotifier#refreshImage()
	 */
	public void refreshImage() {
		// do nothing here.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.images.IImageNotifier#removeImageListener(org.soyatec.tools.designer.images.IImageListener)
	 */
	public void removeImageListener(IImageListener listener) {
		imageSupport.removeImageListener(listener);
	}

	protected void notifyImageChanged(Image image) {
		imageSupport.fireImageChanged(image);
	}

	public void setVisualable(Object visualable) {
		this.visualable = visualable;
	}

	public Object getVisualable() {
		return visualable;
	}

}
