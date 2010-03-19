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
package org.eclipse.e4.tools.ui.designer.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class WizardNewPart extends NewElementWizard {

	protected IFile fFile;
	protected MPart fPart;

	public WizardNewPart(IFile file, MPart part) {
		this.fFile = file;
		this.fPart = part;
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish && getCreatedElement() != null) {
			IType type = (IType) getCreatedElement();
			String elementName = type.getFullyQualifiedName();
			String projectName = type.getJavaProject().getElementName();
			String partURI = URI.createPlatformPluginURI(
					projectName + "/" + elementName, true).toString();
			fPart.setURI(partURI);
			fPart.setLabel(type.getElementName());
		}
		return performFinish;
	}
}
