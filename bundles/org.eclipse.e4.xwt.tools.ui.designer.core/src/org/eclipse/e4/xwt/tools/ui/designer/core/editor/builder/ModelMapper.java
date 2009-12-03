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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class ModelMapper {

	private final Map<XamlNode, IDOMNode> model2text = new HashMap<XamlNode, IDOMNode>(1);
	private final Map<IDOMNode, XamlNode> text2model = new HashMap<IDOMNode, XamlNode>(1);

	public void map(XamlNode model, IDOMNode textNode) {
		model2text.put(model, textNode);
		text2model.put(textNode, model);
	}

	public void remove(Object obj) {
		IDOMNode remove = model2text.remove(obj);
		if (remove != null) {
			text2model.remove(remove);
		}
		XamlNode rev = text2model.remove(obj);
		if (rev != null) {
			model2text.remove(rev);
		}
	}

	public void clear() {
		model2text.clear();
		text2model.clear();
	}

	public XamlNode getModel(Object textNode) {
		return text2model.get(textNode);
	}

	public IDOMNode getTextNode(Object model) {
		return model2text.get(model);
	}
}
