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
import org.eclipse.e4.tools.ui.designer.parts.E4EditPartsFactory;
import org.eclipse.e4.tools.ui.designer.properties.E4PropertySourceProvider;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropContext;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPalettePage;
import org.eclipse.e4.xwt.tools.ui.palette.tools.PaletteTools;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4Designer extends Designer {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createModelBuilder()
	 */
	protected IModelBuilder createModelBuilder() {
		return new E4ModelBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createEditPartFactory ()
	 */
	protected EditPartFactory createEditPartFactory() {
		return new E4EditPartsFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPages()
	 */
	protected void createPages() {
		super.createPages();
		// TODO: hide source page quickly.
		pageContainer.setWeights(new int[] { 1, 0 });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createVisualsRender()
	 */
	protected IVisualRenderer createVisualsRender() {
		return new E4VisualRenderer(getInputFile(), (MApplication) getDocumentRoot());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#getDropContext()
	 */
	protected DropContext getDropContext() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createPalettePage()
	 */
	protected CustomPalettePage createPalettePage() {
		return PaletteTools.createPalettePage(this, new E4PaletteProvider(), null, null);
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPropertyPage()
	 */
	protected IPropertySheetPage createPropertyPage() {
		PropertySheetPage propertyPage = new PropertySheetPage();
		propertyPage.setPropertySourceProvider(new E4PropertySourceProvider());
		return propertyPage;
	}
}
