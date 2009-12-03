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
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.AbstractVisualInfo;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class ViewerVisualInfo extends AbstractVisualInfo {

	private ControlVisualInfo delegate = null;

	public ViewerVisualInfo(Viewer viewer) {
		if (viewer != null) {
			delegate = new ControlVisualInfo(viewer.getControl());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.visuals.IVisualInfo#getBounds()
	 */
	public Rectangle getBounds() {
		if (delegate != null) {
			return delegate.getBounds();
		}
		return new Rectangle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.visuals.IVisualInfo#getClientArea()
	 */
	public Rectangle getClientArea() {
		if (delegate != null) {
			return delegate.getClientArea();
		}
		return new Rectangle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.visuals.AbstractVisualInfo#setVisualable(java.lang.Object)
	 */
	public void setVisualable(Object visualable) {
		if (visualable instanceof Viewer) {
			delegate.setVisualable(((Viewer) visualable).getControl());
		}
		super.setVisualable(visualable);
	}
}
