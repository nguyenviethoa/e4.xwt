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

import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FindByContributionURIDialog extends
		AbstractFindElementsDialog {

	public FindByContributionURIDialog(Shell shell,
			Object[] initializeElements) {
		super(shell, initializeElements, "contribution URI:");
		setMessage("Find Element by contribution URI");
	}

	protected String getFilterForeignText(Object item) {
		String contributionURI = null;
		if (item instanceof MContribution) {
			contributionURI = ((MContribution) item).getContributionURI();
		}
		return contributionURI == null ? "" : contributionURI;
	}

}
