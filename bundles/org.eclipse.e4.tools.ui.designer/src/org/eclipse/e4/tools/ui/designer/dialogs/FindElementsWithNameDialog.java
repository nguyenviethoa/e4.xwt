/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.dialogs;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FindElementsWithNameDialog extends AbstractFindElementsDialog {

	public FindElementsWithNameDialog(Shell shell, Object[] initializeElements) {
		super(shell, initializeElements, "elementName:");
		setMessage("Find Element With elementName");
	}

	protected String getFilterForeignText(Object item) {
		String elementName = "";
		if (item instanceof EObject) {
			EClass eClass = ((EObject) item).eClass();
			if (eClass != null) {
				elementName = eClass.getName();
			}
		}
		return elementName;
	}

}
