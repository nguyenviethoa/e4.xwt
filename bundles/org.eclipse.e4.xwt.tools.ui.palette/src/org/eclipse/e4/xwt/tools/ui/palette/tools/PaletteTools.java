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
package org.eclipse.e4.xwt.tools.ui.palette.tools;

import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPalettePage;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPaletteViewerProvider;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.ExtensionRegistry;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.PaletteResourceProvider;
import org.eclipse.e4.xwt.tools.ui.palette.root.PaletteRootFactory;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.ui.IEditorPart;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class PaletteTools {

	public static CustomPalettePage createPalettePage(IEditorPart editorPart, PaletteResourceProvider resourceProvider, Class<? extends Tool> creationToolClass, Class<? extends Tool> selectionToolClass) {
		EditDomain editDomain = (EditDomain) editorPart.getAdapter(EditDomain.class);
		if (editDomain == null) {
			editDomain = new DefaultEditDomain(editorPart);
		}
		if (resourceProvider == null) {
			resourceProvider = ExtensionRegistry.loadFromExtensions(editorPart);
		}
		PaletteRoot paletteRoot = createPaletteRoot(resourceProvider, creationToolClass, selectionToolClass);
		if (paletteRoot != null) {
			editDomain.setPaletteRoot(paletteRoot);
		}
		CustomPaletteViewerProvider provider = new CustomPaletteViewerProvider(editDomain);
		return new CustomPalettePage(provider);
	}

	private static PaletteRoot createPaletteRoot(PaletteResourceProvider resourceProvider, Class<? extends Tool> createToolClass, Class<? extends Tool> selectionToolClass) {
		PaletteRootFactory factory = new PaletteRootFactory(resourceProvider, createToolClass, selectionToolClass);
		return factory.createPaletteRoot();
	}
}
