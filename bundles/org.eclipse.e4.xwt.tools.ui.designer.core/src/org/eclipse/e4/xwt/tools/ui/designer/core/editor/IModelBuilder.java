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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author bo.zhou
 * @author jliu
 */
public interface IModelBuilder {

	/**
	 * Load model from given designer.
	 */
	public boolean doLoad(Designer designer, IProgressMonitor monitor);

	/**
	 * Return loaded {@link XamlDocument};
	 */
	public EObject getDocumentRoot();

	public void addModelBuildListener(ModelBuildListener listener);

	public boolean hasListener(ModelBuildListener listener);

	public void removeModelBuildListener(ModelBuildListener listener);

	public void doSave(IProgressMonitor monitor);

	public EObject getModel(Object textNode);

	public IDOMNode getTextNode(Object model);

	public void dispose();
}
