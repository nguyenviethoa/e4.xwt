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
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.tools.ui.designer.palette.E4PaletteProvider;
import org.eclipse.e4.ui.model.application.MApplicationPackage;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.GraphicalViewerDropCreationListener;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.request.EntryCreationFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4GraphicalViewerDropCreationListener extends
		GraphicalViewerDropCreationListener {

	public E4GraphicalViewerDropCreationListener(EditPartViewer viewer) {
		super(viewer);
	}

	protected CreationFactory createCreationFactory(Object selection) {
		if (selection instanceof ICompilationUnit) {
			Class<?> classType = getClassType((ICompilationUnit) selection);
			if (classType != null) {
				Entry entry = E4PaletteProvider
						.createEntry(MApplicationPackage.Literals.PART);
				entry.setDataContext(classType);
				return new EntryCreationFactory(entry);
			}
		}
		return super.createCreationFactory(selection);
	}
}
