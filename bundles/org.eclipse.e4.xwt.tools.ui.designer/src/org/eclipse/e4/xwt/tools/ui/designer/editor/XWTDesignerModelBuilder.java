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
package org.eclipse.e4.xwt.tools.ui.designer.editor;

import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.BraceHandler;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.DesignerModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.utils.XWTModelUtil;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class XWTDesignerModelBuilder extends DesignerModelBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.designer.editor.builder.DesignerModelBuilder#getAttribute(org.soyatec.tools.designer.xaml.XamlNode, java.lang.String, java.lang.String)
	 */
	protected XamlAttribute getAttribute(XamlNode parent, String attrName, String namespace) {
		return XWTModelUtil.getAdaptableAttribute(parent, attrName, namespace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder.DesignerModelBuilder#createBraceHandler(org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument)
	 */
	protected BraceHandler createBraceHandler(XamlDocument document) {
		return new XWTBraceHandler(document);
	}
}
