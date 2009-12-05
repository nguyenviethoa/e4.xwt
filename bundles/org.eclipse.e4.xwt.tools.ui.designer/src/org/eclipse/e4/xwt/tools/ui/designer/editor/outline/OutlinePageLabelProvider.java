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

import java.text.MessageFormat;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.metadata.IMetaclass;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineLableProvider;
import org.eclipse.e4.xwt.tools.ui.designer.parts.DiagramEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.parts.ViewerEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.parts.WidgetEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.policies.layout.grid.GridLayoutPolicyHelper;
import org.eclipse.e4.xwt.tools.ui.designer.resources.ImageShop;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTUtility;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.e4.xwt.tools.ui.xaml.tools.AnnotationTools;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class OutlinePageLabelProvider extends OutlineLableProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.OutlineLableProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}
		if (element instanceof EditPart) {
			Object model = ((EditPart) element).getModel();
			if (model instanceof XamlNode) {
				return getText((XamlNode) model);
			}
			if (element instanceof DiagramEditPart) {
				return " ";
			}
		}
		return super.getText(element);
	}

	private String getText(XamlNode node) {
		if (AnnotationTools.isAnnotated(node, GridLayoutPolicyHelper.FILLER_DATA)) {
			return "(filler)";
		}
		String name = node.getName();
		IMetaclass metaclass = XWTUtility.getMetaclass(node);
		if (metaclass != null) {
			XamlAttribute textAttr = node.getAttribute("text");
			if (textAttr == null) {
				textAttr = node.getAttribute("text", IConstants.XWT_NAMESPACE);
			}
			XamlAttribute nameAttr = node.getAttribute("name", IConstants.XWT_NAMESPACE);
			if (nameAttr == null) {
				nameAttr = node.getAttribute("name", IConstants.XWT_X_NAMESPACE);
			}
			String value = null;
			if (nameAttr != null && nameAttr.getValue() != null) {
				value = MessageFormat.format("\"{0}\"", nameAttr.getValue());
			} else if (textAttr != null && textAttr.getValue() != null) {
				value = MessageFormat.format("\"{0}\"", textAttr.getValue());
			}
			if (value != null) {
				name = MessageFormat.format("{0} - {1}", new Object[] { name, value });
			}
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.outline.OutlineLableProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}
		if (element instanceof WidgetEditPart) {
			Widget widget = ((WidgetEditPart) element).getWidget();
			return ImageShop.getImageForWidget(widget);
		} else if (element instanceof ViewerEditPart) {
			Viewer viewer = ((ViewerEditPart) element).getJfaceViewer();
			if (viewer != null) {
				String name = viewer.getClass().getSimpleName().toLowerCase();
				return ImageShop.getObj16(name);
			}
		} else if (element instanceof DiagramEditPart) {
			return ImageShop.get(ImageShop.IMG_XWT);
		}
		return super.getImage(element);
	}
}
