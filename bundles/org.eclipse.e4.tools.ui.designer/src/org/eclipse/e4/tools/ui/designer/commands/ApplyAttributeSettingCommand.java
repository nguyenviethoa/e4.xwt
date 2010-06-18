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
package org.eclipse.e4.tools.ui.designer.commands;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.commands.Command;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ApplyAttributeSettingCommand extends Command {

	private EObject eObject;
	private String featureName;
	private Object newValue;
	private Object oldValue;
	private EStructuralFeature feature;

	public ApplyAttributeSettingCommand(EObject eObject, String featureName,
			Object newValue) {
		this.eObject = eObject;
		this.featureName = featureName;
		this.newValue = newValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (eObject == null || featureName == null) {
			return false;
		}
		feature = eObject.eClass().getEStructuralFeature(featureName);
		if (feature == null) {
			return false;
		}
		if (eObject.eIsSet(feature)) {
			oldValue = eObject.eGet(feature);
			return oldValue == null ? newValue != null : !oldValue
					.equals(newValue);
		}
		return super.canExecute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (feature.isMany()) {
			List oldValue = (List) eObject.eGet(feature);
			if (newValue == null) {
				return;					
			}
			if (oldValue != null && oldValue.isEmpty()) {
				return;
			}
			if (!(newValue instanceof List) && !oldValue.contains(newValue)) {
				oldValue.add(newValue);
				return;
			}
		}
		eObject.eSet(feature, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (oldValue != null) {
			eObject.eSet(feature, oldValue);
		} else {
			eObject.eUnset(feature);
		}
	}
}
