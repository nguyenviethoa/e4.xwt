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
package org.eclipse.e4.xwt.tools.ui.designer.core.images;

/**
 * @author jliu jin.liu@soyatec.com
 */
public interface IImageNotifier {

	public void addImageListener(IImageListener listener);

	public void removeImageListener(IImageListener listener);

	public boolean hasImageListeners();

	public void refreshImage();

}
