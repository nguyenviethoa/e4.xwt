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
package org.eclipse.e4.tools.ui.designer.widgets;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FindElementsByIdDialog extends AbstractFindElementsDialog {

	public FindElementsByIdDialog(Shell shell, Object[] initializeElements) {
		super(shell, initializeElements, "elementId:");
	}

	protected String getFilterForeignText(Object item) {
		String elementId = null;
		if (item instanceof MApplicationElement) {
			elementId = ((MApplicationElement) item).getElementId();
		}
		return elementId == null ? "" : elementId;
	}
}
