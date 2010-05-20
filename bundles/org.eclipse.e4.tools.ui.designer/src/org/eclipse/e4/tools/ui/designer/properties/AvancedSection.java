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
package org.eclipse.e4.tools.ui.designer.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AvancedSection extends AdvancedPropertySection {

	public void createControls(Composite parent,
			TabbedPropertySheetPage propertyPage) {
		super.createControls(parent, propertyPage);
		page.setPropertySourceProvider(ApplicationModelHelper
				.getContentProvider());
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		List<Object> models = new ArrayList<Object>();
		if (selection != null && !selection.isEmpty()
				&& selection instanceof StructuredSelection) {
			Object[] array = ((StructuredSelection) selection).toArray();
			for (Object object : array) {
				if (object instanceof EditPart) {
					models.add(((EditPart) object).getModel());
				}
			}
		}
		super.setInput(part, new StructuredSelection(models));
	}
}
