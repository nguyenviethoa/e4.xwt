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
package org.eclipse.e4.tools.ui.designer.outline;

import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineContentProvider;
import org.eclipse.e4.xwt.tools.ui.designer.core.parts.root.DesignerRootEditPart;
import org.eclipse.gef.EditPart;

/**
 * @yyang <yves.yang@soyatec.com>
 */
public class E4DesignerOutlineContentProvider extends OutlineContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DesignerRootEditPart) {
			DesignerRootEditPart editPart = (DesignerRootEditPart) parentElement;
			List<EditPart> list = editPart.getChildren();
			if (list == null || list.isEmpty()) {
				return EMPTY;
			}
			parentElement = list.get(0);
		}
		return super.getChildren(parentElement);
	}
}
