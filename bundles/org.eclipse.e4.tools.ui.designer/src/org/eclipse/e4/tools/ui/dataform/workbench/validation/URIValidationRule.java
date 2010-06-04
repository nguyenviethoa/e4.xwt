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

import org.eclipse.e4.ui.internal.workbench.Activator;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.tools.ui.dataform.DataForms;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class URIValidationRule extends BasicValidationRule {

	public IStatus validate(Object value) {
		IStatus status = null;
		if (DataForms.isRequiredSF(getFeature())) {
			status = validateNull(value);
		} else if (value == null || "".equals(value)) {
			return ValidationStatus.ok();// If not required, null is acceptable.
		}
		if (status != null && !status.isOK()) {
			return status;
		}
		status = validateURI(URI.createURI((String) value));
		return status;
	}

	public IStatus validateBack(Object value) {
		IStatus status = null;
		if (DataForms.isRequiredSF(getFeature())) {
			status = validateNull(value);
		} else if (value == null || "".equals(value)) {
			return ValidationStatus.ok();// If not required, null is acceptable.
		}
		if (status != null && !status.isOK()) {
			return status;
		}
		status = validateURI(URI.createURI((String) value));
		return status;
	}

	private IStatus validateURI(URI uri) {
		String featureName = getFeatureName();
		if (uri == null || uri.isEmpty() || !uri.isPlatform()) {
			return ValidationStatus.error("Invalid \'" + featureName + "\': "
					+ uri);
		} else {
			Bundle bundle = getBundle(uri);
			if (uri.segmentCount() < 2) {
				return ValidationStatus.error("Invalid \'" + featureName
						+ "\': " + uri);
			} else if (uri.segmentCount() > 3) {
				ImageDescriptor image = null;
				try {
					URL url = new URL(uri.toString());
					InputStream stream = url.openStream();
					image = ImageDescriptor.createFromURL(url);
					stream.close();
				} catch (Exception e) {
				}
				if (image == null) {
					return ValidationStatus.error("Invalid \'" + featureName
							+ "\': " + uri);
				}
			} else {
				String resource = uri.segment(2);
				try {
					bundle.loadClass(resource);
				} catch (ClassNotFoundException e) {
					return ValidationStatus.error("Invalid \'" + featureName
							+ "\': " + "Resource not found from \'" + resource
							+ "\'");
				}
			}

		}
		return ValidationStatus.ok();
	}

	protected IStatus validateNull(Object value) {
		if (value == null || "".equals(value) || !(value instanceof String)) {
			return ValidationStatus.error("Invalid \'" + getFeatureName()
					+ "\'.");
		}
		return ValidationStatus.ok();
	}

	protected Bundle getBundle(URI platformURI) {
		return Activator.getDefault().getBundleForName(platformURI.segment(1));
	}
}
