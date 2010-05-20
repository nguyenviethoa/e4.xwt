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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.dataform.workbench.events.EventFactory;
import org.eclipse.e4.xwt.databinding.BindingContext;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class AbstractDataForm extends Composite {

	private EObject container;
	private EObject dataContext;
	private EObject newObject;

	private IProject project;
	private BindingContext bindingContext;

	private Adapter copier;

	public AbstractDataForm(Composite parent, int style) {
		super(parent, style);
	}

	public void setContainer(EObject container) {
		this.container = container;
	}

	public EObject getContainer() {
		return container;
	}

	public void setNewObject(EObject newObject) {
		this.newObject = newObject;
		if (newObject != null) {
			removeAdapter();
			copy(newObject, getDataContext());
			addAdapter();
		} else {
			removeAdapter();
		}
	}

	private void copy(EObject from, EObject to) {
		if (!hasSameType(from, to)) {
			return;
		}
		BindingContext bc = getBindingContext();
		List<Binding> bindings = null;
		if (bc != null) {
			bindings = new ArrayList<Binding>(bc.getBindings());
			for (Binding binding : bindings) {
				bc.removeBinding(binding);
			}
		}
		EClass eType = to.eClass();
		EObject copy = EcoreUtil.copy(from);
		EList<EStructuralFeature> features = eType.getEAllStructuralFeatures();
		for (EStructuralFeature sf : features) {
			if (copy.eIsSet(sf)) {
				Object featureValue = copy.eGet(sf);
				to.eSet(sf, featureValue);
			} else if (to.eIsSet(sf)) {
				to.eUnset(sf);
			}
		}
		if (bc != null && bindings != null) {
			for (Binding binding : bindings) {
				bc.addBinding(binding);
			}
		}
	}

	private void removeAdapter() {
		if (getDataContext() != null) {
			dataContext.eAdapters().remove(copier);
		}
	}

	private void addAdapter() {
		EObject dc = getDataContext();
		if (dc == null) {
			return;
		}
		if (copier == null) {
			copier = new AdapterImpl() {
				public void notifyChanged(Notification msg) {
					handleValueChanged(msg);
				}
			};
		}
		if (!dc.eAdapters().contains(copier)) {
			dc.eAdapters().add(copier);
		}
	}

	private void handleValueChanged(Notification msg) {
		if (msg.isTouch() || !hasSameType(newObject, dataContext)) {
			return;
		}
		EStructuralFeature feature = (EStructuralFeature) msg.getFeature();
		Object newValue = msg.getNewValue();
		if (feature != null) {
			newObject.eSet(feature, newValue);
		}
	}

	protected boolean hasSameType(EObject o1, EObject o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		return equals(o1.eClass(), o2.eClass());
	}

	public void dispose() {
		if (dataContext != null) {
			dataContext.eAdapters().remove(copier);
		}
		super.dispose();
	}

	protected boolean equals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public EObject getDataContext() {
		if (dataContext == null) {
			dataContext = (EObject) UserData.getDataContext(this);
		}
		return dataContext;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	protected void handleEvent(String featureName) {
		EventFactory.handleEvent(this, getProject(), getContainer(),
				getDataContext(), featureName);
	}

	public void setBindingContext(BindingContext bindingContext) {
		this.bindingContext = bindingContext;
	}

	public BindingContext getBindingContext() {
		return bindingContext;
	}
}
