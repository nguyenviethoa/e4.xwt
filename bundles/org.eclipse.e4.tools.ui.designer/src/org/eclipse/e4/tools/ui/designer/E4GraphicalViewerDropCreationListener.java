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
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.tools.ui.designer.palette.E4EClassPartInitializer;
import org.eclipse.e4.tools.ui.designer.palette.E4PaletteProvider;
import org.eclipse.e4.tools.ui.designer.palette.E4PartInitializer;
import org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.GraphicalViewerDropCreationListener;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.request.EntryCreationFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4GraphicalViewerDropCreationListener
		extends
			GraphicalViewerDropCreationListener {

	public E4GraphicalViewerDropCreationListener(EditPartViewer viewer) {
		super(viewer);
	}

	protected CreationFactory createCreationFactory(Object selection) {
		if (selection instanceof ICompilationUnit) {
			Class<?> classType = getClassType((ICompilationUnit) selection);
			if (classType != null) {
				Entry entry = E4PaletteProvider
						.createEntry(BasicPackageImpl.Literals.PART);
				entry.setInitializer(new E4PartInitializer());
				entry.setDataContext(classType);
				return new EntryCreationFactory(entry);
			}
		} else if (selection instanceof EClass) {
			Entry entry = E4PaletteProvider
					.createEntry(BasicPackageImpl.Literals.PART);
			entry.setInitializer(new E4EClassPartInitializer());
			entry.setDataContext(selection);
			return new EntryCreationFactory(entry);
		}
		return super.createCreationFactory(selection);
	}
}
