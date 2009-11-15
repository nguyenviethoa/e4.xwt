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
package org.eclipse.e4.xwt.emf;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;

public class EMFHelper {

	public static String getQualifiedName(ENamedElement namedElement) {
		EObject object = namedElement.eContainer();
		if (object instanceof ENamedElement) {
			String parentQN = getQualifiedName((ENamedElement) namedElement.eContainer());

			if (parentQN != null && !parentQN.equals("")) {
				return parentQN + "." + namedElement.getName();
			}
		}
		return namedElement.getName();
	}
	
	public static EClass toType(Object object) {
		EClass type = null;
		if (object instanceof EClass) {
			type = (EClass) object;
		}
		else if (object instanceof EObject) {
			EObject ecoreObject = (EObject) object;
			type = ecoreObject.eClass();
		}
		else {
			throw new IllegalStateException();
		}
		return type;
	}
}
