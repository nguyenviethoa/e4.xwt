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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class EditDomain extends DefaultEditDomain {

	private Map<Object, Map<Object, Object>> viewerData;
	private Map<Object, Object> domainData;

	/**
	 * @param editorPart
	 */
	public EditDomain(IEditorPart editorPart) {
		super(editorPart);
	}

	/**
	 * @param graphicalViewer
	 * @param key
	 * @param zoomController
	 */
	public void setViewerData(GraphicalViewer viewer, Object key, Object data) {
		if (viewerData == null)
			viewerData = new HashMap<Object, Map<Object, Object>>(3);

		HashMap<Object, Object> vdata = (HashMap<Object, Object>) viewerData.get(viewer);
		if (vdata == null)
			viewerData.put(viewer, vdata = new HashMap<Object, Object>(3));
		vdata.put(key, data);
	}

	/**
	 * Get the data for the specified key for a particular viewer. Return null if the key is not set.
	 */
	public Object getViewerData(EditPartViewer viewer, Object key) {
		if (viewerData != null) {
			HashMap<Object, Object> data = (HashMap<Object, Object>) viewerData.get(viewer);
			if (data != null)
				return data.get(key);
		}
		return null;
	}

	public void setData(Object key, Object data) {
		if (domainData == null) {
			domainData = new HashMap<Object, Object>();
		}
		domainData.put(key, data);
	}

	public Object getData(Object key) {
		if (domainData == null) {
			return null;
		}
		return domainData.get(key);
	}

	public static EditDomain getEditDomain(EditPart ep) {
		if (ep != null) {
			return (EditDomain) ep.getViewer().getEditDomain();
		}
		return null;
	}
}