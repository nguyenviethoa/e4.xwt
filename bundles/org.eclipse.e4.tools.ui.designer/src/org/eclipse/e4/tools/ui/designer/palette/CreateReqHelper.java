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
package org.eclipse.e4.tools.ui.designer.palette;

import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Initializer;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class CreateReqHelper {

	public static final Object UNKNOWN_TYPE = new Object();

	private CreateRequest createReq;

	public CreateReqHelper(CreateRequest createReq) {
		this.createReq = createReq;
	}

	public MUIElement getNewObject() {
		Object newObject = createReq.getNewObject();
		if (newObject instanceof Entry) {
			Initializer initializer = ((Entry) newObject).getInitializer();
			if (initializer != null) {
				newObject = initializer.parse((Entry) newObject);
			}
		}
		if (newObject instanceof MUIElement) {
			return (MUIElement) newObject;
		}
		return null;
	}

	public String getNewObjectType() {
		Object type = createReq.getType();
		if (type != null) {
			return type.toString();
		}
		return null;
	}

	public static boolean canCreate(MUIElement parent, MUIElement child) {
		EObject container = (EObject) parent;
		for (EReference reference : container.eClass().getEReferences()) {
			EClassifier classifier = reference.getEType();
			if (classifier.isInstance(child)) {
				return true;
			}
		}
		return false;
	}

	public boolean canCreate(MUIElement parent) {
		return canCreate(parent, getNewObject());
	}
}
