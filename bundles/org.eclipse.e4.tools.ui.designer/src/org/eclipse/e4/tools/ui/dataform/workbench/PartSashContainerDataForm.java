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
package org.eclipse.e4.tools.ui.dataform.workbench;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.e4.tools.ui.dataform.AbstractDataForm;
/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartSashContainerDataForm extends AbstractDataForm {

	public PartSashContainerDataForm(Composite parent, int style) {
		super(parent, style);
	}

	public void chooseSelectedElement(Object object, Event event) {
		handleEvent("selectedElement");
	}

	public void chooseTags(Object object, Event event) {
		handleEvent("tags");
	}

}
