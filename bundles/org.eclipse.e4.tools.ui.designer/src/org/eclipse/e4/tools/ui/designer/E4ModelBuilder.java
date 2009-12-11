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
package org.eclipse.e4.tools.ui.designer;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.impl.ApplicationImpl;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.AbstractModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4ModelBuilder extends AbstractModelBuilder {

	private ApplicationImpl theApp = null;
	private Adapter refresher = new AdapterImpl() {
		public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
			if (msg.getNewValue() instanceof EObject) {
				addNotify((EObject) msg.getNewValue());
			}
			fireChangeEvent(msg);
		};
	};
	private Resource resource;

	public boolean doLoad(Designer designer, IProgressMonitor monitor) {
		IFile inputFile = designer.getInputFile();
		URI uri = URI.createFileURI(inputFile.getLocation().toString());
		resource = new ResourceSetImpl().getResource(uri, true);
		theApp = (ApplicationImpl) resource.getContents().get(0);
		addNotify(theApp);
		return theApp != null;
	}

	private void addNotify(EObject object) {
		if (object == null || object.eAdapters().contains(refresher)) {
			return;
		}
		object.eAdapters().add(refresher);
		EList<EStructuralFeature> features = object.eClass().getEAllStructuralFeatures();
		for (EStructuralFeature sf : features) {
			if (!object.eIsSet(sf)) {
				continue;
			}
			Object value = object.eGet(sf);
			if (value instanceof Collection) {
				for (Iterator iterator = ((Collection) value).iterator(); iterator.hasNext();) {
					Object obj = (Object) iterator.next();
					if (obj instanceof EObject) {
						addNotify((EObject) obj);
					}
				}
			} else if (value instanceof EObject) {
				addNotify((EObject) value);
			}
		}
	}

	private void removeNotify(EObject object) {
		if (object == null || !object.eAdapters().contains(refresher)) {
			return;
		}
		object.eAdapters().remove(refresher);
		EList<EStructuralFeature> features = object.eClass().getEAllStructuralFeatures();
		for (EStructuralFeature sf : features) {
			if (!object.eIsSet(sf)) {
				return;
			}
			Object value = object.eGet(sf);
			if (value instanceof EObject) {
				removeNotify((EObject) value);
			}
		}
	}

	public ApplicationImpl getDocumentRoot() {
		return theApp;
	}

	public void doSave(IProgressMonitor monitor) {
		if (resource != null) {
			try {
				resource.save(null);
			} catch (Exception e) {
			}
		}
	}

	public EObject getModel(Object textNode) {
		return null;
	}

	public IDOMNode getTextNode(Object model) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.IModelBuilder#dispose()
	 */
	public void dispose() {
		if (theApp != null) {
			removeNotify(theApp);
		}
	}

}
