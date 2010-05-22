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
package org.eclipse.e4.tools.ui.dataform;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.e4.tools.ui.designer.commands.ApplyAttributeSettingCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.commands.CommandStack;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class DataCopier extends AdapterImpl {

	public static final String DATA_COPIER_KEY = "datacopier";

	private EObject fromObj;
	private EObject toObj;

	private CommandStack commandStack;
	private boolean setup = true;

	public DataCopier(EObject fromObj, EObject toObj) {
		this.fromObj = fromObj;
		this.toObj = toObj;
		Assert.isNotNull(fromObj);
		Assert.isNotNull(toObj);
		Assert.isTrue(hasSameType(fromObj, toObj));
		adapt();
	}

	private void adapt() {
		Adapter a = getCopier(fromObj);
		boolean hasAdapter = a != null && a instanceof DataCopier;
		if (hasAdapter) {
			hasAdapter = equals(toObj, ((DataCopier) a).toObj);
		}
		if (!hasAdapter) {
			fromObj.eAdapters().add(this);
		}
	}

	private DataCopier getCopier(EObject eObject) {
		if (eObject == null) {
			return null;
		}
		return (DataCopier) EcoreUtil
				.getExistingAdapter(toObj, DATA_COPIER_KEY);
	}

	public void notifyChanged(Notification msg) {
		if (msg.isTouch() || !isSetup()) {
			return;
		}
		Object notifier = msg.getNotifier();
		if (fromObj != notifier) {
			return;
		}
		EStructuralFeature feature = (EStructuralFeature) msg.getFeature();
		Object newValue = msg.getNewValue();
		if (feature != null) {
			applyNewValue(toObj, feature, newValue);
		}
	}

	protected void copy() {
		EClass eType = fromObj.eClass();
		EObject copy = EcoreUtil.copy(fromObj);
		EList<EStructuralFeature> features = eType.getEAllStructuralFeatures();
		DataCopier toCopier = getCopier(toObj);
		if (toCopier != null) {
			toCopier.setSetup(false);
		}
		for (EStructuralFeature sf : features) {
			if (copy.eIsSet(sf)) {
				Object featureValue = copy.eGet(sf);
				toObj.eSet(sf, featureValue);
			} else if (toObj.eIsSet(sf)) {
				toObj.eUnset(sf);
			}
		}
		if (toCopier != null) {
			toCopier.setSetup(true);
		}
	}

	public boolean isAdapterForType(Object type) {
		return DATA_COPIER_KEY.equals(type);
	}

	protected void applyNewValue(EObject eObj, EStructuralFeature sf,
			Object newValue) {
		if (eObj == null || sf == null) {
			return;
		}
		if (commandStack != null) {
			ApplyAttributeSettingCommand cmd = new ApplyAttributeSettingCommand(
					eObj, sf.getName(), newValue);
			commandStack.execute(cmd);
		} else {
			// Prevent StackOverFlow
			if (sf.isMany()) {
				List oldValue = (List) eObj.eGet(sf);
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
			eObj.eSet(sf, newValue);
		}
	}

	protected boolean hasSameType(EObject o1, EObject o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		return equals(o1.eClass(), o2.eClass());
	}

	protected boolean equals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	public void dispose() {
		fromObj.eAdapters().remove(this);
		fromObj = null;
		toObj = null;
	}

	public void setSetup(boolean setup) {
		this.setup = setup;
	}

	public boolean isSetup() {
		return setup;
	}
}
