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
package org.eclipse.e4.tools.ui.designer.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.designer.commands.CommandFactory;
import org.eclipse.e4.tools.ui.designer.dialogs.ElementInitializeDialog;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.node.CategoryNode;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewChildAction extends Action implements IMenuCreator {

	public static final String ID = NewChildAction.class.getName();
	private EditPart editPart;
	private EObject parent;
	private Set<EClass> creatingTypes = new HashSet<EClass>();
	private Map<EObject, MenuManager> menus = new HashMap<EObject, MenuManager>(
			1);

	private IProject project;

	public NewChildAction(IProject project, EditPart editPart) {
		super("New", AS_DROP_DOWN_MENU);
		this.project = project;
		this.editPart = editPart;
		setId(ID);
		setMenuCreator(this);
		setEnabled(calculateEnabled());
	}

	private MenuManager getDropDownMenus() {
		MenuManager menuManager = menus.get(parent);
		if (parent != null && menuManager == null) {
			menuManager = new MenuManager("#menu");
			makeActions(menuManager);
			menus.put(parent, menuManager);
		}
		return menuManager;
	}

	private void makeActions(MenuManager menuManager) {
		boolean addSep = false;
		for (EClass childType : creatingTypes) {
			if (childType.isAbstract() || childType.isInterface()) {
				continue;
			}
			menuManager.add(new CreateChildAction(parent, childType, -1));
			addSep = true;
		}
		int index = -1;
		EObject newContainer = parent.eContainer();
		EStructuralFeature eContainingFeature = parent.eContainmentFeature();
		if (eContainingFeature != null && eContainingFeature.isMany()) {
			if (addSep) {
				menuManager.add(new Separator());
			}
			List featureValue = (List) newContainer.eGet(eContainingFeature);
			index = featureValue.indexOf(parent);
			menuManager.add(new CreateChildAction(newContainer,
					parent.eClass(), index));
		}
	}

	public Menu getMenu(Control parent) {
		MenuManager mm = getDropDownMenus();
		if (mm != null) {
			return mm.createContextMenu(parent);
		}
		return null;
	}

	public Menu getMenu(Menu parent) {
		MenuManager mm = getDropDownMenus();
		if (mm != null) {
			Menu menu = new Menu(parent);
			IContributionItem[] items = mm.getItems();
			for (int i = 0; i < items.length; i++) {
				IContributionItem item = items[i];
				IContributionItem newItem = item;
				if (item instanceof ActionContributionItem) {
					newItem = new ActionContributionItem(
							((ActionContributionItem) item).getAction());
				}
				newItem.fill(menu, -1);
			}
			return menu;
		}
		return null;
	}

	public void dispose() {
		Collection<MenuManager> values = menus.values();
		for (MenuManager mm : values) {
			mm.dispose();
		}
		menus.clear();
	}

	protected boolean calculateEnabled() {
		if (editPart == null) {
			return false;
		}
		Object model = editPart.getModel();
		if (model instanceof EObject) {
			parent = (EObject) model;
		}
		if (parent == null) {
			return false;
		}
		if (parent instanceof CategoryNode) {
			CategoryNode categoryNode = (CategoryNode) parent;
			parent = categoryNode.getObject();
			EReference reference = categoryNode.getReference();
			if (parent == null || reference == null
					|| !(reference.getEType() instanceof EClass)) {
				return false;
			}
			creatingTypes.add((EClass) reference.getEType());
		} else {
			creatingTypes.addAll(ApplicationModelHelper
					.getAccessibleChildren(parent.eClass(), false));
			EList<EReference> references = parent.eClass().getEAllReferences();
			for (EReference ref : references) {
				if (!ref.isContainment() || !(ref.getEType() instanceof EClass)) {
					continue;
				}
				creatingTypes.add((EClass) ref.getEType());
			}
		}
		return true;
	}

	public void execute(Command command) {
		if (command == null || !command.canExecute()) {
			return;
		}
		EditDomain editDomain = EditDomain.getEditDomain(editPart);
		editDomain.getCommandStack().execute(command);
	}

	class CreateChildAction extends Action {

		private EObject parent;
		private EClass childType;
		private int index = -1;

		public CreateChildAction(EObject parent, EClass childType, int index) {
			this.childType = childType;
			this.parent = parent;
			this.index = index;
			setText(childType.getName());
			Image image = ApplicationModelHelper.getImage(EcoreUtil
					.create(childType));
			if (image != null) {
				setImageDescriptor(ImageDescriptor.createFromImage(image));
			}
		}

		public void run() {
			EObject eObj = EcoreUtil.create(childType);
			ElementInitializeDialog dialog = new ElementInitializeDialog(
					new Shell(), project, parent, eObj);
			if (dialog.open() == Window.OK) {
				Command command = CommandFactory.createAddChildCommand(parent,
						eObj, index);
				execute(command);
			}
		}
	}

}
