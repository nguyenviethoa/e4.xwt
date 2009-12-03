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
package org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.dnd;

import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class OutlineNodeTransfer extends LocalSelectionTransfer {
	public static final OutlineNodeTransfer INSTANCE = new OutlineNodeTransfer();

	private LocalSelectionTransfer jfaceTransfer = LocalSelectionTransfer.getTransfer();

	private XamlNode node;

	private OutlineNodeTransfer() {
	}

	public static OutlineNodeTransfer getTransfer() {
		return INSTANCE;
	}

	public void setNode(XamlNode node) {
		this.node = node;
		if (node != null) {
			StructuredSelection selection = new StructuredSelection(node);
			jfaceTransfer.setSelection(selection);
		} else {
			jfaceTransfer.setSelection(null);
		}
	}

	public XamlNode getNode() {
		if (node != null) {
			return node;
		}
		IStructuredSelection selection = (IStructuredSelection) jfaceTransfer.getSelection();
		if (selection == null) {
			return null;
		}
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof XamlNode) {
			return node = (XamlNode) firstElement;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.LocalSelectionTransfer#getSelection()
	 */
	public ISelection getSelection() {
		ISelection selection = super.getSelection();
		if (selection == null) {
			selection = jfaceTransfer.getSelection();
		}
		return selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.LocalSelectionTransfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	public Object nativeToJava(TransferData transferData) {
		return jfaceTransfer.nativeToJava(transferData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.LocalSelectionTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	public void javaToNative(Object object, TransferData transferData) {
		jfaceTransfer.javaToNative(object, transferData);
	}
}
