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

import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MElementContainer;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4PaletteHelper {

	public static MUIElement createElement(MElementContainer container,
			Entry entry) {
		if (container == null || entry == null) {
			return null;
		}
		Object type = entry.getType();
		if (type != null && type instanceof EClass) {
			EObject element = MApplicationFactory.eINSTANCE
					.create((EClass) type);
			if (element instanceof MUIElement) {
				return verify(container, (MUIElement) element);
			}
		}
		return null;
	}

	private static MUIElement verify(MElementContainer container,
			MUIElement element) {
		// TODO: add some initialize values.
		// if (container instanceof MPartSashContainer) {
		// element.setContainerData("300");
		// }
		return element;
	}

	public static MUIElement createElement(MElementContainer container,
			CreateRequest request) {
		if (container == null || request == null) {
			return null;
		}
		Object newObject = request.getNewObject();
		if (newObject instanceof Entry) {
			return createElement(container, (Entry) newObject);
		}
		return null;
	}
}