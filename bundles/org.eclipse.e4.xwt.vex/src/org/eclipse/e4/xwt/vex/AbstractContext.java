/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.vex;

import org.eclipse.e4.xwt.vex.toolpalette.ContextType;
import org.eclipse.e4.xwt.vex.toolpalette.Entry;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractContext implements VEXContext {

	public int findDropPosition(Node node, Entry entry, int documentPosition) {
		String scope = entry.getScope();
		ContextType contextType = entry.getContext();

		return findDropPosition(node, scope, contextType, documentPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.vex.VEXContext#findDropPosition(org.w3c.dom.Node, java.lang.String, org.eclipse.e4.xwt.vex.toolpalette.ContextType, int)
	 */
	public int findDropPosition(Node node, String scope, ContextType contextType, int cursorPosition) {
		IDOMNode treeNode = (IDOMNode) node;
		// check scope first
		if (scope != null) {
			String scopeName = scope.trim();
			if (scopeName.length() > 0) {
				Node parentNode = treeNode.getParentNode();
				if (parentNode == null || !isKindOf(parentNode, scopeName)) {
					return -1;
				}
			}
		}

		switch (contextType) {
		case XML_ALL:
			break;
		case XML_ATTRIBUTE:
			break;
		case XML_ATTRIBUTE_VALUE: {
			ITextRegion completionRegion = VEXTextEditorHelper.getCompletionRegion(cursorPosition, treeNode);

			String regionType = completionRegion.getType();

			if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				return ((IDOMNode) treeNode).getStartOffset();
			}
			return -1;
		}
		case NONE:
			break;
		case XML_NEW:
			break;
		case XML_TAG: {
			if (treeNode.getNodeType() == Node.TEXT_NODE) {
				return treeNode.getStartOffset();
			}

			// Node node = (Node) treeNode;
			// while ((node != null) && (node.getNodeType() == Node.TEXT_NODE)
			// && (node.getParentNode() != null)) {
			// node = node.getParentNode();
			// }
			// IDOMNode xmlnode = treeNode;

			ITextRegion completionRegion = VEXTextEditorHelper.getCompletionRegion(cursorPosition, treeNode);

			String regionType = completionRegion.getType();

			if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE || regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS || regionType == DOMRegionContext.XML_TAG_NAME) {
				// in case of attribute, find the first text node
				NodeList nodeList = treeNode.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node child = nodeList.item(i);
					if (child.getNodeType() == Node.TEXT_NODE) {
						return ((IDOMNode) child).getEndOffset();
					}
				}
			}
		}
			return -1;
		default:
			throw new UnsupportedOperationException();
		}

		return cursorPosition;
	}

	/**
	 * 
	 * @param node
	 * @param targetType
	 *            a qualified class name
	 * @return
	 */
	protected abstract boolean isKindOf(Node node, String targetType);
}
