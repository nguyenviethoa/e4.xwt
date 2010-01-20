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

import org.eclipse.e4.ui.model.application.MApplicationPackage;
import org.eclipse.e4.ui.model.application.MMenu;
import org.eclipse.e4.ui.model.application.MMenuItem;
import org.eclipse.e4.ui.model.application.MToolBar;
import org.eclipse.e4.ui.model.application.MToolItem;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.emf.ecore.EClass;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 */
public class ApplicationModelHelper {

	public static boolean canAddedChild(Entry entry, MUIElement target) {
		EClass eClass = (EClass) entry.getType();
		if ((eClass == MApplicationPackage.eINSTANCE.getMenu())
				&& (target instanceof MMenu)) {
			return false;
		}

		if ((eClass == MApplicationPackage.eINSTANCE.getMenu())
				&& !(target instanceof MWindow)) {
			return false;
		}

		if ((eClass == MApplicationPackage.eINSTANCE.getMenuItem())
				&& !(target instanceof MMenu)) {
			return false;
		}

		if ((eClass == MApplicationPackage.eINSTANCE.getToolItem())
				&& !(target instanceof MToolBar)) {
			return false;
		}
		return true;
	}

	public static boolean canAddedChild(MUIElement element, MUIElement target) {
		if (element instanceof MMenu && target instanceof MMenu) {
			return false;
		}

		if (element instanceof MMenu && !(target instanceof MWindow)) {
			return false;
		}

		if (element instanceof MMenuItem && !(target instanceof MMenu)) {
			return false;
		}

		if (element instanceof MToolItem && !(target instanceof MToolBar)) {
			return false;
		}
		return true;
	}
}
