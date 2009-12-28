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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.tools.ui.designer.palette.E4PaletteProvider;
import org.eclipse.e4.tools.ui.designer.parts.E4EditPartsFactory;
import org.eclipse.e4.tools.ui.designer.properties.E4PropertySourceProvider;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IModelBuilder;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.IVisualRenderer;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.dnd.DropContext;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.ContentOutlinePage;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.OutlineLableProvider;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPalettePage;
import org.eclipse.e4.xwt.tools.ui.palette.tools.PaletteTools;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4Designer extends Designer {

	private boolean isDirty = false;
	private E4UIRenderer uiRenderer = new E4UIRenderer();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createModelBuilder()
	 */
	protected IModelBuilder createModelBuilder() {
		return uiRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createEditPartFactory
	 * ()
	 */
	protected EditPartFactory createEditPartFactory() {
		return new E4EditPartsFactory();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		EditDomain domain = getEditDomain();
		domain.setDefaultTool(new E4SelectionTool());
		domain.loadDefaultTool();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	protected void performModelChanged(Notification event) {
		super.performModelChanged(event);
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPages()
	 */
	protected void createPages() {
		super.createPages();
		// TODO: hide source page quickly.
		pageContainer.setWeights(new int[] { 1, 0 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createVisualsRender()
	 */
	protected IVisualRenderer createVisualsRender() {
		return uiRenderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.core.Designer#getDropContext()
	 */
	protected DropContext getDropContext() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.Designer#createPalettePage()
	 */
	protected CustomPalettePage createPalettePage() {
		return PaletteTools.createPalettePage(this, new E4PaletteProvider(),
				null, E4SelectionTool.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createPropertyPage
	 * ()
	 */
	protected IPropertySheetPage createPropertyPage() {
		PropertySheetPage propertyPage = new PropertySheetPage();
		propertyPage.setPropertySourceProvider(new E4PropertySourceProvider());
		return propertyPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.tools.ui.designer.core.editor.Designer#createOutlinePage
	 * ()
	 */
	protected ContentOutlinePage createOutlinePage() {
		E4DesignerOutlineContentProvider contentProvider = new E4DesignerOutlineContentProvider();
		OutlineLableProvider lableProvider = new OutlineLableProvider();
		ContentOutlinePage outlinePage = new ContentOutlinePage(this,
				contentProvider, lableProvider);
		outlinePage.setContextMenuProvider(getContextMenuProvider());
		return outlinePage;
	}
}