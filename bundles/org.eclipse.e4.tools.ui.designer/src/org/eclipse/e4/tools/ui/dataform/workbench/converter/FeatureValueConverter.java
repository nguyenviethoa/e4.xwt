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
package org.eclipse.e4.tools.ui.dataform.workbench.converter;

import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class FeatureValueConverter implements IValueConverter {

	private EObject target;
	private String featureName;

	public Object getFromType() {
		EStructuralFeature feature = getFeature();
		if (feature != null) {
			return feature.getEType();
		}
		return Object.class;
	}

	public Object getToType() {
		return String.class;
	}

	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;// quickly failed.
		}
		if (fromObject instanceof EObject) {
			String text = ApplicationModelHelper.getLabelProvider().getText(
					fromObject);
			if (text != null) {
				return text;
			}
		} else {
			EStructuralFeature feature = getFeature();
			if (feature != null && feature.getEType() instanceof EDataType
					&& fromObject instanceof String) {
				return EcoreUtil.convertToString(
						(EDataType) feature.getEType(), fromObject);
			}
		}
		return fromObject == null ? "" : fromObject.toString();
	}

	public Object convertBack(Object value) {
		if (value == null) {
			return null;// quickly failed.
		}
		EStructuralFeature feature = getFeature();
		if (feature != null && value instanceof String) {
			String strValue = (String) value;
			if ("[]".equals(strValue)) {
				return target.eGet(feature);
			} else if (feature.getEType() instanceof EDataType) {
				return EcoreUtil.createFromString((EDataType) feature
						.getEType(), strValue);
			}
		}
		return value;
	}

	public EStructuralFeature getFeature() {
		if (target != null && featureName != null) {
			return target.eClass().getEStructuralFeature(featureName);
		}
		return null;
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
}
