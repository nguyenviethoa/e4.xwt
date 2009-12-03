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
package org.eclipse.e4.xwt.tools.ui.designer.editor.outline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineContentProvider;
import org.eclipse.e4.xwt.tools.ui.designer.parts.DataContextEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.policies.layout.grid.GridLayoutPolicyHelper;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.e4.xwt.tools.ui.xaml.tools.AnnotationTools;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class OutlinePageContentProvider extends OutlineContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.OutlineContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof RootEditPart) {
			EditPart contents = ((RootEditPart) inputElement).getContents();
			if (contents != null) {
				return getChildren(contents);
			}
		}
		return super.getElements(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.OutlineContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EditPart) {
			List<EditPart> children = new ArrayList<EditPart>();
			List editparts = ((EditPart) parentElement).getChildren();
			for (Iterator iterator = editparts.iterator(); iterator.hasNext();) {
				EditPart editPart = (EditPart) iterator.next();
				if (isFiller(editPart) || editPart instanceof DataContextEditPart) {
					continue;
				}
				children.add(editPart);
			}
			return children.toArray(EMPTY);
		}
		return EMPTY;
	}

	private boolean isFiller(EditPart editPart) {
		Object model = editPart.getModel();
		if (model instanceof XamlNode) {
			return AnnotationTools.isAnnotated((XamlNode) model, GridLayoutPolicyHelper.FILLER_DATA);
		}
		return false;
	}
}
