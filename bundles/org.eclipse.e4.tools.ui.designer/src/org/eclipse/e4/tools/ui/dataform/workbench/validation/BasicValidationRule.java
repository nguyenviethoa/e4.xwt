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
import org.eclipse.e4.xwt.validation.AbstractValidationRule;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class BasicValidationRule extends AbstractValidationRule {

	private EObject target;
	private String featureName;

	public IStatus validateBack(Object value) {
		return ValidationStatus.ok();
	}

	public IStatus validate(Object value) {
		return ValidationStatus.ok();
	}

	public void setTarget(EObject target) {
		this.target = target;
	}

	public EObject getTarget() {
		return target;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureName() {
		return featureName;
	}

	public EStructuralFeature getFeature() {
		if (target != null && featureName != null) {
			return target.eClass().getEStructuralFeature(featureName);
		}
		return null;
	}
}
