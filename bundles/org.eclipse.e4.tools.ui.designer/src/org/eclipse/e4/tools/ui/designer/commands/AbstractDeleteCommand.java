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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.UsageCrossReferencer;
import org.eclipse.gef.commands.Command;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public abstract class AbstractDeleteCommand extends Command {

	protected EObject target;
	private Map<EStructuralFeature.Setting, Object> usageValues = new HashMap<EStructuralFeature.Setting, Object>();

	public AbstractDeleteCommand(EObject target) {
		this.target = target;
	}

	public boolean canExecute() {
		return target != null && target.eContainer() != null;
	}

	public void execute() {
		preExecute();
		doExecute();
	}

	protected abstract void doExecute();

	protected abstract void doUndo();

	public void preExecute() {
		collectSetting(target);
	}

	protected void collectSetting(EObject eObject) {
		EObject rootEObject = EcoreUtil.getRootContainer(target);
		Resource resource = rootEObject.eResource();

		Collection<EStructuralFeature.Setting> usages;
		if (resource == null) {
			usages = UsageCrossReferencer.find(target, rootEObject);
		} else {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet == null) {
				usages = UsageCrossReferencer.find(target, resource);
			} else {
				usages = UsageCrossReferencer.find(target, resourceSet);
			}
		}

		for (EStructuralFeature.Setting setting : usages) {
			if (setting.getEStructuralFeature().isChangeable()) {
				usageValues.put(setting, setting.get(false));
				EcoreUtil.remove(setting, target);
			}
		}

		for (Object element : eObject.eContents()) {
			if (element instanceof EObject) {
				collectSetting((EObject) element);
			}
		}
	}

	public void undo() {
		doUndo();
		postUndo();
	}

	public void postUndo() {
		for (EStructuralFeature.Setting setting : usageValues.keySet()) {
			Object value = usageValues.get(setting);
			setting.set(value);
		}
	}
}
