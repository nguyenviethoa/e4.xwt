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
package org.eclipse.e4.tools.ui.dataform.workbench.validation;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class RequiredValidationRule extends BasicValidationRule {

	public IStatus validateBack(Object value) {
		if (value == null || value.toString().length() == 0) {
			String featureName = ApplicationModelHelper.getDisplayName(
					getTarget(), getFeature());
			if (featureName != null) {
				return ValidationStatus.error("\'" + featureName
						+ "' can not be empty.");
			}
			return ValidationStatus.error("Value is required");
		}
		return super.validateBack(value);
	}

	public IStatus validate(Object value) {
		if (value == null || value.toString().length() == 0) {
			String featureName = ApplicationModelHelper.getDisplayName(
					getTarget(), getFeature());
			if (featureName != null) {
				return ValidationStatus.error("\'" + featureName
						+ "' can not be empty.");
			}
			return ValidationStatus.error("Value is required");
		}
		return super.validate(value);
	}
}
