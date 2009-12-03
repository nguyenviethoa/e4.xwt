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

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class AbstractRenderer implements IVisualRenderer {

	private IFile file;
	private XamlDocument document;

	public AbstractRenderer(IFile file, XamlDocument document) {
		this.file = file;
		this.document = document;
	}

	public IFile getFile() {
		return file;
	}

	public XamlDocument getDocument() {
		return document;
	}
}
