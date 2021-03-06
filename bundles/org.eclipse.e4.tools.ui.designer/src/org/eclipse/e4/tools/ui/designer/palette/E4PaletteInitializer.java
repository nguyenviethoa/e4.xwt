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
package org.eclipse.e4.tools.ui.designer.palette;

import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.impl.InitializerImpl;
import org.eclipse.e4.xwt.tools.ui.palette.tools.EntryHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4PaletteInitializer extends InitializerImpl {

	private Object creatingObject;

	public boolean initialize(Object element) {
		return false;
	}

	public boolean initialize(Entry entry, Object newObject) {
		if (entry == null) {
			return false;
		}
		if (newObject == null) {
			newObject = EntryHelper.getNewObject(entry);
		}
		if (newObject == null) {
			return false;
		}
		return true;
	}

	public Object parse(Entry entry) {
		EClass type = entry.getType();
		if (type == null) {
			return null;
		}
		boolean useCache = false;
		if (creatingObject != null
				&& type.equals(((EObject) creatingObject).eClass())) {
			useCache = ((EObject) creatingObject).eContainer() == null;
		}
		if (!useCache) {
			creatingObject = EcoreUtil.create(type);
			if (creatingObject instanceof MContribution) {
				((MContribution) creatingObject)
						.setContributionURI("platform:/plugin/org.eclipse.e4.tools.ui.designer/org.eclipse.e4.tools.ui.designer.E4Designer");
			}
		}
		return creatingObject;
	}
}
