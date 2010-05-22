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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
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

	private CommandStack commandStack;

	private DataCopier dataContextCopier;
	private DataCopier newObjectCopier;

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
		EObject dc = getDataContext();
		if (dc == null) {
			return;
		}
		if (newObject != null) {
			addAdapter();
		} else {
			removeAdapter();
		}
	}

	private void removeAdapter() {
		if (dataContextCopier != null) {
			dataContextCopier.dispose();
		}
		dataContextCopier = null;
		if (newObjectCopier != null) {
			newObjectCopier.dispose();
		}
		newObjectCopier = null;
	}

	private void addAdapter() {
		removeAdapter();
		EObject dc = getDataContext();
		dataContextCopier = new DataCopier(dc, newObject);
		dataContextCopier.setCommandStack(commandStack);
		newObjectCopier = new DataCopier(newObject, dc) {
			protected void copy() {
				BindingContext bc = getBindingContext();
				List<Binding> bindings = null;
				if (bc != null) {
					bindings = new ArrayList<Binding>(bc.getBindings());
					for (Binding binding : bindings) {
						bc.removeBinding(binding);
					}
				}
				super.copy();
				if (bc != null && bindings != null) {
					for (Binding binding : bindings) {
						bc.addBinding(binding);
					}
				}
			};
		};
		newObjectCopier.copy();
	}

	public void dispose() {
		removeAdapter();
		super.dispose();
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

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}
}
