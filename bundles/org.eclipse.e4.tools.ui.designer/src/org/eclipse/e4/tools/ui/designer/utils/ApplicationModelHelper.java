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
package org.eclipse.e4.tools.ui.designer.utils;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 */
public class ApplicationModelHelper {

	public static boolean isLive(Object element) {
		if (!(element instanceof EObject)) {
			return false;
		}
		EObject eObject = (EObject) element;
		return (eObject != null && eObject.eResource() != null);
	}

	public static boolean canAddedChild(EClass eClass, MUIElement target) {
		// EClass eClass = (EClass) entry.getType();
		EClass toolBarClass = MenuPackageImpl.eINSTANCE.getToolBar();
		EClass menuClass = MenuPackageImpl.eINSTANCE.getMenu();
		if (target instanceof MPart
				&& !((toolBarClass.isSuperTypeOf(eClass) || toolBarClass == eClass) || (menuClass
						.isSuperTypeOf(eClass) || menuClass == eClass))) {
			return false;
		}

		if ((eClass == menuClass) && (target instanceof MMenu)) {
			return false;
		}

		if ((eClass == toolBarClass) && (target instanceof MToolBar)) {
			return false;
		}

		if ((eClass == menuClass) && (!(target instanceof MWindow || target instanceof MPart))) {
			return false;
		}

		// accept only MMenuItem by MMenu
		if ((eClass == MenuPackageImpl.eINSTANCE.getMenuItem()) && !(target instanceof MMenu)) {
			return false;
		}

		// accept only MToolItem by MToolBar
		if ((eClass == MenuPackageImpl.eINSTANCE.getToolItem())
				&& !(target instanceof MToolBar)) {
			return false;
		}

		// accept only MPerspective by MPerspectiveStack
		if (eClass == AdvancedPackageImpl.eINSTANCE.getPerspective()
				&& !(target instanceof MPerspectiveStack)) {
			return false;
		}
		return true;
	}

	public static boolean canAddedChild(MUIElement element, MUIElement target) {
		if (target instanceof MPart
				&& !((element instanceof MToolBar) || (element instanceof MMenu))) {
			return false;
		}

		if (element instanceof MToolItem && target instanceof MToolBar) {
			return true;
		}

		if (element instanceof MMenuItem && target instanceof MMenu) {
			return true;
		}

		if (element instanceof MMenu && target instanceof MMenu) {
			return false;
		}

		if (element instanceof MMenu && !(target instanceof MWindow || target instanceof MPart)) {
			return false;
		}

		// accept only MMenuItem by MMenu
		if (element instanceof MMenuItem && !(target instanceof MMenu)) {
			return false;
		}

		// accept only MToolItem by MToolBar
		if (element instanceof MToolItem && !(target instanceof MToolBar)) {
			return false;
		}

		// accept only MPerspective by MPerspectiveStack
		if (element instanceof MPerspective && !(target instanceof MPerspectiveStack)) {
			return false;
		}
		return true;
	}
}
