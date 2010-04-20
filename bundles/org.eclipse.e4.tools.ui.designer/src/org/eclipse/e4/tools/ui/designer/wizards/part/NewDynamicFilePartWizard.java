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
package org.eclipse.e4.tools.ui.designer.wizards.part;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewDynamicFilePartWizard extends WizardNewPart {

	private PartDataContext dataContext;
	private MApplication application;

	public NewDynamicFilePartWizard(IFile file, MPart part,
			MApplication application) {
		super(file, part);
		this.application = application;
	}

	public void addPages() {
		dataContext = new PartDataContext();
		NewDynamicFileSelectionWizardPage newSelectionPage = new NewDynamicFileSelectionWizardPage(
				dataContext);
		addPage(newSelectionPage);

		NewDynamicFilePartWizardPage newTypePage = new NewDynamicFilePartWizardPage(
				dataContext);
		newTypePage.init(new StructuredSelection(fFile));
		addPage(newTypePage);
		setNewTypeWizardPage(newTypePage);
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish && application != null
				&& dataContext.hasMasterProperties()) {
			// try to add this variable, so that, the selection changed event
			// will dispatch to all sub contexts, otherwise not.
			List<MWindow> children = application.getChildren();
			if (children.isEmpty()) {
				application.getVariables().add(IServiceConstants.SELECTION);
			} else {
				for (MWindow mWindow : children) {
					mWindow.getVariables().add(IServiceConstants.SELECTION);
				}
			}
		}
		return performFinish;
	}
}
