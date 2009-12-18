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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public abstract class AbstractModelBuilder implements IModelBuilder {

	private List<ModelBuildListener> listeners;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder#
	 * addModelBuildListener
	 * (org.eclipse.e4.xwt.tools.ui.designer.core.editor.ModelBuildListener)
	 */
	public void addModelBuildListener(ModelBuildListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ModelBuildListener>();
		}
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder#hasListener
	 * (org.eclipse.e4.xwt.tools.ui.designer.core.editor.ModelBuildListener)
	 */
	public boolean hasListener(ModelBuildListener listener) {
		return listeners != null && listeners.contains(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder#
	 * removeModelBuildListener
	 * (org.eclipse.e4.xwt.tools.ui.designer.core.editor.ModelBuildListener)
	 */
	public void removeModelBuildListener(ModelBuildListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	protected void fireChangeEvent(Notification event) {
		if (listeners != null) {
			for (ModelBuildListener l : listeners) {
				l.notifyChanged(event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder#dispose()
	 */
	public void dispose() {
		if (listeners != null) {
			listeners.clear();
		}
	}
}
