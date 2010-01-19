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
package org.eclipse.e4.tools.ui.designer.palette;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class EntryHelper {

	public static final String CURSOR_CONSTANTS = "${cursor}";
	public static final String ANN_CURSOR_DATA = "CURSOR_DATA_ANN";

	private static Map<Entry, MUIElement> nodes = new HashMap<Entry, MUIElement>();

	public static MUIElement getNode(Entry entry) {
		if (entry == null) {
			return null;
		}
		MUIElement node = nodes.get(entry);
		if (node == null || node.getParent() != null) {
			try {
				node = new EntryHelper().createNode(entry);
				nodes.put(entry, node);
			} catch (Exception e) {
				// LoggerManager.log(e);
			}
		}
		return (MUIElement) EcoreUtil.copy((EObject)node);
	}

	public static MUIElement getNode(CreateRequest createReq) {
		Object newObject = createReq.getNewObject();
		if (newObject instanceof Entry) {
			return getNode((Entry) newObject);
		}
		return null;
	}

	private MUIElement createNode(Entry entry) {
		if (entry == null || entry.getContent() == null) {
			return null;
		}
		return null;
	}
}
